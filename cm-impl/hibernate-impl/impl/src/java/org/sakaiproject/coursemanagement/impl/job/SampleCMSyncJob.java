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
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;

/**
 * A sample quartz job to synchronize the CM data in Sakai's hibernate impl with an
 * external data source.  In this case, we're using an xml file as the external source.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class SampleCMSyncJob implements Job {
	private static final Log log = LogFactory.getLog(SampleCMSyncJob.class);
	
	protected CourseManagementService cmService;
	protected CourseManagementAdministration cmAdmin;
	protected String classPathToXml;

	Document doc;
	
	/**
	 * Load the xml file in init() so the right classloader is used
	 */
	public void init() {
		if(log.isInfoEnabled()) log.info("init()");
		doc = loadDataFromXml();
	}
	
	public void destroy() {
		if(log.isInfoEnabled()) log.info("destroy()");
	}
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		loginToSakai();
		reconcileAcademicSessions(doc);
	}

	private void loginToSakai() {
		if(log.isDebugEnabled()) log.debug("Logging in to Sakai");

	    Session sakaiSession = SessionManager.getCurrentSession();
		sakaiSession.setUserId("admin");
		sakaiSession.setUserEid("admin");

		// establish the user's session
		UsageSessionService.startSession("admin", "127.0.0.1", "RosterSync");
		
		// update the user's externally provided realm definitions
		AuthzGroupService.refreshUser("admin");

		// post the login event
		EventTrackingService.post(EventTrackingService.newEvent(UsageSessionService.EVENT_LOGIN, null, true));
	}

	public void logoutFromSakai() {
		// post the logout event
		EventTrackingService.post(EventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true));
	}
	
	/** This is public for testing purposes */
	public Document loadDataFromXml() {
		SAXBuilder builder = new SAXBuilder();
		InputStream in = getClass().getClassLoader().getResourceAsStream(classPathToXml);
		try {
			return builder.build(in);
		} catch (Exception e) {
			log.warn("Unable to find xml document at " + classPathToXml + ": " + e);
			return null;
		}
	}
	
	public void reconcileAcademicSessions(Document doc) {
		synchronized (doc) {
			if(log.isDebugEnabled()) log.debug("Reconciling AcademicSessions");
			List existing = cmService.getAcademicSessions();

			// Create a map of existing AcademicSession EIDs to AcademicSessions
			Map academicSessionMap = new HashMap();
			for(Iterator iter = existing.iterator(); iter.hasNext();) {
				AcademicSession as = (AcademicSession)iter.next();
				academicSessionMap.put(as.getEid(), as);
			}
			
			// Find the academic sessions spepcified in the xml doc
			try {
				XPath docsPath = XPath.newInstance("/cm-data/academic-sessions/academic-session");
				List items = docsPath.selectNodes(doc);
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
		}
	}


	private void addAcademicSession(Element element) {
		String eid = element.getChildText("eid");
		if(log.isDebugEnabled()) log.debug("Adding AcademicSession + " + eid);
		String title = element.getChildText("title");
		String description = element.getChildText("description");
		Date startDate = getDate(element.getChildText("start-date"));
		Date endDate = getDate(element.getChildText("end-date"));
		cmAdmin.createAcademicSession(eid, title, description, startDate, endDate);
	}
	
	private void updateAcademicSession(AcademicSession session, Element element) {
		if(log.isDebugEnabled()) log.debug("Updating AcademicSession + " + session.getEid());
		session.setTitle(element.getChildText("title"));
		session.setDescription(element.getChildText("description"));
		session.setStartDate(getDate(element.getChildText("start-date")));
		session.setEndDate(getDate(element.getChildText("end-date")));
		cmAdmin.updateAcademicSession(session);
	}

	private Date getDate(String str) {
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

	public void setClassPathToXml(String classPathToXml) {
		this.classPathToXml = classPathToXml;
	}

}
