/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of California
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      https://source.sakaiproject.org/svn/sakai/trunk/sakai_license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
package org.sakaiproject.coursemanagement.impl;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class CourseManagementServiceHibernateImpl extends HibernateDaoSupport implements CourseManagementService {
	private static final Log log = LogFactory.getLog(CourseManagementServiceHibernateImpl.class);
	
	/**
	 * A generic approach to finding objects by their eid.  This is "coding by convention",
	 * since it expects the parameterized query to use "eid" as the single named parameter.
	 * 
	 * @param eid The eid of the object we're trying to load
	 * @param className The name of the class / interface we're looking for
	 * @param namedQuery The name of the query
	 * @return The object, if found
	 * @throws IdNotFoundException
	 */
	private Object getObjectByEid(final String eid, final String className, final String namedQuery) throws IdNotFoundException {
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
	
	
	public CourseSet getCourseSet(String eid) throws IdNotFoundException {
		return (CourseSet)getObjectByEid(eid, CourseSetImpl.class.getName(), "findCourseSetByEid");
	}

	public Set getChildCourseSets(final String parentCourseSetEid) {
		// Ensure that the parent exists
		// TODO Add exists() methods rather than loading the entire object
		getCourseSet(parentCourseSetEid);

		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findChildCourseSets");
				q.setParameter("parentEid", parentCourseSetEid);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set getCourseSets() {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findTopLevelCourseSets");
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set getCourseSetMemberships(String courseSetEid) throws IdNotFoundException {
		return getMemberships((CourseSetImpl)getCourseSet(courseSetEid));
	}

	public CanonicalCourse getCanonicalCourse(String eid) throws IdNotFoundException {
		return (CanonicalCourse)getObjectByEid(eid, CanonicalCourseImpl.class.getName(), "findCanonicalCourseByEid");
	}

	public Set getEquivalentCanonicalCourses(String canonicalCourseEid) {
		final CanonicalCourseImpl canonicalCourse = (CanonicalCourseImpl)getCanonicalCourse(canonicalCourseEid);
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEquivalentCanonicalCourses");
				q.setParameter("crossListing", canonicalCourse.getCrossListing());
				q.setParameter("canonicalCourse", canonicalCourse);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set getCanonicalCourses(final String courseSetEid) throws IdNotFoundException {
		return ((CourseSetImpl)getCourseSet(courseSetEid)).getCanonicalCourses();
	}

	public Set getAcademicSessions() {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findAcademicSessions");
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public AcademicSession getAcademicSession(final String eid) throws IdNotFoundException {
		return (AcademicSession)getObjectByEid(eid, AcademicSessionImpl.class.getName(), "findAcademicSessionByEid");
	}
	
	public CourseOffering getCourseOffering(String eid) throws IdNotFoundException {
		return (CourseOffering)getObjectByEid(eid, CourseOfferingImpl.class.getName(), "findCourseOfferingByEid");
	}

	public Set getCourseOfferings(String courseSetEid) throws IdNotFoundException {
		return ((CourseSetImpl)getCourseSet(courseSetEid)).getCourseOfferings();
	}

	public Set getEquivalentCourseOfferings(String courseOfferingEid) throws IdNotFoundException {
		final CourseOfferingImpl courseOffering = (CourseOfferingImpl)getCourseOffering(courseOfferingEid);
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEquivalentCourseOfferings");
				q.setParameter("crossListing", courseOffering.getCrossListing());
				q.setParameter("courseOffering", courseOffering);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set getCourseOfferingMemberships(String courseOfferingEid) throws IdNotFoundException {
		return getMemberships((CourseOfferingImpl)getCourseOffering(courseOfferingEid));
	}

	/**
	 * Gets the memberships for a membership container.  This query can not be
	 * performed using just the container's eid, since it may conflict with other kinds
	 * of objects with the same eid.
	 * 
	 * @param container
	 * @return
	 */
	private Set getMemberships(final AbstractMembershipContainer container) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer sb = new StringBuffer("select member from MembershipImpl as member, ");
					sb.append(container.getClass().getName());
					sb.append(" as container where member.memberContainer=container ");
					sb.append("and container.eid=:eid");
				Query q = session.createQuery(sb.toString());
				q.setParameter("eid", container.getEid());
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}


	public Section getSection(String eid) throws IdNotFoundException {
		return (Section)getObjectByEid(eid, SectionImpl.class.getName(), "findSectionByEid");
	}

	public Set getSections(String courseOfferingEid) throws IdNotFoundException {
		final CourseOffering courseOffering = getCourseOffering(courseOfferingEid);
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findTopLevelSectionsInCourseOffering");
				q.setParameter("courseOffering", courseOffering);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set getChildSections(final String parentSectionEid) throws IdNotFoundException {
		// Ensure that the parent exists
		// TODO Add exists() methods rather than loading the entire object
		getSection(parentSectionEid);
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findChildSections");
				q.setParameter("parentEid", parentSectionEid);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set getSectionMemberships(String sectionEid) throws IdNotFoundException {
		return getMemberships((SectionImpl)getSection(sectionEid));
	}

	public EnrollmentSet getEnrollmentSet(String eid) throws IdNotFoundException {
		return (EnrollmentSet)getObjectByEid(eid, EnrollmentSetImpl.class.getName(), "findEnrollmentSetByEid");
	}

	public Set getEnrollmentSets(final String courseOfferingEid) throws IdNotFoundException {
		// Ensure that the parent exists
		// TODO Add exists() methods rather than loading the entire object
		getCourseOffering(courseOfferingEid);
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEnrollmentSetsByCourseOffering");
				q.setParameter("courseOfferingEid", courseOfferingEid);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set getEnrollments(final String enrollmentSetEid) throws IdNotFoundException {
		// Ensure that the parent exists
		// TODO Add exists() methods rather than loading the entire object
		getEnrollmentSet(enrollmentSetEid);
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEnrollments");
				q.setParameter("enrollmentSetEid", enrollmentSetEid);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public boolean isEnrolled(final String userId, final Set enrollmentSetEids) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("countEnrollments");
				q.setParameter("userId", userId);
				q.setParameterList("enrollmentSetEids", enrollmentSetEids);
				return q.iterate().next();
			}
		};
		Integer i = (Integer)getHibernateTemplate().execute(hc);
		if(log.isDebugEnabled()) log.debug(userId + " is enrolled in " + i + " of these " + enrollmentSetEids.size() + " EnrollmentSets" );
		return i.intValue() > 0;
	}

	public boolean isEnrolled(String userId, String enrollmentSetEid) {
		HashSet enrollmentSetEids = new HashSet();
		enrollmentSetEids.add(enrollmentSetEid);
		return isEnrolled(userId, enrollmentSetEids);
	}
	
	public Enrollment findEnrollment(final String userId, final String enrollmentSetEid) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEnrollment");
				q.setParameter("userId", userId);
				q.setParameter("enrollmentSetEid", enrollmentSetEid);
				return q.uniqueResult();
			}
		};
		return (Enrollment)getHibernateTemplate().execute(hc);
	}
	
	public Set getInstructorsOfRecordIds(String enrollmentSetEid) throws IdNotFoundException {
		EnrollmentSet es = getEnrollmentSet(enrollmentSetEid);
		return es.getOfficialGraders();
	}


	public Set findCurrentlyEnrolledEnrollmentSets(final String userId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findCurrentlyEnrolledEnrollmentSets");
				q.setParameter("userId", userId);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}


	public Set findCurrentlyInstructingEnrollmentSets(final String userId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findCurrentlyGradableEnrollmentSets");
				q.setParameter("userId", userId);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set findCurrentSectionsWithMember(final String userId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findCurrentSectionsWithMember");
				q.setParameter("userId", userId);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	private MembershipImpl getMembership(final String userId, final AbstractMembershipContainer container) {
        final StringBuffer sb = new StringBuffer("select member from MembershipImpl as member, ");
		sb.append(container.getClass().getName());
        sb.append(" as container where member.memberContainer=container ");
        sb.append("and container.eid=:eid ");
    	sb.append("and member.userId=:userId");
    	
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery(sb.toString());
				q.setParameter("eid", container.getEid());
				q.setParameter("userId", userId);
				return q.uniqueResult();
			}
		};
		return (MembershipImpl)getHibernateTemplate().execute(hc);
	}

	public String getSectionRole(final String sectionEid, final String userId) {
		SectionImpl section = (SectionImpl)getSection(sectionEid);
		Membership member = getMembership(userId, section);
		if(member == null) {
			return null;
		} else {
			return member.getRole();
		}
	}


	public Set findCourseOfferings(final String courseSetEid, final String academicSessionEid) throws IdNotFoundException {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findCourseOfferingsByCourseSetAndAcademicSession");
				q.setParameter("courseSetEid", courseSetEid);
				q.setParameter("academicSessionEid", academicSessionEid);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}


	public boolean isEmpty(final String courseSetEid) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findNonEmptyCourseSet");
				q.setParameter("eid", courseSetEid);
				return new Boolean( ! q.iterate().hasNext());
			}
		};
		return ((Boolean)getHibernateTemplate().execute(hc)).booleanValue();
	}
}
