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
package org.sakaiproject.coursemanagement.test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;

public class CourseManagementAdministrationTest extends CourseManagementTestBase {
	private static final Log log = LogFactory.getLog(CourseManagementAdministrationTest.class);
	
	private CourseManagementService cm;
	private CourseManagementAdministration cmAdmin;
	
	protected void onSetUpBeforeTransaction() throws Exception {
    }

	protected void onSetUpInTransaction() throws Exception {
    	cm = (CourseManagementService)applicationContext.getBean("org.sakaiproject.coursemanagement.api.CourseManagementService");
    	cmAdmin = (CourseManagementAdministration)applicationContext.getBean("org.sakaiproject.coursemanagement.api.CourseManagementAdministration");
	}
	
	public void testCreateAcademicSession() throws Exception {
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		Assert.assertTrue(cm.getAcademicSession("as1").getTitle().equals("academic session 1"));
		
		try {
			cmAdmin.createAcademicSession("as1", "foo", "foo", null, null);
			fail();
		} catch (IdExistsException ide) {}
	}

	public void testCreateCanonicalCourse() throws Exception {
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		Assert.assertTrue(cm.getCanonicalCourse("cc1").getTitle().equals("cc 1"));
		
		try {
			cmAdmin.createCanonicalCourse("cc1", "another canon course", "another canonical course");
			fail();
		} catch (IdExistsException ide) {}
	}

	public void testCreateCourseOffering() throws Exception {
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "co 1", "a course offering", "as1","cc1",  null, null);
		Assert.assertTrue(cm.getCourseOffering("co1").getTitle().equals("co 1"));
		
