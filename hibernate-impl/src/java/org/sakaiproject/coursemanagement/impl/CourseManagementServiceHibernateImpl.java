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
	 * since it expects the parameterized query to use "eid" as the named parameter.
	 * 
	 * @param eid The eid of the object we're trying to load
	 * @param className The name of the class / interface we're looking for
	 * @param namedQuery The name of the query
	 * @return The object, if found
	 * @throws IdNotFoundException
	 */
	private Object getObjectById(final String eid, final String className, final String namedQuery) throws IdNotFoundException {
		HibernateCallback hc = new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query q = session.getNamedQuery(namedQuery);
				q.setParameter("eid", eid);
				if(log.isDebugEnabled()) log.debug("Get object by eid: " + q.getQueryString());
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
		return (CourseSet)getObjectById(eid, CourseSet.class.getName(), "findCourseSetById");
	}

	public Set getChildCourseSets(String parentCourseSetId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getCourseSets() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getCourseSetMembers(String courseSetId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public CanonicalCourse getCanonicalCourse(String eid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getEquivalentCanonicalCourses(String canonicalCourseId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getCanonicalCourses(String courseSetId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
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
		return (AcademicSession)getObjectById(eid, AcademicSession.class.getName(), "findAcademicSessionById");
	}
	
	public CourseOffering getCourseOffering(String eid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getEquivalentCourseOfferings(String courseOfferingId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getCourseOfferingMembers(String courseOfferingId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Section getSection(String eid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSections(String courseOfferingId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getChildSections(String parentSectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSectionMembers(String sectionId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public EnrollmentSet getEnrollmentSet(String eid) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getEnrollmentSets(String courseOfferingId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getEnrollments(String enrollmentSetId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getOfficialGraderIds(String enrollmentSetId) throws IdNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
