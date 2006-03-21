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

import junit.framework.Assert;

import net.sf.hibernate.SessionFactory;

import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.impl.AcademicSessionImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

public class CourseManagementServiceTest extends CourseManagementTestBase {
	private CourseManagementService cm;
	
	protected void onSetUpBeforeTransaction() throws Exception {
    	cm = (CourseManagementService)applicationContext.getBean("org.sakaiproject.coursemanagement.api.CourseManagementService");
    }

	protected void onSetUpInTransaction() throws Exception {
		DataLoader loader = new DataLoader(applicationContext);
		loader.loadAcademicSessions();
	}
	
	public void testGetAcademicSessions() throws Exception {
		Assert.assertEquals(cm.getAcademicSessions().size(), 1);
	}
}

class DataLoader extends HibernateDaoSupport {
	DataLoader(ApplicationContext ac) {
		setSessionFactory((SessionFactory)ac.getBean("cmSessionFactory"));
	}
	
	void loadAcademicSessions() {
		AcademicSessionImpl term = new AcademicSessionImpl();
		term.setId("F2006");
		term.setTitle("Fall 2006");
		term.setDescription("Fall 2006, starts Sept 1, 2006");
		getHibernateTemplate().save(term);
	}
}
