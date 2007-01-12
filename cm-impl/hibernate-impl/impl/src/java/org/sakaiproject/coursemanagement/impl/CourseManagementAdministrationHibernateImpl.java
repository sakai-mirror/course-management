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

import java.sql.Time;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.SectionCategory;
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
			parent = (CourseSet)getObjectByEid(parentCourseSetEid, CourseSetCmImpl.class.getName());
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
		CourseSetCmImpl courseSet = (CourseSetCmImpl)getObjectByEid(courseSetEid, CourseSetCmImpl.class.getName());
		CanonicalCourseCmImpl canonCourse = (CanonicalCourseCmImpl)getObjectByEid(canonicalCourseEid, CanonicalCourseCmImpl.class.getName());
		
		Set<CanonicalCourse> canonCourses = courseSet.getCanonicalCourses();
		if(canonCourses == null) {
			canonCourses = new HashSet<CanonicalCourse>();
			courseSet.setCanonicalCourses(canonCourses);
		}
		canonCourses.add(canonCourse);
		getHibernateTemplate().update(courseSet);
	}

	public boolean removeCanonicalCourseFromCourseSet(String courseSetEid, String canonicalCourseEid) {
		CourseSetCmImpl courseSet = (CourseSetCmImpl)getObjectByEid(courseSetEid, CourseSetCmImpl.class.getName());
		CanonicalCourseCmImpl canonCourse = (CanonicalCourseCmImpl)getObjectByEid(canonicalCourseEid, CanonicalCourseCmImpl.class.getName());
		
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
		Set<CrossListingCmImpl> oldCrossListings = new HashSet<CrossListingCmImpl>();

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
		AcademicSession as = (AcademicSession)getObjectByEid(academicSessionEid, AcademicSessionCmImpl.class.getName());
		CanonicalCourse cc = (CanonicalCourse)getObjectByEid(canonicalCourseEid, CanonicalCourseCmImpl.class.getName());
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
		CourseSetCmImpl courseSet = (CourseSetCmImpl)getObjectByEid(courseSetEid, CourseSetCmImpl.class.getName());
		CourseOfferingCmImpl courseOffering = (CourseOfferingCmImpl)getObjectByEid(courseOfferingEid, CourseOfferingCmImpl.class.getName());
		Set<CourseOffering> offerings = courseSet.getCourseOfferings();
		if(offerings == null) {
			offerings = new HashSet<CourseOffering>();
		}
		offerings.add(courseOffering);
		courseSet.setCourseOfferings(offerings);
		getHibernateTemplate().update(courseSet);
	}

	public boolean removeCourseOfferingFromCourseSet(String courseSetEid, String courseOfferingEid) {
		CourseSetCmImpl courseSet = (CourseSetCmImpl)getObjectByEid(courseSetEid, CourseSetCmImpl.class.getName());
		CourseOffering courseOffering = (CourseOffering)getObjectByEid(courseOfferingEid, CourseOfferingCmImpl.class.getName());
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
		CourseOffering co = (CourseOffering)getObjectByEid(courseOfferingEid, CourseOfferingCmImpl.class.getName());
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
		
		List enrollments = getHibernateTemplate().findByNamedQueryAndNamedParam("findEnrollment",
				new String[] {"enrollmentSetEid", "userId"},
				new Object[] {enrollmentSetEid, userId});
		if(enrollments.isEmpty()) {
			EnrollmentSet enrollmentSet = (EnrollmentSet)getObjectByEid(enrollmentSetEid, EnrollmentSetCmImpl.class.getName());
			enrollment = new EnrollmentCmImpl(userId, enrollmentSet, enrollmentStatus, credits, gradingScheme);
			getHibernateTemplate().save(enrollment);
		} else {
			enrollment = (EnrollmentCmImpl)enrollments.get(0);
			enrollment.setEnrollmentStatus(enrollmentStatus);
			enrollment.setCredits(credits);
			enrollment.setGradingScheme(gradingScheme);
			getHibernateTemplate().update(enrollment);
		}
		return enrollment;
	}

	public boolean removeEnrollment(String userId, String enrollmentSetEid) {
		List enrollments = getHibernateTemplate().findByNamedQueryAndNamedParam("findEnrollment",
				new String[] {"enrollmentSetEid", "userId"},
				new Object[] {enrollmentSetEid, userId});
		
		if(enrollments.isEmpty()) {
			return false;
		} else {
			Enrollment enr = (Enrollment)enrollments.get(0);
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
                Integer maxSize = null;

		// Get the enrollment set, if needed
		if(courseOfferingEid != null) {
			co = (CourseOffering)getObjectByEid(courseOfferingEid, CourseOfferingCmImpl.class.getName());
		}

		// Get the parent section, if needed
		if(parentSectionEid != null) {
			parent = (Section)getObjectByEid(parentSectionEid, SectionCmImpl.class.getName());
		}
		
		// Get the enrollment set, if needed
		if(enrollmentSetEid != null) {
			es = (EnrollmentSet)getObjectByEid(enrollmentSetEid, EnrollmentSetCmImpl.class.getName());
		}

		SectionCmImpl section = new SectionCmImpl(eid, title, description, category, parent, co, es, maxSize);
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
	
    public Membership addOrUpdateCourseSetMembership(final String userId, String role, final String courseSetEid, final String status) throws IdNotFoundException {
		CourseSetCmImpl cs = (CourseSetCmImpl)getObjectByEid(courseSetEid, CourseSetCmImpl.class.getName());
		MembershipCmImpl member =getMembership(userId, cs);
		if(member == null) {
			// Add the new member
		    member = new MembershipCmImpl(userId, role, cs, status);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			member.setStatus(status);
			getHibernateTemplate().update(member);
		}
		return member;
	}

	public boolean removeCourseSetMembership(String userId, String courseSetEid) {
		MembershipCmImpl member = getMembership(userId, (CourseSetCmImpl)getObjectByEid(courseSetEid, CourseSetCmImpl.class.getName()));
		if(member == null) {
			return false;
		} else {
			getHibernateTemplate().delete(member);
			return true;
		}
	}

    public Membership addOrUpdateCourseOfferingMembership(String userId, String role, String courseOfferingEid, String status) {
		CourseOfferingCmImpl co = (CourseOfferingCmImpl)getObjectByEid(courseOfferingEid, CourseOfferingCmImpl.class.getName());
		MembershipCmImpl member =getMembership(userId, co);
		if(member == null) {
			// Add the new member
		    member = new MembershipCmImpl(userId, role, co, status);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			member.setStatus(status);
			getHibernateTemplate().update(member);
		}
		return member;
	}

	public boolean removeCourseOfferingMembership(String userId, String courseOfferingEid) {
		CourseOfferingCmImpl courseOffering = (CourseOfferingCmImpl)getObjectByEid(courseOfferingEid, CourseOfferingCmImpl.class.getName());
		MembershipCmImpl member = getMembership(userId, courseOffering);
		if(member == null) {
			return false;
		} else {
			getHibernateTemplate().delete(member);
			return true;
		}
	}
	
    public Membership addOrUpdateSectionMembership(String userId, String role, String sectionEid, String status) {
		SectionCmImpl sec = (SectionCmImpl)getObjectByEid(sectionEid, SectionCmImpl.class.getName());
		MembershipCmImpl member =getMembership(userId, sec);
		if(member == null) {
			// Add the new member
		    member = new MembershipCmImpl(userId, role, sec, status);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			member.setStatus(status);
			getHibernateTemplate().update(member);
		}
		return member;
	}

	public boolean removeSectionMembership(String userId, String sectionEid) {
		SectionCmImpl sec = (SectionCmImpl)getObjectByEid(sectionEid, SectionCmImpl.class.getName());
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

	public Meeting newSectionMeeting(String sectionEid, String location, Time startTime, Time finishTime, String notes) {
		Section section = (Section)getObjectByEid(sectionEid, SectionCmImpl.class.getName());
		MeetingCmImpl meeting = new MeetingCmImpl(section, location, startTime, finishTime, notes);
		Set<Meeting> meetings = section.getMeetings();
		if(meetings == null) {
			meetings = new HashSet<Meeting>();
			section.setMeetings(meetings);
		}
		return meeting;
	}

	public void removeAcademicSession(String eid) {
		getHibernateTemplate().delete(
				getObjectByEid(eid, AcademicSessionCmImpl.class.getName())
		);
	}

	public void removeCanonicalCourse(String eid) {
		CanonicalCourseCmImpl cc = (CanonicalCourseCmImpl)getObjectByEid(eid, CanonicalCourseCmImpl.class.getName());
		
		// Remove any equivalents
		removeEquiv(cc);
		
		// Remove the associated course offerings (see removeCourseOffering for further cascades)
		Set<CourseOffering> coSet = new HashSet<CourseOffering>(getHibernateTemplate().findByNamedQueryAndNamedParam("findCourseOfferingsByCanonicalCourse", "canonicalCourseEid",eid));
		for(Iterator<CourseOffering> iter = coSet.iterator(); iter.hasNext();) {
			CourseOffering co = iter.next();
			removeCourseOffering(co.getEid());
		}
		
		getHibernateTemplate().delete(cc);
	}

	public void removeCourseOffering(String eid) {
		CourseOfferingCmImpl co = (CourseOfferingCmImpl)getObjectByEid(eid, CourseOfferingCmImpl.class.getName());
		
		// Remove the memberships
		for(Iterator iter = getMemberships(co).iterator(); iter.hasNext();) {
			getHibernateTemplate().delete(iter.next());
		}

		// Remove the sections
		List sections = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findTopLevelSectionsInCourseOffering", "courseOffering",co);
		for(Iterator iter = sections.iterator(); iter.hasNext();) {
			Section sec = (Section)iter.next();
			removeSection(sec.getEid());
		}
		
		List enrollmentSets = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findEnrollmentSetsByCourseOffering", "courseOfferingEid",eid);
		// Remove the enrollment sets
		for(Iterator iter = enrollmentSets.iterator(); iter.hasNext();) {
			EnrollmentSet enr = (EnrollmentSet)iter.next();
			removeEnrollmentSet(enr.getEid());
		}
		
		// Remove the course offering itself
		getHibernateTemplate().delete(co);
	}

	public void removeCourseSet(String eid) {
		CourseSetCmImpl cs = (CourseSetCmImpl)getObjectByEid(eid, CourseSetCmImpl.class.getName());

		// Remove the memberships
		for(Iterator iter = getMemberships(cs).iterator(); iter.hasNext();) {
			getHibernateTemplate().delete(iter.next());
		}

		// Remove the course set itself
		getHibernateTemplate().delete(cs);
	}

	public void removeEnrollmentSet(String eid) {
		EnrollmentSet es = (EnrollmentSet)getObjectByEid(eid, EnrollmentSetCmImpl.class.getName());

		List enrollments = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findEnrollments", "enrollmentSetEid", eid);
		for(Iterator iter = enrollments.iterator(); iter.hasNext();) {
			getHibernateTemplate().delete(iter.next());
		}
		
		// Remove the enrollment set itself
		getHibernateTemplate().delete(es);
	}

	public void removeSection(String eid) {
		SectionCmImpl sec = (SectionCmImpl)getObjectByEid(eid, SectionCmImpl.class.getName());

		// Remove the memberships
		for(Iterator iter = getMemberships(sec).iterator(); iter.hasNext();) {
			getHibernateTemplate().delete(iter.next());
		}

		// Remove the section itself
		getHibernateTemplate().delete(sec);
	}

	public SectionCategory addSectionCategory(String categoryCode, String categoryDescription) {
		SectionCategoryCmImpl cat = new SectionCategoryCmImpl(categoryCode, categoryDescription);
		getHibernateTemplate().save(cat);
		return cat;
	}
	
	
	// TODO: The following two methods were copied from CM Service.  Consolidate them.
	
	/**
	 * A generic approach to finding objects by their eid.  This is "coding by convention",
	 * since it expects the parameterized query to use "eid" as the single named parameter.
	 * 
	 * @param eid The eid of the object we're trying to load
	 * @param className The name of the class / interface we're looking for
	 * @return The object, if found
	 * @throws IdNotFoundException
	 */
	private Object getObjectByEid(final String eid, final String className) throws IdNotFoundException {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer hql = new StringBuffer();
				hql.append("from ").append(className).append(" as obj where obj.eid=:eid");
				Query q = session.createQuery(hql.toString());
				q.setParameter("eid", eid);
				Object result = q.uniqueResult();
				if(result == null) {
					throw new IdNotFoundException(eid, className);
				}
				return result;
			}
		};
		return getHibernateTemplate().execute(hc);
	}
	
	/**
	 * Gets the memberships for a membership container.  This query can not be
	 * performed using just the container's eid, since it may conflict with other kinds
	 * of objects with the same eid.
	 * 
	 * @param container
	 * @return
	 */
	private Set<Membership> getMemberships(final AbstractMembershipContainerCmImpl container) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer sb = new StringBuffer("select mbr from MembershipCmImpl as mbr, ");
					sb.append(container.getClass().getName());
					sb.append(" as container where mbr.memberContainer=container ");
					sb.append("and container.eid=:eid");
				Query q = session.createQuery(sb.toString());
				q.setParameter("eid", container.getEid());
				return q.list();
			}
		};
		return new HashSet<Membership>(getHibernateTemplate().executeFind(hc));
	}


}
