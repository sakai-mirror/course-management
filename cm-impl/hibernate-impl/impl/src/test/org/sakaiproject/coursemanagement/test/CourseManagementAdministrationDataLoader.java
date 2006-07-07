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

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * Loads data into persistence.  This is not a junit test per se.  It extends TestCase
 * so it's easy to execute via maven.
 * 
 * If you want to load data into a database, just modify this class, set your db connection
 * information in hibernate.dataload.properties, and run 'maven load-data'.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class CourseManagementAdministrationDataLoader extends TestCase implements DataLoader {
	private CourseManagementAdministration cmAdmin;
	public void testLoadData() throws Exception {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(new String[] {"spring-test.xml", "spring-config-dataload.xml"});
		cmAdmin = (CourseManagementAdministration)ac.getBean(CourseManagementAdministration.class.getName());
		load();
	}

	public void load() throws Exception {
		// Academic Sessions
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		
		startCal.set(2006, 8, 1);
		endCal.set(2006, 12, 1);
		cmAdmin.createAcademicSession("f2006", "Fall 2006", "The fall term, 2006", startCal.getTime(), endCal.getTime());
		
		startCal.set(2007, 3, 1);
		endCal.set(2007, 6, 1);
		cmAdmin.createAcademicSession("sp2007", "Spring 2007", "The spring term, 2007", startCal.getTime(), endCal.getTime());
		
		cmAdmin.createAcademicSession("IND", "Ongoing Courses", "Ongoing session for independent study", null, null);
		
		// Canonical Courses
		cmAdmin.createCanonicalCourse("bio101", "Intro to Biology", "An introduction to biology");
		cmAdmin.createCanonicalCourse("indep_study_bio", "Independent Study in Biology", "A self paced independent study in biology.  Must be " +
				"approved and sponsored by biology department faculty.");
		
		// Course Offerings
		startCal.set(2006, 8, 1);
		endCal.set(2006, 12, 1);
		cmAdmin.createCourseOffering("bio101_f2006", "Bio 101, Fall 2006", "Intro to Biology, Fall 06", "f2006", "bio101", startCal.getTime(), endCal.getTime());

		startCal.set(2007, 3, 1);
		endCal.set(2007, 6, 1);
		cmAdmin.createCourseOffering("bio101_sp2007", "Bio 101, Spring 2007", "Intro to Biology, Spring 07", "sp2007", "bio101", startCal.getTime(), endCal.getTime());
		
		cmAdmin.createCourseOffering("indep_study_bio_molecular_research", "Independent study in molecular research", "Details to be determined by student and sponsor",
				"IND", "indep_study_bio", null, null);
		
		// Enrollment sets
		Set instructors = new HashSet();
		instructors.add("admin");
		cmAdmin.createEnrollmentSet("bio101_f2006_lec1", "Bio 101 Lecture", "Bio 101 Lecture.  Required.", "lecture", "3", "bio101_f2006", instructors);
		
		instructors.clear();
		instructors.add("ta1");
		cmAdmin.createEnrollmentSet("bio101_f2006_lab1", "Lab 1", "Lab 1", "lab", "1", "bio101_f2006", instructors);

		instructors.clear();
		instructors.add("ta2");
		cmAdmin.createEnrollmentSet("bio101_f2006_lab2", "Lab 2", "Lab 2", "lab", "1", "bio101_f2006", instructors);

		// Enrollments
		cmAdmin.addOrUpdateEnrollment("student1", "bio101_f2006_lec1", "enrolled", "3", "standard");
		cmAdmin.addOrUpdateEnrollment("student2", "bio101_f2006_lec1", "enrolled", "3", "pass/fail");
		cmAdmin.addOrUpdateEnrollment("student3", "bio101_f2006_lec1", "waitlisted", "3", "standard");
		
		cmAdmin.addOrUpdateEnrollment("student1", "bio101_f2006_lab1", "enrolled", "1", "standard");
		cmAdmin.addOrUpdateEnrollment("student2", "bio101_f2006_lab1", "enrolled", "1", "pass/fail");

		cmAdmin.addOrUpdateEnrollment("student3", "bio101_f2006_lab2", "waitlisted", "1", "standard");

		// Sections
		cmAdmin.createSection("bio101_f2006_lec1", "Bio 101, Lecture", "Intro to Biology, Fall 06, Lecture", "lecture", null, "bio101_f2006", "bio101_f2006_lec1");
		cmAdmin.createSection("bio101_f2006_lab1", "Lab 1", "Intro to Biology, Fall 06, Lab 1", "lab", null, "bio101_f2006", "bio101_f2006_lab1");
		cmAdmin.createSection("bio101_f2006_lab2", "Lab 2", "Intro to Biology, Fall 06, Lab 2", "lab", null, "bio101_f2006", "bio101_f2006_lab2");
				
	}

}