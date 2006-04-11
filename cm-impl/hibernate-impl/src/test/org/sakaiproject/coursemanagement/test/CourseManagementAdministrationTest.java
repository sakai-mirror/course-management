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
		Assert.assertTrue( ! equivalents.contains(cm.getCanonicalCourse("cc3")));
	}
}