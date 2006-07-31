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
 *      http://www.opensource.org/licenses/ecl1.txt
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
package org.sakaiproject.coursemanagement.api;

import java.util.Date;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * A service that provides for the administration of enterprise-defined course data.
 * This service is typically not used inside Sakai, and should not be exposed until
 * appropriate permission and reconciliation issues are solved.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface CourseManagementAdministration {

	/**
	 * Creates a new AcademicSession.
	 * 
	 * @param eid
	 * @param title
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @throws IdExistsException
	 */
	public void createAcademicSession(String eid, String title, String description,
			Date startDate, Date endDate) throws IdExistsException;

	/**
	 * Updates an existing AcademicSession.
	 * 
	 * @param academicSession The AcademicSession to be updated
	 */
	public void updateAcademicSession(AcademicSession academicSession);

	/**
	 * Creates a new CourseSet.
	 * 
	 * @param eid
	 * @param title
	 * @param description
	 * @param category
	 * @param parentCourseSetEid The parent CourseSet's eid, or null if none.
	 * @throws IdExistsException
	 */
	public void createCourseSet(String eid, String title, String description, String category, String parentCourseSetEid)
		throws IdExistsException;
	
	/**
	 * Updates an existing CourseSet.
	 * 
	 * @param courseSet
	 */
	public void updateCourseSet(CourseSet courseSet);

	/**
	 * Creates a new CanonicalCourse.
	 * 
	 * @param eid
	 * @param title
	 * @param description
	 * @throws IdExistsException
	 */
	public void createCanonicalCourse(String eid, String title, String description)
		throws IdExistsException;
	
	/**
	 * Updates an existing CanonicalCourse.
	 * 
	 * @param canonicalCourse
	 */
	public void updateCanonicalCourse(CanonicalCourse canonicalCourse);
	
	/**
	 * Adds a CanonicalCourse to a CourseSet.
	 * 
	 * @param courseSetEid
	 * @param canonicalCourseEid
	 * @throws IdNotFoundException
	 */
	public void addCanonicalCourseToCourseSet(String courseSetEid, String canonicalCourseEid)
		throws IdNotFoundException;

	/**
	 * Removes a CanonicalCourse from a CourseSet.
	 * 
	 * @param courseSetEid
	 * @param canonicalCourseEid
	 * @return Whether the CanonicalCourse was a member of the CourseSet and
	 * was successfully removed.
	 */
	public boolean removeCanonicalCourseFromCourseSet(String courseSetEid, String canonicalCourseEid);
	
	/**
	 * Creates an equivalency (cross listing) between CanonicalCourses
	 * 
	 * @param canonicalCourses
	 */
	public void setEquivalentCanonicalCourses(Set canonicalCourses);
	
	/**
	 * Removes a CanonicalCourse from its set of equivalent CanonicalCourses, if it is
	 * a member of such a set.
	 * 
	 * @param canonicalCourse
	 * @return Whether the equivalency existed and was removed.
	 */
	public boolean removeEquivalency(CanonicalCourse canonicalCourse);

	/**
	 * Creates a new CourseOffering.
	 * 
	 * @param eid
	 * @param title
	 * @param description
	 * @param academicSessionEid
	 * @param canonicalCourseEid
	 * @param startDate
	 * @param endDate
	 * @throws IdExistsException
	 */
	public void createCourseOffering(String eid, String title, String description,
			String academicSessionEid, String canonicalCourseEid, Date startDate, Date endDate)
			throws IdExistsException;

	/**
	 * Updates an existing CourseOffering.
	 * 
	 * @param courseOffering
	 */
	public void updateCourseOffering(CourseOffering courseOffering);
	
	/**
	 * Creates an equivalency (cross listing) betweencourseOfferings
	 * 
	 * @param courseOfferings
	 */
	public void setEquivalentCourseOfferings(Set courseOfferings);
	
	/**
	 * Removes a CourseOffering from its set of equivalent CourseOfferings, if it is
	 * a member of such a set.
	 * 
	 * @param courseOffering
	 * @return Whether the equivalency existed and was removed.
	 */
	public boolean removeEquivalency(CourseOffering courseOffering);
	
	/**
	 * Adds a CourseOffering to a CourseSet.
	 * 
	 * @param courseSetEid
	 * @param courseOfferingEid
	 */
	public void addCourseOfferingToCourseSet(String courseSetEid, String courseOfferingEid);

	/**
	 * Removes a CourseOffering from a CourseSet.
	 * 
	 * @param courseSetEid
	 * @param courseOfferingEid
	 * @return Whether the CourseOffering was in the CourseSet and was removed.
	 */
	public boolean removeCourseOfferingFromCourseSet(String courseSetEid, String courseOfferingEid);

	/**
	 * Creates a new EnrollmentSet.
	 * 
	 * @param eid
	 * @param title
	 * @param description
	 * @param category
	 * @param defaultEnrollmentCredits
	 * @param courseOfferingEid
	 * @param officialGraders
	 * @throws IdExistsException
	 */
	public void createEnrollmentSet(String eid, String title, String description,
			String category, String defaultEnrollmentCredits, String courseOfferingEid, Set officialGraders)
			throws IdExistsException;
	
	/**
	 * Updates an existing EnrollmentSet.
	 * 
	 * @param enrollmentSet
	 */
	public void updateEnrollmentSet(EnrollmentSet enrollmentSet);
	
	/**
	 * Adds an Enrollment to an EnrollmentSet.  If the user is already enrolled in the
	 * EnrollmentSet, the Enrollment record is updated for the user.
	 * 
	 * @param userId
	 * @param enrollmentSetEid
	 * @param enrollmentStatus
	 * @param credits
	 * @param gradingScheme
	 */
	public void addOrUpdateEnrollment(String userId, String enrollmentSetEid,
			String enrollmentStatus, String credits, String gradingScheme);

	/**
	 * Removes an Enrollment from an EnrollmentSet.
	 * 
	 * @param userId
	 * @param enrollmentSetEid
	 * @return Whether the enrollment existed and was removed.
	 */
	public boolean removeEnrollment(String userId, String enrollmentSetEid);
	
	/**
	 * Creates a new Section.
	 * 
	 * @param eid
	 * @param title
	 * @param description
	 * @param category
	 * @param parentSectionEid
	 * @param courseOfferingEid
	 * @param enrollmentSetEid
	 * @throws IdExistsException
	 */
	public void createSection(String eid, String title, String description,
			String category, String parentSectionEid, String courseOfferingEid,
			String enrollmentSetEid) throws IdExistsException;
	
	/**
	 * Updates an existing Section.
	 * 
	 * @param section
	 */
	public void updateSection(Section section);
	
	/**
	 * Adds a user to a CourseSet.  If the user is already a member of the CourseSet,
	 * update the user's role.
	 * 
	 * @param userId
	 * @param role
	 * @param courseSetEid
	 * @throws IdNotFoundException If the CourseSet can not be found
	 */
	public void addOrUpdateCourseSetMembership(String userId, String role, String courseSetEid) throws IdNotFoundException;
	
	/**
	 * Removes a user from a CourseSet.
	 * 
	 * @param userId
	 * @param courseSetEid
	 * @return Whether the user was a member of the CourseSet and was removed.
	 */
	public boolean removeCourseSetMembership(String userId, String courseSetEid);

	/**
	 * Adds a user to a CourseOffering.  If the user is already a member of the CourseOffering,
	 * update the user's role.
	 * 
	 * @param userId
	 * @param role
	 * @param courseOfferingEid
	 */
	public void addOrUpdateCourseOfferingMembership(String userId, String role, String courseOfferingEid);
	
	/**
	 * Removes a user from a CourseOffering.
	 * 
	 * @param userId
	 * @param courseOfferingEid
	 * @return Whether the user was a member of the CourseOffering and was
	 * removed.
	 */
	public boolean removeCourseOfferingMembership(String userId, String courseOfferingEid);
	
	/**
	 * Adds a user to a Section.  If the user is already a member of the Section,
	 * update the user's role.
	 * 
	 * @param userId
	 * @param role
	 * @param sectionEid
	 */
	public void addOrUpdateSectionMembership(String userId, String role, String sectionEid);
	
	/**
	 * Removes a user from a Section.
	 * 
	 * @param userId
	 * @param sectionEid
	 * @return Whether the user was a member of the Section and was removed.
	 */
	public boolean removeSectionMembership(String userId, String sectionEid);
}
