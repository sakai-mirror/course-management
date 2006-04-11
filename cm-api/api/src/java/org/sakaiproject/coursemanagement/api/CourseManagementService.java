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
package org.sakaiproject.coursemanagement.api;

import java.util.Set;

import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * A read-only service that queries enterprise course, section, membership, and
 * enrollment data.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface CourseManagementService {

	/**
	 * Gets a CourseSet by its eid.
	 * 
	 * @param eid The CourseSet's unique eid
	 * @return The CourseSet
	 * @throws IdNotFoundException If the eid is not associated with any CourseSet
	 */
	public CourseSet getCourseSet(String eid) throws IdNotFoundException;
	
	/**
	 * Gets the child CourseSet from a parent CourseSet.
	 * 
	 * @param parentCourseSetEid The parent CourseSet eid
	 * @return The Set of child CourseSets
	 */
	public Set getChildCourseSets(String parentCourseSetEid);

	/**
	 * Gets all of the top level CourseSets
	 * 
	 * @return The Set of CourseSets that have no parent CourseSet
	 */
	public Set getCourseSets();
		
	/**
	 * Gets the memberships directly contained by this CourseSet.
	 * 
	 * @param courseSetEid
	 * @return The set of memberships in this CourseSet.  This is not a transitive
	 * set.
	 * @throws IdNotFoundException If the eid is not associated with any CourseSet
	 */
	public Set getCourseSetMemberships(String courseSetEid) throws IdNotFoundException;

	/**
	 * Gets a CanonicalCourse by its eid.
	 * 
	 * @param eid
	 * @return The CanonicalCourse
	 * @throws IdNotFoundException If the eid is not associated with any CanonicalCourse
	 */
	public CanonicalCourse getCanonicalCourse(String eid) throws IdNotFoundException;
	
	/**
	 * Gets the equivalent CanonicalCourses.
	 * 
	 * @param canonicalCourseEid The eid of the CanonicalCourse to use in finding equivalents
	 * @return The set of CanonicalCourses that are equivalent (in the Enterprise
	 * view, not in the Java view -- this is independent of CanonicalCourse.equals()).
	 */
	public Set getEquivalentCanonicalCourses(String canonicalCourseEid);

	/**
	 * Gets the CanonicalCourses in a CourseSet.
	 * 
	 * @param courseSetEid The eid of the CourseSet
	 * @return The set of CanonicalCourses in the CourseSet
	 * @throws IdNotFoundException If the eid is not associated with any CourseSet
	 */
	public Set getCanonicalCourses(String courseSetEid) throws IdNotFoundException;

	/**
	 * Gets the set of all known AcademicSessions.
	 * 
	 * @return
	 */
	public Set getAcademicSessions();
	
	/**
	 * Gets a AcademicSession by its eid.
	 * @param eid
	 * @return The AcademicSession
	 * @throws IdNotFoundException If the eid is not associated with any AcademicSession
	 */
	public AcademicSession getAcademicSession(String eid) throws IdNotFoundException;
	
	/**
	 * Gets a CourseOffering by its eid.
	 * 
	 * @param eid
	 * @return The CourseOffering
	 * @throws IdNotFoundException If the eid is not associated with any CourseOffering
	 */
	public CourseOffering getCourseOffering(String eid) throws IdNotFoundException;

	/**
	 * Gets any equivalent CourseOfferings.
	 * 
	 * @param courseOfferingEid The eid of the CourseOffering to use in finding equivalents
	 * @return The set of CourseOfferings that are equivalent (in the Enterprise
	 * view, not in the Java view -- this is independent of CourseOffering.equals()).
	 * @throws IdNotFoundException If the eid is not associated with any CourseOffering
	 */
	public Set getEquivalentCourseOfferings(String courseOfferingEid) throws IdNotFoundException;

	/**
	 * Gets the memberships directly contained by this CourseOffering.
	 * 
	 * @param courseOfferingEid
	 * @return The set of memberships in this CourseOffering.  This is not a transitive
	 * set.
	 * @throws IdNotFoundException If the eid is not associated with any CourseOffering
	 */
	public Set getCourseOfferingMemberships(String courseOfferingEid) throws IdNotFoundException;

	/**
	 * Gets the CourseOfferings in a CourseSet.
	 * 
	 * @param courseSetEid The eid of the CourseSet
	 * @return The set of CourseOfferings in the CourseSet
	 * @throws IdNotFoundException If the eid is not associated with any CourseSet
	 */
	public Set getCourseOfferings(String courseSetEid) throws IdNotFoundException;

	/**
	 * Gets a Section by its eid.
	 * 
	 * @param eid
	 * @return The Section
	 * @throws IdNotFoundException If the eid is not associated with any Section
	 */
	public Section getSection(String eid) throws IdNotFoundException;

	/**
	 * Gets the top-level Sections associated with a CourseOffering
	 * 
	 * @param courseOfferingEid
	 * @return The Set of Sections
	 * @throws IdNotFoundException If the eid is not associated with any CourseOffering
	 */
	public Set getSections(String courseOfferingEid) throws IdNotFoundException;

	/**
	 * Gets the child Sections from a parent Section.
	 * 
	 * @param parentSectionEid The parent Section eid
	 * @return The Set of child Sections
	 * @throws IdNotFoundException If the eid is not associated with any parent Section
	 */
	public Set getChildSections(String parentSectionEid) throws IdNotFoundException;

	/**
	 * Gets the members directly contained by this Section.
	 * 
	 * @param sectionEid
	 * @return The set of members in this Section.  This is not a transitive
	 * set.
	 * @throws IdNotFoundException If the eid is not associated with any Section
	 */
	public Set getSectionMembers(String sectionEid) throws IdNotFoundException;
	
	/**
	 * Gets an EnrollmentSet by its eid.
	 * 
	 * @param eid
	 * @return The EnrollmentSet
	 * @throws IdNotFoundException If the eid is not associated with any EnrollmentSet
	 */
	public EnrollmentSet getEnrollmentSet(String eid) throws IdNotFoundException;

	/**
	 * Gets the EnrollmentSets associated with a CourseOffering
	 * 
	 * @param courseOfferingEid
	 * @return The Set of EnrollmentSets
	 * @throws IdNotFoundException If the eid is not associated with any CourseOffering
	 */
	public Set getEnrollmentSets(String courseOfferingEid) throws IdNotFoundException;

	/**
	 * Gets the Enrollments in an EnrollmentSet
	 * 
	 * @param enrollmentSetEid
	 * @return The Set of Enrollments
	 * @throws IdNotFoundException If the eid is not associated with any EnrollmentSet
	 */
	public Set getEnrollments(String enrollmentSetEid) throws IdNotFoundException;

	/**
	 * Gets the set of user ids that are sanctioned by the enterprise to grade
	 * students enrolled in this EnrollmentSet.
	 * 
	 * @param enrollmentSetEid
	 * @return The set of ids for users who can grade students in this EnrollmentSet
	 * @throws IdNotFoundException If the eid is not associated with any EnrollmentSet
	 */
	public Set getOfficialGraderIds(String enrollmentSetEid) throws IdNotFoundException;
	
	/**
	 * Determines whether a user is enrolled in one of the EnrollmentSets.  This
	 * method is needed to implement Sakai's GroupProvider.
	 * 
	 * @param userId The student's userId
	 * @param enrollmentSetEids The set of EnrollmentSetEids
	 * @return
	 */
	public boolean isEnrolled(String userId, Set enrollmentSetEids);

	/**
	 * Gets the set of current EnrollmentSets for which a user is enrolled.
	 * An EnrollmentSet is considered current if its CourseOffering's start date
	 * (is null or prior to the current date/time) and its end date (is null or
	 * after the current date/time).
	 * 
	 * @param userId
	 * @return
	 */
	public Set getCurrentlyEnrolledEnrollmentSets(String userId);

	/**
	 * Gets the set of current EnrollmentSets for which a user is an official grader.
	 * An EnrollmentSet is considered current if its CourseOffering's start date
	 * (is null or prior to the current date/time) and its end date (is null or
	 * after the current date/time).
	 * 
	 * @param userId
	 * @return
	 */
	public Set getCurrentlyGradableEnrollmentSets(String userId);
}
