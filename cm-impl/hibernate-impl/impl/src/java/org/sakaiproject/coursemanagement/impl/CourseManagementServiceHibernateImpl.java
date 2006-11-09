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
import org.sakaiproject.coursemanagement.api.SectionCategory;
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
	
	public CourseSet getCourseSet(String eid) throws IdNotFoundException {
		return (CourseSet)getObjectByEid(eid, CourseSetCmImpl.class.getName());
	}

	public Set getChildCourseSets(final String parentCourseSetEid) throws IdNotFoundException {
		// Ensure that the parent exists
		if(!isCourseSetDefined(parentCourseSetEid)) {
			throw new IdNotFoundException(parentCourseSetEid, CourseSetCmImpl.class.getName());
		}
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findChildCourseSets", "parentEid", parentCourseSetEid));
	}

	public Set getCourseSets() {
		return new HashSet(getHibernateTemplate().findByNamedQuery("findTopLevelCourseSets"));
	}

	public Set getCourseSetMemberships(String courseSetEid) throws IdNotFoundException {
		return getMemberships((CourseSetCmImpl)getCourseSet(courseSetEid));
	}

	public CanonicalCourse getCanonicalCourse(String eid) throws IdNotFoundException {
		return (CanonicalCourse)getObjectByEid(eid, CanonicalCourseCmImpl.class.getName());
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
		return getHibernateTemplate().findByNamedQuery("findAcademicSessions");
	}

	public List getCurrentAcademicSessions() {
		return getHibernateTemplate().findByNamedQuery("findCurrentAcademicSessions");
	}

	public AcademicSession getAcademicSession(final String eid) throws IdNotFoundException {
		return (AcademicSession)getObjectByEid(eid, AcademicSessionCmImpl.class.getName());
	}
	
	public CourseOffering getCourseOffering(String eid) throws IdNotFoundException {
		return (CourseOffering)getObjectByEid(eid, CourseOfferingCmImpl.class.getName());
	}

	public Set getCourseOfferingsInCourseSet(final String courseSetEid) throws IdNotFoundException {
		if( ! isCourseSetDefined(courseSetEid)) {
			throw new IdNotFoundException(courseSetEid, CourseOfferingCmImpl.class.getName());
		}
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
				StringBuffer sb = new StringBuffer("select mbr from MembershipCmImpl as mbr, ");
					sb.append(container.getClass().getName());
					sb.append(" as container where mbr.memberContainer=container ");
					sb.append("and container.eid=:eid");
				Query q = session.createQuery(sb.toString());
				q.setParameter("eid", container.getEid());
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}


	public Section getSection(String eid) throws IdNotFoundException {
		return (Section)getObjectByEid(eid, SectionCmImpl.class.getName());
	}

	public Set getSections(String courseOfferingEid) throws IdNotFoundException {
		CourseOffering courseOffering = getCourseOffering(courseOfferingEid);
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findTopLevelSectionsInCourseOffering", "courseOffering", courseOffering));
	}

	public Set getChildSections(final String parentSectionEid) throws IdNotFoundException {
		if( ! isSectionDefined(parentSectionEid)) {
			throw new IdNotFoundException(parentSectionEid, SectionCmImpl.class.getName());
		}
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findChildSections", "parentEid", parentSectionEid));
	}

	public Set getSectionMemberships(String sectionEid) throws IdNotFoundException {
		return getMemberships((SectionCmImpl)getSection(sectionEid));
	}

	public EnrollmentSet getEnrollmentSet(String eid) throws IdNotFoundException {
		return (EnrollmentSet)getObjectByEid(eid, EnrollmentSetCmImpl.class.getName());
	}

	public Set getEnrollmentSets(final String courseOfferingEid) throws IdNotFoundException {
		if(! isCourseOfferingDefined(courseOfferingEid)) {
			throw new IdNotFoundException(courseOfferingEid, CourseOfferingCmImpl.class.getName());
		}
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findEnrollmentSetsByCourseOffering", "courseOfferingEid", courseOfferingEid));
	}

	public Set getEnrollments(final String enrollmentSetEid) throws IdNotFoundException {
		if( ! isEnrollmentSetDefined(enrollmentSetEid)) {
			throw new IdNotFoundException(enrollmentSetEid, EnrollmentSetCmImpl.class.getName());
		}
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findEnrollments", "enrollmentSetEid", enrollmentSetEid));
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
		if( ! isEnrollmentSetDefined(enrollmentSetEid)) {
			log.warn("Could not find an enrollment set with eid=" + enrollmentSetEid);
			return null;
		}
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
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam("findCurrentlyEnrolledEnrollmentSets", "userId", userId));
	}


	public Set findCurrentlyInstructingEnrollmentSets(final String userId) {
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findCurrentlyInstructingEnrollmentSets", "userId", userId));
	}

	public Set findInstructingSections(final String userId) {
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findInstructingSections", "userId", userId));
	}

	public Set findEnrolledSections(final String userId) {
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findEnrolledSections", "userId", userId));
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
				return Boolean.valueOf( ! q.iterate().hasNext());
			}
		};
		return ((Boolean)getHibernateTemplate().execute(hc)).booleanValue();
	}


	public List findCourseSets(final String category) {
		return getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findCourseSetByCategory", "category", category);
	}


	public Map findCourseOfferingRoles(final String userEid) {
		// Keep track of CourseOfferings that we've already queried
		Set<String> queriedCourseOfferingEids = new HashSet<String>();
		List results = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findCourseOfferingRoles", "userEid", userEid);
		Map<String, String> courseOfferingRoleMap = new HashMap<String, String>();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			courseOfferingRoleMap.put((String)oa[0], (String)oa[1]);
			queriedCourseOfferingEids.add((String)oa[0]);
		}
		return courseOfferingRoleMap;
	}

	public Map findCourseSetRoles(final String userEid) {
		List results = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findCourseSetRoles", "userEid", userEid);
		Map courseSetRoleMap = new HashMap();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			courseSetRoleMap.put(oa[0], oa[1]);
		}
		return courseSetRoleMap;
	}


	public Map findSectionRoles(final String userEid) {
		List results = getHibernateTemplate().findByNamedQueryAndNamedParam(
				"findSectionRoles", "userEid", userEid);
		Map sectionRoleMap = new HashMap();
		for(Iterator iter = results.iterator(); iter.hasNext();) {
			Object[] oa = (Object[])iter.next();
			sectionRoleMap.put(oa[0], oa[1]);
		}
		return sectionRoleMap;
	}


	public Set getCourseOfferingsInCanonicalCourse(final String canonicalCourseEid) throws IdNotFoundException {
		if(!isCanonicalCourseDefined(canonicalCourseEid)) {
			throw new IdNotFoundException(canonicalCourseEid, CanonicalCourseCmImpl.class.getName());
		}
		return new HashSet(getHibernateTemplate().findByNamedQueryAndNamedParam("findCourseOfferingsByCanonicalCourse", "canonicalCourseEid", canonicalCourseEid));
	}

	public boolean isAcademicSessionDefined(String eid) {
		return ((Integer)getHibernateTemplate().findByNamedQueryAndNamedParam("isAcademicSessionDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public boolean isCanonicalCourseDefined(String eid) {
		return ((Integer)getHibernateTemplate().findByNamedQueryAndNamedParam("isCanonicalCourseDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public boolean isCourseOfferingDefined(String eid) {
		return ((Integer)getHibernateTemplate().findByNamedQueryAndNamedParam("isCourseOfferingDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public boolean isCourseSetDefined(String eid) {
		return ((Integer)getHibernateTemplate().findByNamedQueryAndNamedParam("isCourseSetDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public boolean isEnrollmentSetDefined(String eid) {
		return ((Integer)getHibernateTemplate().findByNamedQueryAndNamedParam("isEnrollmentSetDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public boolean isSectionDefined(String eid) {
		return ((Integer)getHibernateTemplate().findByNamedQueryAndNamedParam("isSectionDefined", "eid", eid).get(0)).intValue() == 1;
	}

	public List getSectionCategories() {
		return getHibernateTemplate().findByNamedQuery("findSectionCategories");
	}

	public String getSectionCategoryDescription(String categoryCode) {
		SectionCategory cat = (SectionCategory)getHibernateTemplate().get(SectionCategoryCmImpl.class, categoryCode);
		if(cat == null) {
			return null;
		} else {
			return cat.getCategoryDescription();
		}
	}

}
