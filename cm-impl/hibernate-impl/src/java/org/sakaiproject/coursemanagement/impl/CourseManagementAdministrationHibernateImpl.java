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

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

public class CourseManagementAdministrationHibernateImpl extends
		HibernateDaoSupport implements CourseManagementAdministration {

	private static final Log log = LogFactory.getLog(CourseManagementAdministrationHibernateImpl.class);
	
	CourseManagementService cmService;
	
	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}
	
	public void createAcademicSession(String eid, String title,
			String description, Date startDate, Date endDate) throws IdExistsException {
		AcademicSessionImpl academicSession = new AcademicSessionImpl(eid, title, description, startDate, endDate);
		try {
			getHibernateTemplate().save(academicSession);
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, AcademicSession.class.getName());
		}
	}

	public void updateAcademicSession(AcademicSession academicSession) {
		getHibernateTemplate().update(academicSession);
	}

	public void createCourseSet(String eid, String title, String description, CourseSet parent) throws IdExistsException {
		CourseSetImpl courseSet = new CourseSetImpl(eid, title, description, parent);
		try {
			getHibernateTemplate().save(courseSet);
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, CourseSet.class.getName());
		}
	}

	public void updateCourseSet(CourseSet courseSet) {
		getHibernateTemplate().update(courseSet);
	}

	public void createCanonicalCourse(String eid, String title, String description) throws IdExistsException {
		CanonicalCourseImpl canonCourse = new CanonicalCourseImpl(eid, title, description);
		try {
			getHibernateTemplate().save(canonCourse);
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, CanonicalCourse.class.getName());
		}
	}

	public void updateCanonicalCourse(CanonicalCourse canonicalCourse) {
		getHibernateTemplate().update(canonicalCourse);
	}

	public void addCanonicalCourseToCourseSet(String courseSetEid, String canonicalCourseEid) throws IdNotFoundException {
		CourseSetImpl cSet = (CourseSetImpl)cmService.getCourseSet(courseSetEid);
		CanonicalCourseImpl canonCourse = (CanonicalCourseImpl)cmService.getCanonicalCourse(canonicalCourseEid);
		cSet.getCanonicalCourses().add(canonCourse);
		getHibernateTemplate().update(cSet);
	}

	public boolean removeCanonicalCourseFromCourseSet(String courseSetEid, String canonicalCourseEid) {
		CourseSetImpl cSet = (CourseSetImpl)cmService.getCourseSet(courseSetEid);
		CanonicalCourseImpl canonCourse = (CanonicalCourseImpl)cmService.getCanonicalCourse(canonicalCourseEid);
		boolean wasMember = cSet.getCanonicalCourses().remove(canonCourse);
		getHibernateTemplate().update(cSet);
		return wasMember;
	}

	public void setEquivalentCanonicalCourses(Set canonicalCourses) {
		CrossListing newCrossListing = new CrossListing();
		getHibernateTemplate().save(newCrossListing);
		Set oldCrossListings = new HashSet();

		for(Iterator iter = canonicalCourses.iterator(); iter.hasNext();) {
			CanonicalCourseImpl cc = (CanonicalCourseImpl)iter.next();
			CrossListing oldCrossListing = cc.getCrossListing();
			if(oldCrossListing != null) {
				oldCrossListings.add(oldCrossListing);
			}
			if(log.isDebugEnabled()) log.debug("Setting crosslisting for CanonicalCourse " + cc.getEid() + " to " + newCrossListing.getKey());
			cc.setCrossListing(newCrossListing);
			getHibernateTemplate().update(cc);
		}
		
		// TODO Clean up orphaned cross listings
	}

	public boolean removeEquivalency(CanonicalCourse canonicalCourse) {
		// TODO Auto-generated method stub
		return false;
	}

	public void createCourseOffering(String eid, String title, String description, AcademicSession academicSession, Date startDate, Date endDate) throws IdExistsException {
		// TODO Auto-generated method stub
		
	}

	public void updateCourseOffering(CourseOffering courseOffering) {
		// TODO Auto-generated method stub
		
	}

	public void setEquivalentCourseOfferings(Set courseOfferings) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeEquivalency(CourseOffering courseOffering) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addCourseOfferingToCourseSet(String courseSetEid, String courseOfferingEid) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeCourseOfferingFromCourseSet(String courseSetEid, String courseOfferingEid) {
		// TODO Auto-generated method stub
		return false;
	}

	public void createEnrollmentSet(String eid, String title, String description, String category, String defaultEnrollmentCredits, Set officialGraders) throws IdExistsException {
		// TODO Auto-generated method stub
		
	}

	public void updateEnrollmentSet(EnrollmentSet enrollmentSet) {
		// TODO Auto-generated method stub
		
	}

	public void addEnrollment(String userId, EnrollmentSet enrollmentSet, String enrollmentStatus, String credits, String gradingScheme) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeEnrollment(String userId, String enrollmentSetEid) {
		// TODO Auto-generated method stub
		return false;
	}

	public void createSection(String eid, String title, String description, String category, Section parent, CourseOffering courseOffering, EnrollmentSet enrollmentSet) throws IdExistsException {
		// TODO Auto-generated method stub
		
	}

	public void updateSection(Section section) {
		// TODO Auto-generated method stub
		
	}

	public void addCourseSetMembership(String userId, String role, String courseSetEid) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeCourseSetMembership(String userId, String courseSetEid) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addCourseOfferingMembership(String userId, String role, String courseOfferingEid) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeCourseOfferingMembership(String userId, String courseOfferingEid) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addSectionMembership(String userId, String role, String sectionEid) {
		// TODO Auto-generated method stub
		
	}

	public boolean removeSectionMembership(String userId, String sectionEid) {
		// TODO Auto-generated method stub
		return false;
	}

}
