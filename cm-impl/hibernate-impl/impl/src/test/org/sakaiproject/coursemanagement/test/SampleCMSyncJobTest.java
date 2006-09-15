package org.sakaiproject.coursemanagement.test;

import java.util.List;

import junit.framework.Assert;

import org.jdom.Document;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.impl.job.SampleCMSyncJob;

public class SampleCMSyncJobTest extends CourseManagementTestBase {
	private SampleCMSyncJob job;
	private CourseManagementService cmService;
	private CourseManagementAdministration cmAdmin;
	
	protected void onSetUpInTransaction() throws Exception {
		job = (SampleCMSyncJob)applicationContext.getBean(SampleCMSyncJob.class.getName());
		cmService = (CourseManagementService)applicationContext.getBean(CourseManagementService.class.getName());
		cmAdmin = (CourseManagementAdministration)applicationContext.getBean(CourseManagementAdministration.class.getName());

		Document doc = job.loadDataFromXml();
		job.reconcileAcademicSessions(doc);
	}
	
	public void testLoadAcademicSessions() throws Exception {
		// Ensure that the academic sessions were loaded
		List asList = cmService.getAcademicSessions();
		Assert.assertEquals(2, asList.size());
	}
	
	public void testUpdateAcademicSessions() throws Exception {
		// Update an AS manually
		AcademicSession academicSession = cmService.getAcademicSession("fall_2006");
		
		String oldTitle = academicSession.getTitle();
		
		academicSession.setTitle("new title");
		cmAdmin.updateAcademicSession(academicSession);
		
		// Ensure that it was indeed updated
		Assert.assertEquals("new title", cmService.getAcademicSession("fall_2006").getTitle());
		
		// Reconcile again
		Document doc = job.loadDataFromXml();
		job.reconcileAcademicSessions(doc);
		
		// Ensure that the reconciliation updated the data
		Assert.assertEquals(oldTitle, cmService.getAcademicSession("fall_2006").getTitle());		
	}
}
