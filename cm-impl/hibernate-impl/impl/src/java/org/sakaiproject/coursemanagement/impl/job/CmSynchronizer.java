/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.coursemanagement.impl.job;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

/**
 * Synchronizes the state of the local CourseManagementService with an external
 * data source.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public abstract class CmSynchronizer {
	private static final Log log = LogFactory.getLog(CmSynchronizer.class);

	protected CourseManagementService cmService;
	protected CourseManagementAdministration cmAdmin;
	protected abstract InputStream getXmlInputStream();

	public synchronized void syncAllCmObjects() {
		long start = System.currentTimeMillis();
		if(log.isInfoEnabled()) log.info("Starting CM synchronization");
		// Load the xml document from the xml stream
		InputStream in = null;
		Document doc = null;
		try {
			in = getXmlInputStream();
			doc = new SAXBuilder().build(in);
		} catch (Exception e) {
			log.error("Could not build a jdom document from the xml input stream... " + e);
			// Close the input stream
			if(in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					log.error("Unable to close input stream " + in);
				}
			}
			throw new RuntimeException(e);
		}

		try {
			reconcileAcademicSessions(doc);
			reconcileCanonicalCourses(doc);
			reconcileCourseOfferings(doc);
			reconcileSections(doc);
			reconcileEnrollmentSets(doc);
			reconcileCourseSets(doc);
		} finally {
			// Close the input stream
			if(in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					log.error("Unable to close input stream " + in);
				}
			}
		}

		if(log.isInfoEnabled()) log.info("Finished CM synchronization in " + (System.currentTimeMillis()-start) + " ms");
	}

	protected void loginToSakai() {
	    Session sakaiSession = SessionManager.getCurrentSession();
		sakaiSession.setUserId("admin");
		sakaiSession.setUserEid("admin");

		// establish the user's session
		UsageSessionService.startSession("admin", "127.0.0.1", "CMSync");
		
		// update the user's externally provided realm definitions
		AuthzGroupService.refreshUser("admin");

		// post the login event
		EventTrackingService.post(EventTrackingService.newEvent(UsageSessionService.EVENT_LOGIN, null, true));
	}

	protected void logoutFromSakai() {
		// post the logout event
		EventTrackingService.post(EventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true));
	}

	protected void reconcileAcademicSessions(Document doc) {
		long start = System.currentTimeMillis();
		if(log.isInfoEnabled()) log.info("Reconciling AcademicSessions");

		// Get a list of all existing academic sessions
		List existing = cmService.getAcademicSessions();
		
		// Create a map of existing AcademicSession EIDs to AcademicSessions
		Map academicSessionMap = new HashMap();
		for(Iterator iter = existing.iterator(); iter.hasNext();) {
			AcademicSession as = (AcademicSession)iter.next();
			academicSessionMap.put(as.getEid(), as);
		}

		// Find the academic sessions specified in the xml doc and reconcile them
		try {
			XPath docsPath = XPath.newInstance("/cm-data/academic-sessions/academic-session");
			List items = docsPath.selectNodes(doc);
			// Add or update each of the academic sessions specified in the xml
			for(Iterator iter = items.iterator(); iter.hasNext();) {
				Element element = (Element)iter.next();
				String eid = element.getChildText("eid");
				if(log.isDebugEnabled()) log.debug("Found academic section to reconcile: " + eid);
				if(academicSessionMap.containsKey(eid)) {
					updateAcademicSession((AcademicSession)academicSessionMap.get(eid), element);
				} else {
					addAcademicSession(element);
				}
			}
		} catch (JDOMException jde) {
			log.error(jde);
		}
		
		if(log.isInfoEnabled()) log.info("Finished reconciling AcademicSessions in " + (System.currentTimeMillis()-start) + " ms");
	}
	
	protected void addAcademicSession(Element element) {
		String eid = element.getChildText("eid");
		if(log.isDebugEnabled()) log.debug("Adding AcademicSession + " + eid);
		String title = element.getChildText("title");
		String description = element.getChildText("description");
		Date startDate = getDate(element.getChildText("start-date"));
		Date endDate = getDate(element.getChildText("end-date"));
		cmAdmin.createAcademicSession(eid, title, description, startDate, endDate);
	}
	
	protected void updateAcademicSession(AcademicSession session, Element element) {
		if(log.isDebugEnabled()) log.debug("Updating AcademicSession + " + session.getEid());
		session.setTitle(element.getChildText("title"));
		session.setDescription(element.getChildText("description"));
		session.setStartDate(getDate(element.getChildText("start-date")));
		session.setEndDate(getDate(element.getChildText("end-date")));
		cmAdmin.updateAcademicSession(session);
	}
	
	protected void reconcileCanonicalCourses(Document doc) {
		long start = System.currentTimeMillis();
		if(log.isInfoEnabled()) log.info("Reconciling AcademicSessions");
		
		try {
			XPath docsPath = XPath.newInstance("/cm-data/canonical-courses/canonical-course");
			List items = docsPath.selectNodes(doc);
			// Add or update each of the canonical courses specified in the xml
			for(Iterator iter = items.iterator(); iter.hasNext();) {
				Element element = (Element)iter.next();
				String eid = element.getChildText("eid");
				if(log.isDebugEnabled()) log.debug("Found canonical course to reconcile: " + eid);
				CanonicalCourse existing;
				try {
					existing = cmService.getCanonicalCourse(eid);
					updateCanonicalCourse(existing, element);
				} catch (IdNotFoundException idex) {
					addCanonicalCourse(element);
				}
			}
		} catch (JDOMException jde) {
			log.error(jde);
		}
		
		if(log.isInfoEnabled()) log.info("Finished reconciling AcademicSessions in " + (System.currentTimeMillis()-start) + " ms");
	}
	
	protected void addCanonicalCourse(Element element) {
		String eid = element.getChildText("eid");
		if(log.isDebugEnabled()) log.debug("Adding CanonicalCourse + " + eid);
		String title = element.getChildText("title");
		String description = element.getChildText("description");
		cmAdmin.createCanonicalCourse(eid, title, description);
	}

	protected void updateCanonicalCourse(CanonicalCourse canonicalCourse, Element element) {
		if(log.isDebugEnabled()) log.debug("Updating CanonicalCourse + " + canonicalCourse.getEid());
		canonicalCourse.setTitle(element.getChildText("title"));
		canonicalCourse.setDescription(element.getChildText("description"));
		cmAdmin.updateCanonicalCourse(canonicalCourse);
	}

	protected void reconcileCourseOfferings(Document doc) {
		// TODO Reconcile course offerings
	}

	protected void reconcileSections(Document doc) {
		// TODO Reconcile sections
	}

	protected void reconcileEnrollmentSets(Document doc) {
		// TODO Reconcile enrollment sets
	}

	protected void reconcileCourseSets(Document doc) {
		// TODO Reconcile course sets
	}

	protected Date getDate(String str) {
		SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy");
		try {
			return df.parse(str);
		} catch (ParseException pe) {
			log.warn("Invalid date: " + str);
			return null;
		}
	}
	
	// Dependency Injection

	public void setCmAdmin(CourseManagementAdministration cmAdmin) {
		this.cmAdmin = cmAdmin;
	}

	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}
}
