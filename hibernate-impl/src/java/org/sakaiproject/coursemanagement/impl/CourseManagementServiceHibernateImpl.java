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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

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
				Query q = session.getNamedQuery(namedQuery);
				q.setParameter("eid", eid);
//				if(log.isDebugEnabled()) log.debug("Get object by eid: " + q.getQueryString());
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
	 * A generic approach to finding members in a membership container.
	 * 
	 * @param membershipContainerEid The membership container's eid
	 * @return The Set of Memberships
	 * @throws IdNotFoundException
	 */
	private Set getMembers(final String membershipContainerEid) throws IdNotFoundException {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				// Ensure that the container exists before looking for members
				Query containerExists = session.getNamedQuery("memberContainerExists").setParameter("eid", membershipContainerEid);
				if(((Integer)containerExists.iterate().next()).intValue() == 0) {
					throw new IdNotFoundException(membershipContainerEid, MembershipContainer.class.getName());
				}
				// Now look for members
				Query q = session.getNamedQuery("findMembers");
				q.setParameter("eid", membershipContainerEid);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));
	}
	
	/**
	 * Gets equivalent CrossListables.
	 * 
	 * @param crossListable The CrossListable for which to find equivalents
	 * @return
	 */
	private Set getEquivalents(final CrossListable crossListable) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findEquivalents");
				q.setParameter("crossListing", crossListable.getCrossListing());
				q.setParameter("crossListable", crossListable);
				return q.list();
			}
		};
		return new HashSet(getHibernateTemplate().executeFind(hc));

	}

	public CourseSet getCourseSet(String eid) throws IdNotFoundException {
		return (CourseSet)getObjectByEid(eid, CourseSetImpl.class.getName(), "findCourseSetByEid");
	}

	// TODO Use getObjectByEid() instead of replicating code
	public Set getChildCourseSets(final String parentCourseSetEid) {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery("findChildCourseSets");
				q.setParameter("parentEid", parentCourseSetEid);
				return q.list();
			}
		};
		try {
			return new HashSet(getHibernateTemplate().executeFind(hc));
		} catch (Exception e) {
			throw new IdNotFoundException(parentCourseSetEid, CourseSetImpl.class.getName());
		}
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

	public Set getCourseSetMembers(final String courseSetEid) throws IdNotFoundException {
		return getMembers(courseSetEid);
	}

	public CanonicalCourse getCanonicalCourse(String eid) throws IdNotFoundException {
		return (CanonicalCourse)getObjectByEid(eid, CanonicalCourseImpl.class.getName(), "findCanonicalCourseByEid");
	}

	public Set getEquivalentCanonicalCourses(String canonicalCourseEid) {
		final CanonicalCourseImpl canonicalCourse = (CanonicalCourseImpl)getCanonicalCourse(canonicalCourseEid);
		return getEquivalents(canonicalCourse);
	}

	public Set getCanonicalCourses(final String courseSetEid) throws IdNotFoundException {
		return ((CourseSetImpl)getCourseSet(courseSetEid)).getCourseOfferings();
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
		// TODO Solve the courseSet problem for both CanonicalCourses and CourseOfferings
		return null;
	}

	public Set getEquivalentCourseOfferings(String courseOfferingEid) throws IdNotFoundException {
		CourseOfferingImpl courseOffering = (CourseOfferingImpl)getCourseOffering(courseOfferingEid);
		return getEquivalents(courseOffering);
	}

	public Set getCourseOfferingMembers(String courseOfferingEid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Section getSection(String eid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSections(String courseOfferingEid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getChildSections(String parentSectionEid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSectionMembers(String sectionEid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public EnrollmentSet getEnrollmentSet(String eid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getEnrollmentSets(String courseOfferingEid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getEnrollments(String enrollmentSetEid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getOfficialGraderIds(String enrollmentSetEid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
