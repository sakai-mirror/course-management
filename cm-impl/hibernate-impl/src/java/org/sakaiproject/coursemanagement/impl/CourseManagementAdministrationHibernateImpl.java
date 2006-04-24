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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

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
import org.springframework.orm.hibernate.HibernateCallback;
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

	public void createCourseSet(String eid, String title, String description,
			String parentCourseSetEid) throws IdExistsException {
		CourseSet parent = null;
		if(parentCourseSetEid != null) {
			parent = cmService.getCourseSet(parentCourseSetEid);
		}
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
		Set canonCourses = courseSet.getCanonicalCourses();
		if(canonCourses == null) {
			canonCourses = new HashSet();
			courseSet.setCanonicalCourses(canonCourses);
		}
		canonCourses.add(canonCourse);
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

	public void createCourseOffering(String eid, String title, String description,
			String academicSessionEid, String canonicalCourseEid, Date startDate, Date endDate) throws IdExistsException {
		AcademicSession as = cmService.getAcademicSession(academicSessionEid);
		CanonicalCourse cc = cmService.getCanonicalCourse(canonicalCourseEid);
		CourseOfferingImpl co = new CourseOfferingImpl(eid, title, description, as, cc, startDate, endDate);
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

	public void createEnrollmentSet(String eid, String title, String description, String category,
			String defaultEnrollmentCredits, String courseOfferingEid, Set officialGraders)
			throws IdExistsException {
		if(courseOfferingEid == null) {
			throw new IllegalArgumentException("You can not create an EnrollmentSet without specifying a courseOffering");
		}
		CourseOffering co = cmService.getCourseOffering(courseOfferingEid);
		EnrollmentSetImpl enrollmentSet = new EnrollmentSetImpl(eid, title, description, category, defaultEnrollmentCredits, co, officialGraders);
		try {
			getHibernateTemplate().save(enrollmentSet);
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, EnrollmentSet.class.getName());
		}
	}

	public void updateEnrollmentSet(EnrollmentSet enrollmentSet) {
		getHibernateTemplate().update(enrollmentSet);
	}

	public void addOrUpdateEnrollment(String userId, String enrollmentSetEid, String enrollmentStatus, String credits, String gradingScheme) {
		if(cmService.isEnrolled(userId,enrollmentSetEid)) {
			EnrollmentImpl enrollment = (EnrollmentImpl)cmService.getEnrollment(userId, enrollmentSetEid);
			enrollment.setEnrollmentStatus(enrollmentStatus);
			enrollment.setCredits(credits);
			enrollment.setGradingScheme(gradingScheme);
			getHibernateTemplate().update(enrollment);
		} else {
			EnrollmentSet enrollmentSet = cmService.getEnrollmentSet(enrollmentSetEid);
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

	public void createSection(String eid, String title, String description, String category,
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

		SectionImpl section = new SectionImpl(eid, title, description, category, parent, co, es);
		try {
			getHibernateTemplate().save(section);
		} catch (DataIntegrityViolationException dive) {
			throw new IdExistsException(eid, Section.class.getName());
		}
	}

	public void updateSection(Section section) {
		getHibernateTemplate().update(section);
	}
	
	public void addOrUpdateCourseSetMembership(final String userId, String role, final String courseSetEid) throws IdNotFoundException {
		CourseSetImpl cs = (CourseSetImpl)cmService.getCourseSet(courseSetEid);
		MembershipImpl member =getMembership(userId, cs);
		if(member == null) {
			// Add the new member
			member = new MembershipImpl(userId, role, cs);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			getHibernateTemplate().update(member);
		}
	}

	public boolean removeCourseSetMembership(String userId, String courseSetEid) {
		MembershipImpl member = getMembership(userId, (CourseSetImpl)cmService.getCourseSet(courseSetEid));
		if(member == null) {
			return false;
		} else {
			getHibernateTemplate().delete(member);
			return true;
		}
	}

	public void addOrUpdateCourseOfferingMembership(String userId, String role, String courseOfferingEid) {
		CourseOfferingImpl co = (CourseOfferingImpl)cmService.getCourseOffering(courseOfferingEid);
		MembershipImpl member =getMembership(userId, co);
		if(member == null) {
			// Add the new member
			member = new MembershipImpl(userId, role, co);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			getHibernateTemplate().update(member);
		}
	}

	public boolean removeCourseOfferingMembership(String userId, String courseOfferingEid) {
		CourseOfferingImpl courseOffering = (CourseOfferingImpl)cmService.getCourseOffering(courseOfferingEid);
		MembershipImpl member = getMembership(userId, courseOffering);
		if(member == null) {
			return false;
		} else {
			getHibernateTemplate().delete(member);
			return true;
		}
	}
	
	public void addOrUpdateSectionMembership(String userId, String role, String sectionEid) {
		SectionImpl sec = (SectionImpl)cmService.getSection(sectionEid);
		MembershipImpl member =getMembership(userId, sec);
		if(member == null) {
			// Add the new member
			member = new MembershipImpl(userId, role, sec);
			getHibernateTemplate().save(member);
		} else {
			// Update the existing member
			member.setRole(role);
			getHibernateTemplate().update(member);
		}
	}

	public boolean removeSectionMembership(String userId, String sectionEid) {
		SectionImpl sec = (SectionImpl)cmService.getSection(sectionEid);
		MembershipImpl member = getMembership(userId, sec);
		if(member == null) {
			return false;
		} else {
			getHibernateTemplate().delete(member);
			return true;
		}
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

}
