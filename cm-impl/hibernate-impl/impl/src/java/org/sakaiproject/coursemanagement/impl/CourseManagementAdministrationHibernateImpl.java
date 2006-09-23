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
package org.sakaiproject.coursemanagement.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Manipulates course and enrollment data stored in sakai's local hibernate tables.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class CourseManagementAdministrationHibernateImpl extends
		HibernateDaoSupport implements CourseManagementAdministration {

	private static final Log log = LogFactory.getLog(CourseManagementAdministrationHibernateImpl.class);
	
	CourseManagementService cmService;
	
	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}
	
	public void init() {
		log.info("Initializing " + getClass().getName());
	}

	public void destroy() {
		log.info("Destroying " + getClass().getName());
	}
	
	public AcademicSession createAcademicSession(String eid, String title,
			String description, Date startDate, Date endDate) throws IdExistsException {
		AcademicSessionCmImpl academicSession = new AcademicSessionCmImpl(eid, title, description, startDate, endDate);
		try {
			getHibernateTemplate().save(academicSession);
			return academicSession;
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, AcademicSession.class.getName());
		}
	}

	public void updateAcademicSession(AcademicSession academicSession) {
		getHibernateTemplate().update(academicSession);
	}

	public CourseSet createCourseSet(String eid, String title, String description, String category,
			String parentCourseSetEid) throws IdExistsException {
		CourseSet parent = null;
		if(parentCourseSetEid != null) {
			parent = cmService.getCourseSet(parentCourseSetEid);
		}
		CourseSetCmImpl courseSet = new CourseSetCmImpl(eid, title, description, category, parent);
		try {
			getHibernateTemplate().save(courseSet);
			return courseSet;
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, CourseSet.class.getName());
		}
	}

	public void updateCourseSet(CourseSet courseSet) {
		getHibernateTemplate().update(courseSet);
	}

	public CanonicalCourse createCanonicalCourse(String eid, String title, String description) throws IdExistsException {
		CanonicalCourseCmImpl canonCourse = new CanonicalCourseCmImpl(eid, title, description);
		try {
			getHibernateTemplate().save(canonCourse);
			return canonCourse;
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, CanonicalCourse.class.getName());
		}
	}

	public void updateCanonicalCourse(CanonicalCourse canonicalCourse) {
		getHibernateTemplate().update(canonicalCourse);
	}

	public void addCanonicalCourseToCourseSet(String courseSetEid, String canonicalCourseEid) throws IdNotFoundException {
		CourseSetCmImpl courseSet = (CourseSetCmImpl)cmService.getCourseSet(courseSetEid);
		CanonicalCourseCmImpl canonCourse = (CanonicalCourseCmImpl)cmService.getCanonicalCourse(canonicalCourseEid);
		Set canonCourses = courseSet.getCanonicalCourses();
		if(canonCourses == null) {
			canonCourses = new HashSet();
			courseSet.setCanonicalCourses(canonCourses);
		}
		canonCourses.add(canonCourse);
		getHibernateTemplate().update(courseSet);
	}

	public boolean removeCanonicalCourseFromCourseSet(String courseSetEid, String canonicalCourseEid) {
		CourseSetCmImpl courseSet = (CourseSetCmImpl)cmService.getCourseSet(courseSetEid);
		CanonicalCourseCmImpl canonCourse = (CanonicalCourseCmImpl)cmService.getCanonicalCourse(canonicalCourseEid);
		Set courses = courseSet.getCanonicalCourses();
		if(courses == null || ! courses.contains(canonCourse)) {
			return false;
		}
		courses.remove(canonCourse);
		getHibernateTemplate().update(courseSet);
		return true;
	}

	private void setEquivalents(Set crossListables) {
		CrossListingCmImpl newCrossListing = new CrossListingCmImpl();
		getHibernateTemplate().save(newCrossListing);
		Set oldCrossListings = new HashSet();

		for(Iterator iter = crossListables.iterator(); iter.hasNext();) {
			CrossListableCmImpl clable = (CrossListableCmImpl)iter.next();
			CrossListingCmImpl oldCrossListing = clable.getCrossListing();
			if(oldCrossListing != null) {
				oldCrossListings.add(oldCrossListing);
			}
			if(log.isDebugEnabled()) log.debug("Setting crosslisting for crosslistable " +
					clable.getEid() + " to " + newCrossListing.getKey());
			clable.setCrossListing(newCrossListing);
			getHibernateTemplate().update(clable);
		}
		
		// TODO Clean up orphaned cross listings
	}
	
	public void setEquivalentCanonicalCourses(Set canonicalCourses) {
		setEquivalents(canonicalCourses);
	}

	private boolean removeEquiv(CrossListableCmImpl impl) {
		boolean hadCrossListing = impl.getCrossListing() != null;
		impl.setCrossListing(null);
		getHibernateTemplate().update(impl);
		return hadCrossListing;
	}
	
	public boolean removeEquivalency(CanonicalCourse canonicalCourse) {
		return removeEquiv((CanonicalCourseCmImpl)canonicalCourse);
	}

	public CourseOffering createCourseOffering(String eid, String title, String description,
			String status, String academicSessionEid, String canonicalCourseEid, Date startDate, Date endDate) throws IdExistsException {
		AcademicSession as = cmService.getAcademicSession(academicSessionEid);
		CanonicalCourse cc = cmService.getCanonicalCourse(canonicalCourseEid);
		CourseOfferingCmImpl co = new CourseOfferingCmImpl(eid, title, description, status, as, cc, startDate, endDate);
		try {
			getHibernateTemplate().save(co);
			return co;
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, CourseOffering.class.getName());
		}
	}

	public void updateCourseOffering(CourseOffering courseOffering) {
		getHibernateTemplate().update(courseOffering);
	}

	public void setEquivalentCourseOfferings(Set courseOfferings) {
		setEquivalents(courseOfferings);
	}

	public boolean removeEquivalency(CourseOffering courseOffering) {
		return removeEquiv((CrossListableCmImpl)courseOffering);
	}

	public void addCourseOfferingToCourseSet(String courseSetEid, String courseOfferingEid) {
		// CourseSet's set of courses are controlled on the CourseSet side of the bi-directional relationship
		CourseSetCmImpl courseSet = (CourseSetCmImpl)cmService.getCourseSet(courseSetEid);
		CourseOfferingCmImpl courseOffering = (CourseOfferingCmImpl)cmService.getCourseOffering(courseOfferingEid);
		Set offerings = courseSet.getCourseOfferings();
		if(offerings == null) {
			offerings = new HashSet();
		}
		offerings.add(courseOffering);
		courseSet.setCourseOfferings(offerings);
		getHibernateTemplate().update(courseSet);
	}

	public boolean removeCourseOfferingFromCourseSet(String courseSetEid, String courseOfferingEid) {
		CourseSetCmImpl courseSet = (CourseSetCmImpl)cmService.getCourseSet(courseSetEid);
		CourseOffering courseOffering = cmService.getCourseOffering(courseOfferingEid);
		Set offerings = courseSet.getCourseOfferings();
		if(offerings == null || ! offerings.contains(courseOffering)) {
			return false;
		}
		offerings.remove(courseOffering);
		getHibernateTemplate().update(courseSet);
		return true;
	}

	public EnrollmentSet createEnrollmentSet(String eid, String title, String description, String category,
			String defaultEnrollmentCredits, String courseOfferingEid, Set officialGraders)
			throws IdExistsException {
		if(courseOfferingEid == null) {
			throw new IllegalArgumentException("You can not create an EnrollmentSet without specifying a courseOffering");
		}
		CourseOffering co = cmService.getCourseOffering(courseOfferingEid);
		EnrollmentSetCmImpl enrollmentSet = new EnrollmentSetCmImpl(eid, title, description, category, defaultEnrollmentCredits, co, officialGraders);
		try {
			getHibernateTemplate().save(enrollmentSet);
			return enrollmentSet;
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, EnrollmentSet.class.getName());
		}
	}

	public void updateEnrollmentSet(EnrollmentSet enrollmentSet) {
		getHibernateTemplate().update(enrollmentSet);
	}

	public Enrollment addOrUpdateEnrollment(String userId, String enrollmentSetEid, String enrollmentStatus, String credits, String gradingScheme) {
		EnrollmentCmImpl enrollment = null;
		if(cmService.isEnrolled(userId,enrollmentSetEid)) {
			enrollment = (EnrollmentCmImpl)cmService.findEnrollment(userId, enrollmentSetEid);
			enrollment.setEnrollmentStatus(enrollmentStatus);
			enrollment.setCredits(credits);
			enrollment.setGradingScheme(gradingScheme);
			getHibernateTemplate().update(enrollment);
		} else {
			EnrollmentSet enrollmentSet = cmService.getEnrollmentSet(enrollmentSetEid);
			enrollment = new EnrollmentCmImpl(userId, enrollmentSet, enrollmentStatus, credits, gradingScheme);
			getHibernateTemplate().save(enrollment);
		}
		return enrollment;
	}

	public boolean removeEnrollment(String userId, String enrollmentSetEid) {
		EnrollmentCmImpl enr = (EnrollmentCmImpl)cmService.findEnrollment(userId, enrollmentSetEid);
		if(enr == null) {
			return false;
		} else {
			enr.setDropped(true);
			getHibernateTemplate().update(enr);
			return true;
		}
	}

	public Section createSection(String eid, String title, String description, String category,
		String parentSectionEid, String courseOfferingEid, String enrollmentSetEid) throws IdExistsException {
		
		// The objects related to this section
		Section parent = null;
		CourseOffering co = null;
		EnrollmentSet es = null;

		// Get the enrollment set, if needed
		if(courseOfferingEid != null) {
			co = cmService.getCourseOffering(courseOfferingEid);
		}

		// Get the parent section, if needed
		if(parentSectionEid != null) {
			parent = cmService.getSection(parentSectionEid);
		}
		
		// Get the enrollment set, if needed
		if(enrollmentSetEid != null) {
			es = cmService.getEnrollmentSet(enrollmentSetEid);
		}

		SectionCmImpl section = new SectionCmImpl(eid, title, description, category, parent, co, es);
		try {
			getHibernateTemplate().save(section);
			return section;
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, Section.class.getName());
		}
	}

	public void updateSection(Section section) {
		getHibernateTemplate().update(section);
	}
	
	public Membership addOrUpdateCourseSetMembership(final String userId, String role, final String courseSetEid) throws IdNotFoundException {
		CourseSetCmImpl cs = (CourseSetCmImpl)cmService.getCourseSet(courseSetEid);
		MembershipCmImpl member =getMembership(userId, cs);
		if(member == null) {
			// Add the new member
			member = new MembershipCmImpl(userId, role, cs);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			getHibernateTemplate().update(member);
		}
		return member;
	}

	public boolean removeCourseSetMembership(String userId, String courseSetEid) {
		MembershipCmImpl member = getMembership(userId, (CourseSetCmImpl)cmService.getCourseSet(courseSetEid));
		if(member == null) {
			return false;
		} else {
			getHibernateTemplate().delete(member);
			return true;
		}
	}

	public Membership addOrUpdateCourseOfferingMembership(String userId, String role, String courseOfferingEid) {
		CourseOfferingCmImpl co = (CourseOfferingCmImpl)cmService.getCourseOffering(courseOfferingEid);
		MembershipCmImpl member =getMembership(userId, co);
		if(member == null) {
			// Add the new member
			member = new MembershipCmImpl(userId, role, co);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			getHibernateTemplate().update(member);
		}
		return member;
	}

	public boolean removeCourseOfferingMembership(String userId, String courseOfferingEid) {
		CourseOfferingCmImpl courseOffering = (CourseOfferingCmImpl)cmService.getCourseOffering(courseOfferingEid);
		MembershipCmImpl member = getMembership(userId, courseOffering);
		if(member == null) {
			return false;
		} else {
			getHibernateTemplate().delete(member);
			return true;
		}
	}
	
	public Membership addOrUpdateSectionMembership(String userId, String role, String sectionEid) {
		SectionCmImpl sec = (SectionCmImpl)cmService.getSection(sectionEid);
		MembershipCmImpl member =getMembership(userId, sec);
		if(member == null) {
			// Add the new member
			member = new MembershipCmImpl(userId, role, sec);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			getHibernateTemplate().update(member);
		}
		return member;
	}

	public boolean removeSectionMembership(String userId, String sectionEid) {
		SectionCmImpl sec = (SectionCmImpl)cmService.getSection(sectionEid);
		MembershipCmImpl member = getMembership(userId, sec);
		if(member == null) {
			return false;
		} else {
			getHibernateTemplate().delete(member);
			return true;
		}
	}
	
	private MembershipCmImpl getMembership(final String userId, final AbstractMembershipContainerCmImpl container) {
        final StringBuffer sb = new StringBuffer("select mbr from MembershipCmImpl as mbr, ");
		sb.append(container.getClass().getName());
        sb.append(" as container where mbr.memberContainer=container ");
        sb.append("and container.eid=:eid ");
    	sb.append("and mbr.userId=:userId");
    	
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery(sb.toString());
				q.setParameter("eid", container.getEid());
				q.setParameter("userId", userId);
				return q.uniqueResult();
			}
		};
		return (MembershipCmImpl)getHibernateTemplate().execute(hc);
	}

	public Meeting newSectionMeeting(String sectionEid, String location, String time, String notes) {
		Section section = cmService.getSection(sectionEid);
		MeetingCmImpl meeting = new MeetingCmImpl(section, location, time, notes);
		Set meetings = section.getMeetings();
		if(meetings == null) {
			meetings = new HashSet();
			section.setMeetings(meetings);
		}
		return meeting;
	}

	public void removeAcademicSession(String eid) {
		AcademicSession as = cmService.getAcademicSession(eid);
		getHibernateTemplate().delete(as);
	}

	public void removeCanonicalCourse(String eid) {
		CanonicalCourseCmImpl cc = (CanonicalCourseCmImpl)cmService.getCanonicalCourse(eid);
		
		// Remove any equivalents
		removeEquiv(cc);
		
		// Remove the associated course offerings (see removeCourseOffering for further cascades)
		Set coSet = cmService.getCourseOfferingsInCanonicalCourse(eid);
		for(Iterator iter = coSet.iterator(); iter.hasNext();) {
			CourseOffering co = (CourseOffering)iter.next();
			removeCourseOffering(co.getEid());
		}
		
		getHibernateTemplate().delete(cc);
	}

	public void removeCourseOffering(String eid) {
		CourseOffering co = cmService.getCourseOffering(eid);
		Set memberships = cmService.getCourseOfferingMemberships(eid);
		
		// Remove the memberships
		for(Iterator iter = memberships.iterator(); iter.hasNext();) {
			getHibernateTemplate().delete(iter.next());
		}

		// Remove the sections
		for(Iterator iter = cmService.getSections(eid).iterator(); iter.hasNext();) {
			Section sec = (Section)iter.next();
			removeSection(sec.getEid());
		}
		
		// Remove the enrollment sets
		for(Iterator iter = cmService.getEnrollmentSets(eid).iterator(); iter.hasNext();) {
			EnrollmentSet enr = (EnrollmentSet)iter.next();
			removeEnrollmentSet(enr.getEid());
		}
		
		// Remove the course offering itself
		getHibernateTemplate().delete(co);
	}

	public void removeCourseSet(String eid) {
		CourseSet cs = cmService.getCourseSet(eid);
		Set memberships = cmService.getCourseSetMemberships(eid);

		// Remove the memberships
		for(Iterator iter = memberships.iterator(); iter.hasNext();) {
			getHibernateTemplate().delete(iter.next());
		}
		
		// Remove the course set itself
		getHibernateTemplate().delete(cs);
	}

	public void removeEnrollmentSet(String eid) {
		EnrollmentSet es = cmService.getEnrollmentSet(eid);

		// Remove the enrollments
		Set enrollments = cmService.getEnrollments(eid);
		for(Iterator iter = enrollments.iterator(); iter.hasNext();) {
			getHibernateTemplate().delete(iter.next());
		}
		
		// Remove the enrollment set itself
		getHibernateTemplate().delete(es);
	}

	public void removeSection(String eid) {
		Section sec = cmService.getSection(eid);
		Set memberships = cmService.getSectionMemberships(eid);
		
		// Remove the memberships
		for(Iterator iter = memberships.iterator(); iter.hasNext();) {
			getHibernateTemplate().delete(iter.next());
		}

		// Remove the section itself
		getHibernateTemplate().delete(sec);
	}

}
