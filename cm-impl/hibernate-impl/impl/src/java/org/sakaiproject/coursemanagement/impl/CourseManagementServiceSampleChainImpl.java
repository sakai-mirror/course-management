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
package org.sakaiproject.coursemanagement.impl;

import java.util.List;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * A template to use when implementing CourseManagementService to provide
 * course and enrollment data in a federated CM configuration.  Extending this class
 * should be useful for institutions intending to federate external datasources
 * (via webservices, SIS APIs, etc) with Sakai's hibernate-based
 * CourseManagementService.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class CourseManagementServiceSampleChainImpl implements CourseManagementService {

	public Set findCourseOfferings(String courseSetEid, String academicSessionEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public List findCourseSets(String category) {
		return null;
	}

	public Set findCurrentSectionsWithMember(String userId) {
		return null;
	}

	public Set findCurrentlyEnrolledEnrollmentSets(String userId) {
		return null;
	}

	public Set findCurrentlyInstructingEnrollmentSets(String userId) {
		return null;
	}

	public Enrollment findEnrollment(String userId, String eid) {
		return null;
	}

	public Set findInstructingSections(String userId) {
		return null;
	}

	public Set findInstructingSections(String userId, String academicSessionEid) throws IdNotFoundException {
		throw new IdNotFoundException(academicSessionEid, AcademicSession.class.getName());
	}

	public AcademicSession getAcademicSession(String academicSessionEid) throws IdNotFoundException {
		throw new IdNotFoundException(academicSessionEid, AcademicSession.class.getName());
	}

	public List getAcademicSessions() {
		return null;
	}

	public CanonicalCourse getCanonicalCourse(String canonicalCourseEid) throws IdNotFoundException {
		throw new IdNotFoundException(canonicalCourseEid, CanonicalCourse.class.getName());
	}

	public Set getCanonicalCourses(String courseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public Set getChildCourseSets(String parentCourseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(parentCourseSetEid, CourseSet.class.getName());
	}

	public Set getChildSections(String parentSectionEid) throws IdNotFoundException {
		throw new IdNotFoundException(parentSectionEid, Section.class.getName());
	}

	public CourseOffering getCourseOffering(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public Set getCourseOfferingMemberships(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public Set getCourseOfferings(String courseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public CourseSet getCourseSet(String courseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public Set getCourseSetMemberships(String courseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public Set getCourseSets() {
		return null;
	}

	public List getCurrentAcademicSessions() {
		return null;
	}

	public EnrollmentSet getEnrollmentSet(String enrollmentSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
	}

	public Set getEnrollmentSets(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public Set getEnrollments(String enrollmentSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
	}

	public Set getEquivalentCanonicalCourses(String canonicalCourseEid) throws IdNotFoundException {
		throw new IdNotFoundException(canonicalCourseEid, CanonicalCourse.class.getName());
	}

	public Set getEquivalentCourseOfferings(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public Set getInstructorsOfRecordIds(String enrollmentSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
	}

	public Section getSection(String sectionEid) throws IdNotFoundException {
		throw new IdNotFoundException(sectionEid, Section.class.getName());
	}

	public Set getSectionMemberships(String sectionEid) throws IdNotFoundException {
		throw new IdNotFoundException(sectionEid, Section.class.getName());
	}

	public String getSectionRole(String sectionEid, String userId) {
		return null;
	}

	public Set getSections(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public boolean isEmpty(String courseSetEid) {
		throw new UnsupportedOperationException();
	}

	public boolean isEnrolled(String userId, Set enrollmentSetEids) {
		throw new UnsupportedOperationException();
	}

	public boolean isEnrolled(String userId, String eid) {
		throw new UnsupportedOperationException();
	}

	public Set findEnrolledSections(String userId) {
		return null;
	}
}
