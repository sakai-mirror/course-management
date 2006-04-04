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

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

public class CourseManagementServiceTest extends CourseManagementTestBase {
	private static final Log log = LogFactory.getLog(CourseManagementServiceTest.class);
	
	private CourseManagementService cm;
	private DataLoader loader;
	
	protected void onSetUpBeforeTransaction() throws Exception {
    }

	protected void onSetUpInTransaction() throws Exception {
    	cm = (CourseManagementService)applicationContext.getBean("org.sakaiproject.coursemanagement.api.CourseManagementService");
		loader = (DataLoader)applicationContext.getBean("hibernateTestDataLoader");
		loader.load();
	}
	
	public void testGetAcademicSessions() throws Exception {
		Assert.assertEquals(1, cm.getAcademicSessions().size());
	}
	
	public void testGetAcademicSessionById() throws Exception {
		AcademicSession term = cm.getAcademicSession("F2006");
		Assert.assertEquals("Fall 2006", term.getTitle());
		try {
			cm.getAcademicSession("bad eid");
			fail();
		} catch (IdNotFoundException ide) {}
	}
	
	public void testGetCourseSets() throws Exception {
		Assert.assertEquals(1, cm.getCourseSets().size());		
	}

	public void testGetChildCourseSets() throws Exception {
		CourseSet parent = (CourseSet)cm.getCourseSets().iterator().next();
		Assert.assertEquals(1, cm.getChildCourseSets(parent.getEid()).size());		
	}
	
	public void testGetCourseSetMembers() throws Exception {
		Set members = cm.getCourseSetMemberships("BIO_DEPT");
		Assert.assertEquals(1, members.size());
		try {
			cm.getCourseSetMemberships("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}
	
	public void testGetCanonicalCourse() throws Exception {
		Assert.assertEquals("Biology 101", cm.getCanonicalCourse("BIO101").getTitle());
		try {
			cm.getCanonicalCourse("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}

	public void testGetEquivalentCanonicalCourses() throws Exception {
		Set equivalents = cm.getEquivalentCanonicalCourses("BIO101");
		Assert.assertEquals(1, equivalents.size());
		Assert.assertTrue(!equivalents.contains(cm.getCanonicalCourse("BIO101")));
		try {
			cm.getEquivalentCanonicalCourses("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}

	public void testGetCanonicalCoursesFromCourseSet() throws Exception {
		Assert.assertEquals(1, cm.getCanonicalCourses("BIO_DEPT").size());
		Assert.assertEquals(2, cm.getCanonicalCourses("BIO_CHEM_GROUP").size());
		try {
			cm.getCanonicalCourses("bad eid");
			fail();
		} catch (IdNotFoundException ide) {}
	}

	public void testGetCourseOfferingsFromCourseSet() throws Exception {
		Assert.assertEquals(1, cm.getCourseOfferings("BIO_DEPT").size());
		Assert.assertEquals(2, cm.getCourseOfferings("BIO_CHEM_GROUP").size());
		try {
			cm.getCanonicalCourses("bad eid");
			fail();
		} catch (IdNotFoundException ide) {}
	}

	public void testGetCourseOffering() throws Exception {
		Assert.assertNotNull(cm.getCourseOffering("BIO101_F2006_01"));
		try {
			cm.getCourseOffering("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}

	public void testGetEquivalentCourseOfferings() throws Exception {
		Set equivalents = cm.getEquivalentCourseOfferings("BIO101_F2006_01");
		Assert.assertEquals(1, equivalents.size());
		try {
			cm.getEquivalentCourseOfferings("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}
	
	public void testGetSectionByEid() throws Exception {
		Assert.assertNotNull(cm.getSection("BIO101_F2006_01_SEC01"));
	}

	public void testGetSectionMembers() throws Exception {
		Assert.assertEquals(1, cm.getSectionMembers("BIO101_F2006_01_SEC01").size());
		try {
			cm.getSectionMembers("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}
	
	public void testGetSectionsFromCourseOffering() throws Exception {
		Assert.assertEquals(1, cm.getSections("BIO101_F2006_01").size());
		try {
			cm.getSections("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}
	
	public void testGetChildSections() throws Exception {
		Assert.assertEquals(1, cm.getChildSections("BIO101_F2006_01_SEC01").size());
		try {
			cm.getChildSections("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}
	
	public void testGetEnrollmentSet() throws Exception {
		Assert.assertNotNull(cm.getEnrollmentSet("BIO101_F2006_01_ES01"));
		try {
			cm.getEnrollmentSet("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}

	public void testGetEnrollmentSetFromCourseOffering() throws Exception {
		Assert.assertEquals(1, cm.getEnrollmentSets("BIO101_F2006_01").size());
		try {
			cm.getEnrollmentSets("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}

	public void testGetEnrollments() throws Exception {
		Assert.assertEquals(1, cm.getEnrollments("BIO101_F2006_01_ES01").size());
		try {
			cm.getEnrollmentSets("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}
	
	public void testIsEnrolled() throws Exception {
		Set enrollmentSetEids = new HashSet();
		enrollmentSetEids.add("BIO101_F2006_01_ES01");
		
		// We don't care about bad EnrollmentSet eids here... we're just interested in Enrollments
		enrollmentSetEids.add("bad eid");

		Assert.assertTrue(cm.isEnrolled("josh", enrollmentSetEids));
		
		// Graders are not enrolled
		Assert.assertTrue( ! cm.isEnrolled("grader1", enrollmentSetEids));
		Assert.assertTrue( ! cm.isEnrolled("grader2", enrollmentSetEids));
	}
	
	public void testGetOfficialGraders() throws Exception {
		Set graders = cm.getOfficialGraderIds("BIO101_F2006_01_ES01");
		Assert.assertTrue(graders.contains("grader1"));
		Assert.assertTrue(graders.contains("grader2"));
		Assert.assertTrue( ! graders.contains("josh"));
		
		try {
			cm.getOfficialGraderIds("bad eid");
			fail();
		} catch(IdNotFoundException ide) {}
	}
}
