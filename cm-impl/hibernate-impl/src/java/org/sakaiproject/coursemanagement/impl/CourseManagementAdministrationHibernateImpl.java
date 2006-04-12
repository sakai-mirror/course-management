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
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
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
		CourseSetImpl courseSet = (CourseSetImpl)cmService.getCourseSet(courseSetEid);
		CanonicalCourseImpl canonCourse = (CanonicalCourseImpl)cmService.getCanonicalCourse(canonicalCourseEid);
		courseSet.getCanonicalCourses().add(canonCourse);
		getHibernateTemplate().update(courseSet);
	}

	public boolean removeCanonicalCourseFromCourseSet(String courseSetEid, String canonicalCourseEid) {
		CourseSetImpl courseSet = (CourseSetImpl)cmService.getCourseSet(courseSetEid);
		CanonicalCourseImpl canonCourse = (CanonicalCourseImpl)cmService.getCanonicalCourse(canonicalCourseEid);
		Set courses = courseSet.getCanonicalCourses();
		if(courses == null || ! courses.contains(canonCourse)) {
			return false;
		}
		courses.remove(canonCourse);
		getHibernateTemplate().update(courseSet);
		return true;
	}

	private void setEquivalents(Set crossListables) {
		CrossListing newCrossListing = new CrossListing();
		getHibernateTemplate().save(newCrossListing);
		Set oldCrossListings = new HashSet();

		for(Iterator iter = crossListables.iterator(); iter.hasNext();) {
			CrossListable clable = (CrossListable)iter.next();
			CrossListing oldCrossListing = clable.getCrossListing();
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

	private boolean removeEquiv(CrossListable impl) {
		boolean hadCrossListing = impl.getCrossListing() != null;
		impl.setCrossListing(null);
		getHibernateTemplate().update(impl);
		return hadCrossListing;
	}
	
	public boolean removeEquivalency(CanonicalCourse canonicalCourse) {
		return removeEquiv((CanonicalCourseImpl)canonicalCourse);
	}

	public void createCourseOffering(String eid, String title, String description, AcademicSession academicSession, Date startDate, Date endDate) throws IdExistsException {
		CourseOfferingImpl co = new CourseOfferingImpl(eid, title, description, academicSession, startDate, endDate);
		try {
			getHibernateTemplate().save(co);
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
		return removeEquiv((CrossListable)courseOffering);
	}

	public void addCourseOfferingToCourseSet(String courseSetEid, String courseOfferingEid) {
		// CourseSet's set of courses are controlled on the CourseSet side of the bi-directional relationship
		CourseSetImpl courseSet = (CourseSetImpl)cmService.getCourseSet(courseSetEid);
		CourseOffering courseOffering = cmService.getCourseOffering(courseOfferingEid);
		Set offerings = courseSet.getCourseOfferings();
		if(offerings == null) {
			offerings = new HashSet();
			courseSet.setCourseOfferings(offerings);
		}
		offerings.add(courseOffering);
		getHibernateTemplate().update(courseSet);
	}

	public boolean removeCourseOfferingFromCourseSet(String courseSetEid, String courseOfferingEid) {
		CourseSetImpl courseSet = (CourseSetImpl)cmService.getCourseSet(courseSetEid);
		CourseOffering courseOffering = cmService.getCourseOffering(courseOfferingEid);
		Set offerings = courseSet.getCourseOfferings();
		if(offerings == null || ! offerings.contains(courseOffering)) {
			return false;
		}
		offerings.remove(courseOffering);
		getHibernateTemplate().update(courseSet);
		return true;
	}

	public void createEnrollmentSet(String eid, String title, String description, String category, String defaultEnrollmentCredits, Set officialGraders) throws IdExistsException {
		EnrollmentSetImpl enrollmentSet = new EnrollmentSetImpl(eid, title, description, category, defaultEnrollmentCredits, officialGraders);
		try {
			getHibernateTemplate().save(enrollmentSet);
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, EnrollmentSet.class.getName());
		}
	}

	public void updateEnrollmentSet(EnrollmentSet enrollmentSet) {
		getHibernateTemplate().update(enrollmentSet);
	}

	public void addOrUpdateEnrollment(String userId, EnrollmentSet enrollmentSet, String enrollmentStatus, String credits, String gradingScheme) {
		if(cmService.isEnrolled(userId,enrollmentSet.getEid())) {
			EnrollmentImpl enrollment = (EnrollmentImpl)cmService.getEnrollment(userId, enrollmentSet.getEid());
			enrollment.setEnrollmentStatus(enrollmentStatus);
			enrollment.setCredits(credits);
			enrollment.setGradingScheme(gradingScheme);
			getHibernateTemplate().update(enrollment);
		} else {
			EnrollmentImpl enrollment = new EnrollmentImpl(userId, enrollmentSet, enrollmentStatus, credits, gradingScheme);
			getHibernateTemplate().save(enrollment);
		}
	}

	public boolean removeEnrollment(String userId, String enrollmentSetEid) {
		EnrollmentImpl enr = (EnrollmentImpl)cmService.getEnrollment(userId, enrollmentSetEid);
		if(enr == null) {
			return false;
		} else {
			enr.setDropped(true);
			getHibernateTemplate().update(enr);
			return true;
		}
	}

	public void createSection(String eid, String title, String description, String category, Section parent, CourseOffering courseOffering, EnrollmentSet enrollmentSet) throws IdExistsException {
		SectionImpl section = new SectionImpl(eid, title, description, category, parent, courseOffering, enrollmentSet);
		try {
			getHibernateTemplate().save(section);
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, Section.class.getName());
		}
	}

	public void updateSection(Section section) {
		getHibernateTemplate().update(section);
	}

	public void addCourseSetMembership(String userId, String role, String courseSetEid) throws IdNotFoundException {
		CourseSetImpl courseSet = (CourseSetImpl)cmService.getCourseSet(courseSetEid);
		Set memberships = courseSet.getMembers();
		if(memberships == null) {
			memberships = new HashSet();
			courseSet.setMembers(memberships);
		}
		// Check to see if this user is already a member
		boolean alreadyMember = false;
		for(Iterator iter = memberships.iterator(); iter.hasNext();) {
			MembershipImpl member = (MembershipImpl)iter.next();
			if(member.getUserId().equals(userId)) {
				alreadyMember = true;
				member.setRole(role);
				break;
			}
		}
		if(!alreadyMember) {
			memberships.add(new MembershipImpl(userId, role));
		}
		getHibernateTemplate().update(courseSet);
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
