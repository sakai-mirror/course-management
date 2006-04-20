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

import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * Loads data into persistence.  This is not a junit test per se.  It extends TestCase
 * so it's easy to execute via maven.
 * 
 * If you want to load data into a database, just modify this class, set your db connection
 * information in hibernate.properties, and run 'maven load-data'.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class CourseManagementAdministrationDataLoader extends TestCase implements DataLoader {
	private CourseManagementAdministration cmAdmin;
	private CourseManagementService cmService;
	public void testLoadData() throws Exception {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("testAppContext.xml");
		cmAdmin = (CourseManagementAdministration)ac.getBean(CourseManagementAdministration.class.getName());
		cmService = (CourseManagementService)ac.getBean(CourseManagementService.class.getName());
		load();
	}

	public void load() throws Exception {
		// Load your data here
		cmAdmin.createAcademicSession("fall 2006", "Fall 2006", "The fall term, 2006", new Date(), new Date());
		
	}

}
