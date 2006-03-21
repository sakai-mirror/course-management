/**********************************************************************************
 * $$URL$$
 * $$Id$$
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
	 * Gets a CourseSet by its id.
	 * 
	 * @param id The CourseSet's unique id
	 * @return The CourseSet
	 * @throws IdNotFoundException If the id is not associated with any CourseSet
	 */
	public CourseSet getCourseSet(String id) throws IdNotFoundException;
	
	/**
	 * Gets the child CourseSet from a parent CourseSet.
	 * 
	 * @param parentCourseSetId The parent CourseSet id
	 * @return The Set of child CourseSets
	 */
	public Set getChildCourseSets(String parentCourseSetId);

	/**
	 * Gets all of the top level CourseSets
	 * 
	 * @return The Set of CourseSets that have no parent CourseSet
	 */
	public Set getCourseSets();
		
	/**
	 * Gets the members directly contained by this CourseSet.
	 * 
	 * @param courseSetId
	 * @return The set of members in this CourseSet.  This is not a transitive
	 * set.
	 * @throws IdNotFoundException If the id is not associated with any CourseSet
	 */
	public Set getCourseSetMembers(String courseSetId) throws IdNotFoundException;

	/**
	 * Gets a CanonicalCourse by its id.
	 * 
	 * @param id
	 * @return The CanonicalCourse
	 * @throws IdNotFoundException If the id is not associated with any CanonicalCourse
	 */
	public CanonicalCourse getCanonicalCourse(String id) throws IdNotFoundException;
	
	/**
	 * Gets the equivalent CanonicalCourses.
	 * 
	 * @param canonicalCourseId The id of the CanonicalCourse to use in finding equivalents
	 * @return The set of CanonicalCourses that are equivalent (in the Enterprise
	 * view, not in the Java view -- this is independent of CanonicalCourse.equals()).
	 */
	public Set getEquivalentCanonicalCourses(String canonicalCourseId);

	/**
	 * Gets the CanonicalCourses in a CourseSet.
	 * 
	 * @param courseSetId The id of the CourseSet
	 * @return The set of CanonicalCourses in the CourseSet
	 * @throws IdNotFoundException If the id is not associated with any CourseSet
	 */
	public Set getCanonicalCourses(String courseSetId) throws IdNotFoundException;

	/**
	 * Gets the set of all known AcademicSessions.
	 * 
	 * @return
	 */
	public Set getAcademicSessions();
	
	/**
	 * Gets a AcademicSession by its id.
	 * @param id
	 * @return The AcademicSession
	 * @throws IdNotFoundException If the id is not associated with any AcademicSession
	 */
	public AcademicSession getAcademicSession(String id) throws IdNotFoundException;
	
	/**
	 * Gets a CourseOffering by its id.
	 * 
	 * @param id
	 * @return The CourseOffering
	 * @throws IdNotFoundException If the id is not associated with any CourseOffering
	 */
	public CourseOffering getCourseOffering(String id) throws IdNotFoundException;

	/**
	 * Gets any equivalent CourseOfferings.
	 * 
	 * @param courseOfferingId The id of the CourseOffering to use in finding equivalents
	 * @return The set of CourseOfferings that are equivalent (in the Enterprise
	 * view, not in the Java view -- this is independent of CourseOffering.equals()).
	 * @throws IdNotFoundException If the id is not associated with any CourseOffering
	 */
	public Set getEquivalentCourseOfferings(String courseOfferingId) throws IdNotFoundException;

	/**
	 * Gets the members directly contained by this CourseOffering.
	 * 
	 * @param courseOfferingId
	 * @return The set of members in this CourseOffering.  This is not a transitive
	 * set.
	 * @throws IdNotFoundException If the id is not associated with any CourseOffering
	 */
	public Set getCourseOfferingMembers(String courseOfferingId) throws IdNotFoundException;

	/**
	 * Gets a Section by its id.
	 * 
	 * @param id
	 * @return The Section
	 * @throws IdNotFoundException If the id is not associated with any Section
	 */
	public Section getSection(String id) throws IdNotFoundException;

	/**
	 * Gets the Sections associated with a CourseOffering
	 * 
	 * @param courseOfferingId
	 * @return The Set of Sections
	 * @throws IdNotFoundException If the id is not associated with any CourseOffering
	 */
	public Set getSections(String courseOfferingId) throws IdNotFoundException;

	/**
	 * Gets the child Sections from a parent Section.
	 * 
	 * @param parentSectionId The parent Section id
	 * @return The Set of child Sections
	 */
	public Set getChildSections(String parentSectionId);

	/**
	 * Gets the members directly contained by this Section.
	 * 
	 * @param sectionId
	 * @return The set of members in this Section.  This is not a transitive
	 * set.
	 * @throws IdNotFoundException If the id is not associated with any Section
	 */
	public Set getSectionMembers(String sectionId) throws IdNotFoundException;
	
	/**
	 * Gets an EnrollmentSet by its id.
	 * 
	 * @param id
	 * @return The EnrollmentSet
	 * @throws IdNotFoundException If the id is not associated with any EnrollmentSet
	 */
	public EnrollmentSet getEnrollmentSet(String id) throws IdNotFoundException;

	/**
	 * Gets the EnrollmentSets associated with a CourseOffering
	 * 
	 * @param courseOfferingId
	 * @return The Set of EnrollmentSets
	 * @throws IdNotFoundException If the id is not associated with any CourseOffering
	 */
	public Set getEnrollmentSets(String courseOfferingId) throws IdNotFoundException;

	/**
	 * Gets the Enrollments in an EnrollmentSet
	 * 
	 * @param enrollmentSetId
	 * @return The Set of Enrollments
	 * @throws IdNotFoundException If the id is not associated with any EnrollmentSet
	 */
	public Set getEnrollments(String enrollmentSetId) throws IdNotFoundException;

	/**
	 * Gets the set of user ids that are sanctioned by the enterprise to grade
	 * students enrolled in this EnrollmentSet.
	 * 
	 * @param enrollmentSetId
	 * @return The set of users who can grade students in this EnrollmentSet
	 * @throws IdNotFoundException If the id is not associated with any EnrollmentSet
	 */
	public Set getOfficialGraderIds(String enrollmentSetId) throws IdNotFoundException;
}
