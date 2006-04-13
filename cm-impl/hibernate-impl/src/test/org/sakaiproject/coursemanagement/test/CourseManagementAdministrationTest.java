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
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
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
			cmAdmin.createAcademicSession("as1", "bad eid", "bad eid", null, null);
			fail();
		} catch (IdExistsException ide) {}
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
	
	public void testAddCourseOfferingToCourseSet() throws Exception {
		// Create a course offering
		cmAdmin.createCourseOffering("co1", "co1", "co1", null, null, null);
		
		// Create a course set
		cmAdmin.createCourseSet("cs1", "cs1", "cs1", null);
		
		// Add the course to the set
		cmAdmin.addCourseOfferingToCourseSet("cs1", "co1");
		
		// Ensure that the course is in the set
		Assert.assertTrue(cm.getCourseOfferings("cs1").contains(cm.getCourseOffering("co1")));
	}

	public void testAddEnrollment() throws Exception {
		// Create the EnrollmentSet
		cmAdmin.createEnrollmentSet("es1", "es1", "es1", "es1", "es1", null);
		
		// Add an enrollment
		cmAdmin.addOrUpdateEnrollment("josh", cm.getEnrollmentSet("es1"), "enrolled", "4", "pass/fail");
		
		// Ensure that the enrollment exists
		Assert.assertNotNull(cm.getEnrollment("josh", "es1"));
	}

	public void testUpdateEnrollment() throws Exception {
		// Create the EnrollmentSet
		cmAdmin.createEnrollmentSet("es1", "es1", "es1", "es1", "es1", null);
		
		// Add an enrollment
		cmAdmin.addOrUpdateEnrollment("josh", cm.getEnrollmentSet("es1"), "enrolled", "4", "pass/fail");
		
		// Update the enrollment
		cmAdmin.addOrUpdateEnrollment("josh", cm.getEnrollmentSet("es1"), "waitlisted", "3", "lettter gradel");
		
		// Ensure that the enrollment has been updated
		Assert.assertEquals("waitlisted", cm.getEnrollment("josh", "es1").getEnrollmentStatus());
	}

	public void testDropEnrollment() throws Exception {
		// Create the EnrollmentSet
		cmAdmin.createEnrollmentSet("es1", "es1", "es1", "es1", "es1", null);
		
		// Add an enrollment
		cmAdmin.addOrUpdateEnrollment("josh", cm.getEnrollmentSet("es1"), "enrolled", "4", "pass/fail");
		
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
}
