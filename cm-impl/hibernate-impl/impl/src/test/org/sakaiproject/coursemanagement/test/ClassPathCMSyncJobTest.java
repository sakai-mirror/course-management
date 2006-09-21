/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.coursemanagement.test;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.coursemanagement.impl.job.ClassPathCMSyncJob;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClassPathCMSyncJobTest extends TestCase {
	private ApplicationContext applicationContext;
	private ClassPathCMSyncJob job;
	private CourseManagementService cmService;
	private CourseManagementAdministration cmAdmin;
	
	protected void setUp() throws Exception {
		if(applicationContext == null) {
			applicationContext = new ClassPathXmlApplicationContext( new String[] {"/spring-test.xml", "spring-config-test.xml"});
		}
		job = (ClassPathCMSyncJob)applicationContext.getBean(ClassPathCMSyncJob.class.getName());
		cmService = (CourseManagementService)applicationContext.getBean(CourseManagementService.class.getName());
		cmAdmin = (CourseManagementAdministration)applicationContext.getBean(CourseManagementAdministration.class.getName());
		job.syncAllCmObjects();
	}
	
	public void testAcademicSessionsLoaded() throws Exception {
		// Ensure that the academic sessions were loaded
		List asList = cmService.getAcademicSessions();
		Assert.assertEquals(2, asList.size());
	}
	
	public void testAcademicSessionsReconciled() throws Exception {
		// Update an AS manually
		AcademicSession academicSession = cmService.getAcademicSession("fall_2006");
		
		String oldTitle = academicSession.getTitle();
		
		academicSession.setTitle("new title");
		cmAdmin.updateAcademicSession(academicSession);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getAcademicSession("fall_2006").getTitle());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getAcademicSession("fall_2006").getTitle());		
	}
	
	public void testCanonicalCoursesLoaded() throws Exception {
		// Ensure that the canonical courses were loaded
		try {
			cmService.getCanonicalCourse("biology_101");
			cmService.getCanonicalCourse("chemistry_101");
		} catch (IdNotFoundException ide) {
			fail();
		}
	}
	
	public void testCanonicalCoursesReconciled() throws Exception {
		// Update a cc manually
		CanonicalCourse cc = cmService.getCanonicalCourse("biology_101");
		String oldTitle = cc.getTitle();
		cc.setTitle("new title");
		cmAdmin.updateCanonicalCourse(cc);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getCanonicalCourse("biology_101").getTitle());
		
		// Reconcile again
		job.syncAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getCanonicalCourse("biology_101").getTitle());
	}
}
