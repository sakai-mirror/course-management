/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2007 The Sakai Foundation.
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
package org.sakaiproject.coursemanagement.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.cover.AuthzGroupService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.SectionCategory;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.cover.SessionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class SampleDataLoader implements BeanFactoryAware {
	private static final Log log = LogFactory.getLog(SampleDataLoader.class);

	// Begin Dependency Injection //
	protected CourseManagementAdministration cmAdmin;
	public void setCmAdmin(CourseManagementAdministration cmAdmin) {
		this.cmAdmin = cmAdmin;
	}

	protected ServerConfigurationService scs;
	public void setScs(ServerConfigurationService scs) {
		this.scs = scs;
	}
	
	protected CourseManagementService cmService;
	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}

	protected BeanFactory beanFactory;
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	/** A flag for disabling the sample data load */
	protected boolean loadSampleData;
	public void setLoadSampleData(boolean loadSampleData) {
		this.loadSampleData = loadSampleData;
	}
	// End Dependency Injection //

	public void init() {
		log.info("Initializing " + getClass().getName());
		if(cmAdmin == null) {
			return;
		}
		if(loadSampleData) {
			loginToSakai();
			PlatformTransactionManager tm = (PlatformTransactionManager)beanFactory.getBean("org.sakaiproject.springframework.orm.hibernate.GlobalTransactionManager");
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			TransactionStatus status = tm.getTransaction(def);
			try {
				load();
			} catch (Exception e) {
				log.error("Unable to load CM data", e);
				tm.rollback(status);
			} finally {
				if(!status.isCompleted()) {
					tm.commit(status);
				}
			}
			logoutFromSakai();
		}
	}
	
	public void destroy() {
		log.info("Destroying " + getClass().getName());
	}
	
	private void loginToSakai() {
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

	private void logoutFromSakai() {
	    Session sakaiSession = SessionManager.getCurrentSession();
		sakaiSession.invalidate();
		
		// post the logout event
		EventTrackingService.post(EventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true));
	}

	public void load() throws Exception {
		String[] legacyTerms = scs.getStrings("termterm");
		String[] legacyYears = scs.getStrings("termyear");
		String[] legacyStartTimes = scs.getStrings("termstarttime");		
		String[] legacyEndTimes = scs.getStrings("termendtime");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		
		// Don't do anything if we've got data already.  The existence of an
		// AcademicSession for the first legacy term will be our indicator for existing
		// data.
		try {
			cmService.getAcademicSession(StringUtils.trim(legacyTerms[0]) + " " + StringUtils.trim(legacyYears[0]));
			if(log.isInfoEnabled()) log.info("CM data exists, skipping data load.");
			return;
		} catch (IdNotFoundException ide) {
			if(log.isInfoEnabled()) log.info("Starting sample CM data load");
		}

		// Academic Sessions
		List<AcademicSession> academicSessions = new ArrayList<AcademicSession>();
		for(int i = 0; i < legacyTerms.length; i++) {
			String termId =StringUtils.trim(StringUtils.trim(legacyTerms[i]) + " " + StringUtils.trim(legacyYears[i])); // See SAK-8299
			Date startDate = sdf.parse(StringUtils.trim(legacyStartTimes[i]));
			Date endDate = sdf.parse(StringUtils.trim(legacyEndTimes[i]));
			academicSessions.add(cmAdmin.createAcademicSession(termId,termId,
					termId, startDate, endDate));
		}

		// Section Categories (these are returned in alpha order, so we can control the order here)
		SectionCategory lectureCategory = cmAdmin.addSectionCategory("01.lct", "Lecture");
		SectionCategory discussionCategory = cmAdmin.addSectionCategory("03.dsc", "Discussion");
		cmAdmin.addSectionCategory("02.lab", "Lab");
		cmAdmin.addSectionCategory("04.rec", "Recitation");
		cmAdmin.addSectionCategory("05.sto", "Studio");

		// Course Sets
		cmAdmin.createCourseSet("SMPL", "Sample Department",
				"We study wet things in the Sample Dept", "DEPT", null);
		cmAdmin.addOrUpdateCourseSetMembership("da1","DeptAdmin", "SMPL", "active");
		
		// Cross-listed Canonical Courses
		Set<CanonicalCourse> cc = new HashSet<CanonicalCourse>();
		cc.add(cmAdmin.createCanonicalCourse("SMPL101", "Sample 101", "A survey of samples"));
		cc.add(cmAdmin.createCanonicalCourse("SMPL202", "Sample 202", "An in depth study of samples"));
		cmAdmin.setEquivalentCanonicalCourses(cc);
		
		// Only work with the last two academic sessions, since the data load is so slow
		int startIndex = 0;
		if(academicSessions.size() > 2) {
			startIndex = academicSessions.size() - 2;
		}
		for(int i = startIndex; i < academicSessions.size(); i++) {
			AcademicSession as = academicSessions.get(i);
			CourseOffering co1 = cmAdmin.createCourseOffering("SMPL101 "+ as.getEid(),
					"SMPL 101", "Sample course offering #1, " + as.getEid(), "open", as.getEid(),
					"SMPL101", as.getStartDate(), as.getEndDate());
			CourseOffering co2 = cmAdmin.createCourseOffering("SMPL202 "+ as.getEid(),
					"SMPL 202", "Sample course offering #2, " + as.getEid(), "open", as.getEid(),
					"SMPL202", as.getStartDate(), as.getEndDate());

			Set<CourseOffering> courseOfferingSet = new HashSet<CourseOffering>();
			courseOfferingSet.add(co1);
			courseOfferingSet.add(co2);
			
			// Cross list these course offerings
			cmAdmin.setEquivalentCourseOfferings(courseOfferingSet);

			cmAdmin.addCourseOfferingToCourseSet("SMPL", co1.getEid());
			cmAdmin.addCourseOfferingToCourseSet("SMPL", co2.getEid());
						
			// And add some other instructors at the offering level (this should help with testing cross listings)
			cmAdmin.addOrUpdateCourseOfferingMembership("instructor1","I", co1.getEid(), null);
			cmAdmin.addOrUpdateCourseOfferingMembership("instructor2","I", co2.getEid(), null);
		}
		
		Map<String, String> enrollmentStatuses = cmService.getEnrollmentStatusDescriptions(Locale.US);
		Map<String, String> gradingSchemes = cmService.getGradingSchemeDescriptions(Locale.US);

		List<String> enrollmentEntries = new ArrayList<String>(enrollmentStatuses.keySet());
		List<String> gradingEntries = new ArrayList<String>(gradingSchemes.keySet());
		int enrollmentIndex = 0;
		int gradingIndex = 0;
		
		// Enrollment sets and sections
		Set<String> instructors = new HashSet<String>();
		instructors.add("admin");
		instructors.add("instructor");

		
		Set<CourseOffering> courseOfferings = cmService.getCourseOfferingsInCourseSet("SMPL");
		for(Iterator<CourseOffering> iter = courseOfferings.iterator(); iter.hasNext();) {
			CourseOffering co = iter.next();
			EnrollmentSet es = cmAdmin.createEnrollmentSet(co.getEid() + "es",
					co.getTitle() + " Enrollment Set", co.getDescription() + " Enrollment Set",
					"lecture", "3", co.getEid(), instructors);
			
			// Enrollments
			for(int enrollmentCounter = 1; enrollmentCounter <= 180; enrollmentCounter++) {
				if(++gradingIndex == gradingEntries.size()) {
					gradingIndex = 0;
				}
				String gradingScheme = gradingEntries.get(gradingIndex);

				if(++enrollmentIndex == enrollmentEntries.size()) {
					enrollmentIndex = 0;
				}
				String enrollmentStatus = enrollmentEntries.get(enrollmentIndex);

				cmAdmin.addOrUpdateEnrollment("student" + enrollmentCounter, es.getEid(), enrollmentStatus, "3", gradingScheme);
			}

			// Sections
			Section lec = cmAdmin.createSection(co.getEid() + " Lecture",
					co.getEid() + " Lecture", "Samples Lecture", lectureCategory.getCategoryCode(),
					null, co.getEid(), es.getEid());
			
			// Meetings
			Set<Meeting> lecMeetings = new HashSet<Meeting>();
			lecMeetings.add(cmAdmin.newSectionMeeting(lec.getEid(), "The location for " + lec.getTitle(), null, null, null));
			lec.setMeetings(lecMeetings);
			cmAdmin.updateSection(lec);

			for(int sectionCounter = 1; sectionCounter <= 6; sectionCounter++) {
				Section discussion = cmAdmin.createSection(co.getEid() + " Discussion " + sectionCounter,
						"Discussion " + sectionCounter, "Samples, Discussion " + sectionCounter,
						discussionCategory.getCategoryCode(), null, co.getEid(), null);

				// Discussion section memberships: students
				for(int studentCounter = (30 * sectionCounter); studentCounter < 30 * (sectionCounter+1); studentCounter++) {
					cmAdmin.addOrUpdateSectionMembership("student" + studentCounter, "S",discussion.getEid(), "member");
				}

				// Discussion section memberships: intructor
				cmAdmin.addOrUpdateSectionMembership("instructor", "I",discussion.getEid(), "section_leader");
				cmAdmin.addOrUpdateSectionMembership("admin", "I",discussion.getEid(), "section_leader");

				// Meetings
				Set<Meeting> discMeetings = new HashSet<Meeting>();
				discMeetings.add(cmAdmin.newSectionMeeting(discussion.getEid(), "The llocation for " + discussion.getTitle(), null, null, null));
				lec.setMeetings(lecMeetings);
				cmAdmin.updateSection(lec);

			}
		}
		if(log.isInfoEnabled()) log.info("Finished loading sample CM data");
	}
}
