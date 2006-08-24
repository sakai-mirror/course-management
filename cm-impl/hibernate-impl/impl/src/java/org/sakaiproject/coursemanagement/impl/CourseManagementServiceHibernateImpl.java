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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Provides access to course and enrollment data stored in sakai's local hibernate tables.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
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
		return (CourseSet)getObjectByEid(eid, CourseSetCmImpl.class.getName(), "findCourseSetByEid");
	}

	public Set getChildCourseSets(final String parentCourseSetEid) throws IdNotFoundException {
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
		return getMemberships((CourseSetCmImpl)getCourseSet(courseSetEid));
	}

	public CanonicalCourse getCanonicalCourse(String eid) throws IdNotFoundException {
		return (CanonicalCourse)getObjectByEid(eid, CanonicalCourseCmImpl.class.getName(), "findCanonicalCourseByEid");
	}

	public Set getEquivalentCanonicalCourses(String canonicalCourseEid) {
		final CanonicalCourseCmImpl canonicalCourse = (CanonicalCourseCmImpl)getCanonicalCourse(canonicalCourseEid);
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
		return ((CourseSetCmImpl)getCourseSet(courseSetEid)).getCanonicalCourses();
	}

	public List getAcademicSessions() {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findAcademicSessions");
				return q.list();
			}
		};
		return getHibernateTemplate().executeFind(hc);
	}

	public List getCurrentAcademicSessions() {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findCurrentAcademicSessions");
				return q.list();
			}
		};
		return getHibernateTemplate().executeFind(hc);
	}

	public AcademicSession getAcademicSession(final String eid) throws IdNotFoundException {
		return (AcademicSession)getObjectByEid(eid, AcademicSessionCmImpl.class.getName(), "findAcademicSessionByEid");
	}
	
	public CourseOffering getCourseOffering(String eid) throws IdNotFoundException {
		return (CourseOffering)getObjectByEid(eid, CourseOfferingCmImpl.class.getName(), "findCourseOfferingByEid");
	}

	public Set getCourseOfferings(String courseSetEid) throws IdNotFoundException {
		return ((CourseSetCmImpl)getCourseSet(courseSetEid)).getCourseOfferings();
	}

	public Set getEquivalentCourseOfferings(String courseOfferingEid) throws IdNotFoundException {
		final CourseOfferingCmImpl courseOffering = (CourseOfferingCmImpl)getCourseOffering(courseOfferingEid);
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
		return getMemberships((CourseOfferingCmImpl)getCourseOffering(courseOfferingEid));
	}

	/**
	 * Gets the memberships for a membership container.  This query can not be
	 * performed using just the container's eid, since it may conflict with other kinds
	 * of objects with the same eid.
	 * 
	 * @param container
	 * @return
	 */
	private Set getMemberships(final AbstractMembershipContainerCmImpl container) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				StringBuffer sb = new StringBuffer("select member from MembershipCmImpl as member, ");
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
		return (Section)getObjectByEid(eid, SectionCmImpl.class.getName(), "findSectionByEid");
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
		return getMemberships((SectionCmImpl)getSection(sectionEid));
	}

	public EnrollmentSet getEnrollmentSet(String eid) throws IdNotFoundException {
		return (EnrollmentSet)getObjectByEid(eid, EnrollmentSetCmImpl.class.getName(), "findEnrollmentSetByEid");
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
		return es.getOfficialInstructors();
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
				Query q = session.getNamedQuery("findCurrentlyInstructingEnrollmentSets");
				q.setParameter("userId", userId);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set findInstructingSections(final String userId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findInstructingSections");
				q.setParameter("userId", userId);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set findEnrolledSections(final String userId) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEnrolledSections");
				q.setParameter("userId", userId);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

	public Set findInstructingSections(final String userId, final String academicSessionEid) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findInstructingSectionsByAcademicSession");
				q.setParameter("userId", userId);
				q.setParameter("academicSessionEid", academicSessionEid);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}

//	public Set findSectionsWithMember(final String userId) {
//		HibernateCallback hc = new HibernateCallback() {
//			public Object doInHibernate(Session session) throws HibernateException {
//				Query q = session.getNamedQuery("findSectionsWithMember");
//				q.setParameter("userId", userId);
//				return q.list();
//			}
//		};
//		return new HashSet(getHibernateTemplate().executeFind(hc));
//	}

//	private MembershipCmImpl getMembership(final String userId, final AbstractMembershipContainerCmImpl container) {
//        final StringBuffer sb = new StringBuffer("select member from MembershipCmImpl as member, ");
//		sb.append(container.getClass().getName());
//        sb.append(" as container where member.memberContainer=container ");
//        sb.append("and container.eid=:eid ");
//    	sb.append("and member.userId=:userId");
//    	
//		HibernateCallback hc = new HibernateCallback() {
//			public Object doInHibernate(Session session) throws HibernateException {
//				Query q = session.createQuery(sb.toString());
//				q.setParameter("eid", container.getEid());
//				q.setParameter("userId", userId);
//				return q.uniqueResult();
//			}
//		};
//		return (MembershipCmImpl)getHibernateTemplate().execute(hc);
//	}

//	public String getSectionRole(final String sectionEid, final String userId) {
//		SectionCmImpl section = (SectionCmImpl)getSection(sectionEid);
//		Membership member = getMembership(userId, section);
//		if(member == null) {
//			return null;
//		} else {
//			return member.getRole();
//		}
//	}


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


	public List findCourseSets(final String category) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findCourseSetByCategory");
				q.setParameter("category", category);
				return q.list();
			}
		};
		return getHibernateTemplate().executeFind(hc);
	}


	public Map findCourseOfferingRoles(final String userEid) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findCourseOfferingRoles");
				q.setParameter("userEid", userEid);
				return q.list();
			}
		};
		List results = getHibernateTemplate().executeFind(hc);
		Map courseOfferingRoleMap = new HashMap();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			courseOfferingRoleMap.put(oa[0], oa[1]);
		}
		return courseOfferingRoleMap;
	}


	public Map findCourseSetRoles(final String userEid) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findCourseSetRoles");
				q.setParameter("userEid", userEid);
				return q.list();
			}
		};
		List results = getHibernateTemplate().executeFind(hc);
		Map courseSetRoleMap = new HashMap();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			courseSetRoleMap.put(oa[0], oa[1]);
		}
		return courseSetRoleMap;
	}


	public Map findSectionRoles(final String userEid) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findSectionRoles");
				q.setParameter("userEid", userEid);
				return q.list();
			}
		};
		List results = getHibernateTemplate().executeFind(hc);
		Map sectionRoleMap = new HashMap();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			sectionRoleMap.put(oa[0], oa[1]);
		}
		return sectionRoleMap;
	}
}
