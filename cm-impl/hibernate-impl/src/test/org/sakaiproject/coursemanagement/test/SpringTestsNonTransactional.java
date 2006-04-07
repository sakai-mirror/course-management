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

import junit.framework.TestCase;

import net.sf.hibernate.SessionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.impl.CanonicalCourseImpl;
import org.sakaiproject.coursemanagement.impl.CourseSetImpl;
import org.sakaiproject.coursemanagement.impl.CrossListing;
import org.sakaiproject.coursemanagement.impl.MembershipImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

/**
 * Sometimes, the fancy spring transactional tests get in the way.  This is non-
 * transactional.
 * 
 * This is not run during the normal build tests.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class SpringTestsNonTransactional extends TestCase {
	
	public void testInSpring() throws Exception {
		ApplicationContext ac = new ClassPathXmlApplicationContext("/testAppContext.xml");
		CourseManagementService cm = (CourseManagementService)ac.getBean("org.sakaiproject.coursemanagement.api.CourseManagementService");
		
		Loader loader = new Loader(ac);
		loader.load();
	}
}

class Loader extends HibernateDaoSupport {
	private static final Log log = LogFactory.getLog(Loader.class);

	CourseManagementService cm;
	
	Loader(ApplicationContext ac) {
		setSessionFactory((SessionFactory)ac.getBean("cmSessionFactory"));
		cm = (CourseManagementService)ac.getBean("org.sakaiproject.coursemanagement.api.CourseManagementService");
	}

	void load() {
		///// Course Sets
		
		CourseSetImpl cSet = new CourseSetImpl();
		cSet.setEid("BIO_DEPT");
		cSet.setTitle("Biology Department");
		cSet.setDescription("Department of Biology");

		MembershipImpl courseSetMember = new MembershipImpl();
		courseSetMember.setRole("departmentAdmin");
		courseSetMember.setUserId("user1");
		
		Set members = new HashSet();
		members.add(courseSetMember);
		cSet.setMembers(members);

		getHibernateTemplate().save(cSet);

		CourseSetImpl cSetChild = new CourseSetImpl();
		cSetChild.setEid("BIO_CHEM_GROUP");
		cSetChild.setTitle("Biochem Group");
		cSetChild.setDescription("Biochemistry group, Department of Biology");
		cSetChild.setParent(cSet);
		getHibernateTemplate().save(cSetChild);

		/////// Canonical Courses
		
		// Cross-list bio and chem (but not English)
		CrossListing cl = new CrossListing();
		getHibernateTemplate().save(cl);
		
		// Build and save the CanonicalCourses
		CanonicalCourseImpl cc1 = new CanonicalCourseImpl();
		cc1.setEid("BIO101");
		cc1.setTitle("Biology 101");
		cc1.setDescription("An intro to biology");
		cc1.setCrossListing(cl);
		getHibernateTemplate().save(cc1);
		
		CanonicalCourseImpl cc2 = new CanonicalCourseImpl();
		cc2.setEid("CHEM101");
		cc2.setTitle("Chem 101");
		cc2.setDescription("An intro to chemistry");
		cc2.setCrossListing(cl);
		getHibernateTemplate().save(cc2);
		
		CanonicalCourseImpl cc3 = new CanonicalCourseImpl();
		cc3.setEid("ENG101");
		cc3.setTitle("English 101");
		cc3.setDescription("An intro to English");
		getHibernateTemplate().save(cc3);

		// Add these canonical courses to course sets
		CourseSetImpl bioCset = (CourseSetImpl)cm.getCourseSet("BIO_DEPT");
		CourseSetImpl bioChemCset = (CourseSetImpl)cm.getCourseSet("BIO_CHEM_GROUP");
		
		Set bioCourses = new HashSet();
		bioCourses.add(cc1);
		bioCset.setCanonicalCourses(bioCourses);
		getHibernateTemplate().update(bioCset);
		
		Set bioChemCourses = new HashSet();
		bioChemCourses.add(cc1);
		bioChemCourses.add(cc2);
		bioChemCset.setCanonicalCourses(bioChemCourses);
		getHibernateTemplate().update(bioChemCset);
		
		//////// WTF is going on ???
		getHibernateTemplate().flush();
		getHibernateTemplate().clear();
		
		CourseSetImpl bioCsetTest1 = (CourseSetImpl)cm.getCourseSet("BIO_DEPT");
		log.debug("bioCset in mem contains" + bioCset.getCanonicalCourses());
		log.debug("bioCset from db contains" + bioCsetTest1.getCanonicalCourses());

		CourseSetImpl bioCsetTest2 = (CourseSetImpl)cm.getCourseSet("BIO_CHEM_GROUP");
		log.debug("bioCset in mem contains" + bioChemCset.getCanonicalCourses());
		log.debug("bioCset from db contains" + bioCsetTest2.getCanonicalCourses());
	}
}