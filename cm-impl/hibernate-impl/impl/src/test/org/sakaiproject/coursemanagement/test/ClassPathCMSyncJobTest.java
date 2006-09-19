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

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.impl.job.ClassPathCMSyncJob;

public class ClassPathCMSyncJobTest extends CourseManagementTestBase {
	private ClassPathCMSyncJob job;
	private CourseManagementService cmService;
	private CourseManagementAdministration cmAdmin;
	
	protected void onSetUpInTransaction() throws Exception {
		job = (ClassPathCMSyncJob)applicationContext.getBean(ClassPathCMSyncJob.class.getName());
		cmService = (CourseManagementService)applicationContext.getBean(CourseManagementService.class.getName());
		cmAdmin = (CourseManagementAdministration)applicationContext.getBean(CourseManagementAdministration.class.getName());
		job.synchAllCmObjects();
	}
	
	public void testAcademicSessionsLoaded() throws Exception {
		// Ensure that the academic sessions were loaded
		List asList = cmService.getAcademicSessions();
		Assert.assertEquals(2, asList.size());
	}
	
	public void testAcademicSessionsSynched() throws Exception {
		// Update an AS manually
		AcademicSession academicSession = cmService.getAcademicSession("fall_2006");
		
		String oldTitle = academicSession.getTitle();
		
		academicSession.setTitle("new title");
		cmAdmin.updateAcademicSession(academicSession);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getAcademicSession("fall_2006").getTitle());
		
		// Reconcile again
		job.synchAllCmObjects();
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getAcademicSession("fall_2006").getTitle());		
	}
}