		try {
			cmAdmin.createCourseOffering("co1", "another course", "another course", "as1", "cc1", null, null);
			fail();
		} catch (IdExistsException ide) {}
	}

	public void testCreateCourseSet() throws Exception {
		cmAdmin.createCourseSet("cs1", "set 1", "a course set", null);
		Assert.assertTrue(cm.getCourseSet("cs1").getTitle().equals("set 1"));
		
		try {
			cmAdmin.createCourseSet("cs1", "another set 1", "another cset", null);
			fail();
		} catch (IdExistsException ide) {}
	}

	public void testAddCanonicalCourseToCourseSet() throws Exception {
		cmAdmin.createCourseSet("cs1", "course set", "course set", null);
		cmAdmin.createCanonicalCourse("cc1", "canon course 1", "canon course");
		cmAdmin.addCanonicalCourseToCourseSet("cs1", "cc1");
		CanonicalCourse cc = cm.getCanonicalCourse("cc1");
		Assert.assertTrue(cm.getCanonicalCourses("cs1").contains(cc));
	}
	
	public void testRemoveCanonicalCourseFromCourseSet() throws Exception {
		cmAdmin.createCourseSet("cs1", "course set", "course set", null);
		cmAdmin.createCanonicalCourse("cc1", "canon course 1", "canon course");
		cmAdmin.addCanonicalCourseToCourseSet("cs1", "cc1");
		cmAdmin.removeCanonicalCourseFromCourseSet("cs1", "cc1");
		CanonicalCourse cc = cm.getCanonicalCourse("cc1");
		Assert.assertFalse(cm.getCanonicalCourses("cs1").contains(cc));
	}

	public void testAddCourseOfferingToCourseSet() throws Exception {
		cmAdmin.createCourseSet("cs1", "course set", "course set", null);
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		cmAdmin.addCourseOfferingToCourseSet("cs1", "co1");
		CourseOffering co = cm.getCourseOffering("co1");
		Assert.assertTrue(cm.getCourseOfferings("cs1").contains(co));
	}
	
	public void testRemoveCourseOfferingFromCourseSet() throws Exception {
		cmAdmin.createCourseSet("cs1", "course set", "course set", null);
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		cmAdmin.addCourseOfferingToCourseSet("cs1", "co1");
		cmAdmin.removeCourseOfferingFromCourseSet("cs1", "co1");
		CourseOffering co = cm.getCourseOffering("co1");
		Assert.assertFalse(cm.getCourseOfferings("cs1").contains(co));
	}

	public void testSetEquivalentCanonicalCourses() throws Exception {
		// Create some courses
		cmAdmin.createCanonicalCourse("cc1", "cc1", "cc1");
		cmAdmin.createCanonicalCourse("cc2", "cc2", "cc2");
		cmAdmin.createCanonicalCourse("cc3", "cc3", "cc3");
		
		// Add them to a set
		Set courses = new HashSet();
		courses.add(cm.getCanonicalCourse("cc1"));
		courses.add(cm.getCanonicalCourse("cc2"));
		courses.add(cm.getCanonicalCourse("cc3"));

		// Crosslist them
		cmAdmin.setEquivalentCanonicalCourses(courses);
		
		// Ensure that CM sees them as crosslisted
		Set equivalents = cm.getEquivalentCanonicalCourses("cc1");
		Assert.assertTrue(equivalents.contains(cm.getCanonicalCourse("cc2")));
		Assert.assertTrue(equivalents.contains(cm.getCanonicalCourse("cc3")));
		
		// Ensure that we can remove one of the equivalents
		courses.remove(cm.getCanonicalCourse("cc3"));
		cmAdmin.setEquivalentCanonicalCourses(courses);
		equivalents = cm.getEquivalentCanonicalCourses("cc1");
		Assert.assertTrue(equivalents.contains(cm.getCanonicalCourse("cc2")));
		Assert.assertFalse(equivalents.contains(cm.getCanonicalCourse("cc3")));
	}
	
	public void testRemoveEquivalencyCanonCourse() throws Exception {
		// Create some courses
		cmAdmin.createCanonicalCourse("cc1", "cc1", "cc1");
		cmAdmin.createCanonicalCourse("cc2", "cc2", "cc2");
		cmAdmin.createCanonicalCourse("cc3", "cc3", "cc3");
		
		// Add them to a set
		Set courses = new HashSet();
		courses.add(cm.getCanonicalCourse("cc1"));
		courses.add(cm.getCanonicalCourse("cc2"));

		// Crosslist them
		cmAdmin.setEquivalentCanonicalCourses(courses);
		
		// Remove a course that was crosslisted
		Assert.assertTrue(cmAdmin.removeEquivalency(cm.getCanonicalCourse("cc1")));
		
		// Remove one that wasn't crosslisted
		Assert.assertFalse(cmAdmin.removeEquivalency(cm.getCanonicalCourse("cc3")));
	}
	
	public void testCreateEnrollmentSet() throws Exception {
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		cmAdmin.createEnrollmentSet("es1", "enr set 1", "an enr set", "lecture", "3", "co1", null);
		Assert.assertTrue(cm.getEnrollmentSet("es1").getTitle().equals("enr set 1"));
		
		try {
			cmAdmin.createEnrollmentSet("es1", "enr set 1", "an enr set", "lecture", "3", "co1", null);
			fail();
		} catch (IdExistsException ide) {}
	}

	public void testAddEnrollment() throws Exception {
		// Create the EnrollmentSet
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		cmAdmin.createEnrollmentSet("es1", "enr set 1", "an enr set", "lecture", "3", "co1", null);
		
		// Add an enrollment
		cmAdmin.addOrUpdateEnrollment("josh", "es1", "enrolled", "4", "pass/fail");
		
		// Ensure that the enrollment exists
		Assert.assertNotNull(cm.getEnrollment("josh", "es1"));
	}

	public void testUpdateEnrollment() throws Exception {
		// Create the EnrollmentSet
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		cmAdmin.createEnrollmentSet("es1", "enr set 1", "an enr set", "lecture", "3", "co1", null);
		
		// Add an enrollment
		cmAdmin.addOrUpdateEnrollment("josh", "es1", "enrolled", "4", "pass/fail");
		
		// Update the enrollment
		cmAdmin.addOrUpdateEnrollment("josh", "es1", "waitlisted", "3", "lettter gradel");
		
		// Ensure that the enrollment has been updated
		Assert.assertEquals("waitlisted", cm.getEnrollment("josh", "es1").getEnrollmentStatus());
	}

	public void testDropEnrollment() throws Exception {
		// Create the EnrollmentSet
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		cmAdmin.createEnrollmentSet("es1", "enr set 1", "an enr set", "lecture", "3", "co1", null);
		
		// Add an enrollment
		cmAdmin.addOrUpdateEnrollment("josh", "es1", "enrolled", "4", "pass/fail");
		
		// Drop the enrollment
		cmAdmin.removeEnrollment("josh", "es1");
		
		// Ensure that the enrollment has been dropped
		Assert.assertEquals(0, cm.getEnrollments("es1").size());
	}
	
	public void testAddCourseSetMembership() throws Exception {
		// Create a course set
		cmAdmin.createCourseSet("cs1", "cs1", "cs1", null);
		
		// Create a membership in the courseSet
		cmAdmin.addOrUpdateCourseSetMembership("josh", "student", "cs1");
		
		// Ensure that the membership was added
		Assert.assertEquals(1, cm.getCourseSetMemberships("cs1").size());

		// Add the same username, this time with a different role
		cmAdmin.addOrUpdateCourseSetMembership("josh", "ta", "cs1");
		
		// Ensure that the membership was updated, not added
		Assert.assertEquals(1, cm.getCourseSetMemberships("cs1").size());
		Assert.assertEquals("ta", ((Membership)cm.getCourseSetMemberships("cs1").iterator().next()).getRole());
	}

	public void testRemoveCourseSetMembers() throws Exception {
		// Create a course set
		cmAdmin.createCourseSet("cs1", "cs1", "cs1", null);
		
		// Create a membership in the courseSet
		cmAdmin.addOrUpdateCourseSetMembership("josh", "student", "cs1");

		// Remove the membership (should return true)
		Assert.assertTrue(cmAdmin.removeCourseSetMembership("josh", "cs1"));
		
		// Try to remove it again (should return false)
		Assert.assertFalse(cmAdmin.removeCourseSetMembership("josh", "cs1"));
	}
	
	public void testAddCourseOfferingMembership() throws Exception {
		// Create a course offering
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		
		// Create a membership in the courseOffering
		cmAdmin.addOrUpdateCourseOfferingMembership("josh", "student", "co1");
		
		// Ensure that the membership was added
		Assert.assertEquals(1, cm.getCourseOfferingMemberships("co1").size());

		// Add the same username, this time with a different role
		cmAdmin.addOrUpdateCourseOfferingMembership("josh", "ta", "co1");
		
		// Ensure that the membership was updated, not added
		Assert.assertEquals(1, cm.getCourseOfferingMemberships("co1").size());
		Assert.assertEquals("ta", ((Membership)cm.getCourseOfferingMemberships("co1").iterator().next()).getRole());
	}

	public void testRemoveCourseOfferingMembers() throws Exception {
		// Create a course offering
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		
		// Create a membership in the courseOffering
		cmAdmin.addOrUpdateCourseOfferingMembership("josh", "student", "co1");

		// Remove the membership (should return true)
		Assert.assertTrue(cmAdmin.removeCourseOfferingMembership("josh", "co1"));
		
		// Try to remove it again (should return false)
		Assert.assertFalse(cmAdmin.removeCourseOfferingMembership("josh", "co1"));
	}

	public void testCreateSection() throws Exception {
		// Create a course offering
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);

		cmAdmin.createSection("sec1", "sec 1", "a sec", "lecture", null, "co1", null);
		Assert.assertTrue(cm.getSection("sec1").getTitle().equals("sec 1"));
		
		try {
			cmAdmin.createSection("sec1", "sec 1", "a sec", "lecture", null, null, null);
			fail();
		} catch (IdExistsException ide) {}
	}

	public void testAddSectionMembership() throws Exception {
		// Create a course offering
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		
		// Add a section
		cmAdmin.createSection("sec1", "sec1", "sec1", "sec1", null, "co1", null);
		
		// Create a membership in the section
		cmAdmin.addOrUpdateSectionMembership("josh", "student", "sec1");
		
		// Ensure that the membership was added
		Assert.assertEquals(1, cm.getSectionMemberships("sec1").size());

		// Add the same username, this time with a different role
		cmAdmin.addOrUpdateSectionMembership("josh", "ta", "sec1");
		
		// Ensure that the membership was updated, not added
		Assert.assertEquals(1, cm.getSectionMemberships("sec1").size());
		Assert.assertEquals("ta", ((Membership)cm.getSectionMemberships("sec1").iterator().next()).getRole());
	}

	public void testRemoveSectionMembers() throws Exception {
		// Create a course offering
		cmAdmin.createAcademicSession("as1", "academic session 1", "an academic session", new Date(), new Date());
		cmAdmin.createCanonicalCourse("cc1", "cc 1", "a canon course");
		cmAdmin.createCourseOffering("co1", "course 1", "course", "as1", "cc1", null, null);
		
		// Add a section
		cmAdmin.createSection("sec1", "sec1", "sec1", "sec1", null, "co1", null);
		
		// Create a membership in the section
		cmAdmin.addOrUpdateSectionMembership("josh", "student", "sec1");

		// Remove the membership (should return true)
		Assert.assertTrue(cmAdmin.removeSectionMembership("josh", "sec1"));
		
		// Try to remove it again (should return false)
		Assert.assertFalse(cmAdmin.removeSectionMembership("josh", "sec1"));
	}

}
