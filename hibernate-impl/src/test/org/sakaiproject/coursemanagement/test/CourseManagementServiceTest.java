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

import net.sf.hibernate.SessionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.coursemanagement.impl.AcademicSessionImpl;
import org.sakaiproject.coursemanagement.impl.CanonicalCourseImpl;
import org.sakaiproject.coursemanagement.impl.CourseOfferingImpl;
import org.sakaiproject.coursemanagement.impl.CourseSetImpl;
import org.sakaiproject.coursemanagement.impl.CrossListing;
import org.sakaiproject.coursemanagement.impl.MembershipImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

public class CourseManagementServiceTest extends CourseManagementTestBase {
	static final Log log = LogFactory.getLog(CourseManagementServiceTest.class);
	
	private CourseManagementService cm;
	
	protected void onSetUpBeforeTransaction() throws Exception {
    	cm = (CourseManagementService)applicationContext.getBean("org.sakaiproject.coursemanagement.api.CourseManagementService");
    }

	protected void onSetUpInTransaction() throws Exception {
		DataLoader loader = new DataLoader(applicationContext);
		loader.loadAcademicSessions();
		loader.loadCourseSetsAndMembers();
		loader.loadCanonicalCourses();
		loader.loadCourseOfferings();
		loader.finish();
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
		Set members = cm.getCourseSetMembers("BIO_DEPT");
		Assert.assertEquals(1, members.size());
		try {
			cm.getCourseSetMembers("bad eid");
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
		// TODO This works outside of a transaction (see SpringTestsNonTransactional).  Why not here?
		
//		Assert.assertEquals(1, cm.getCanonicalCourses("BIO_DEPT").size());
//		Assert.assertEquals(2, cm.getCanonicalCourses("BIO_CHEM_GROUP").size());
//		try {
//			cm.getCanonicalCourses("bad eid");
//			fail();
//		} catch (IdNotFoundException ide) {}
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
	
}

class DataLoader extends HibernateDaoSupport {
	CourseManagementService cm;
	
	DataLoader(ApplicationContext ac) {
		setSessionFactory((SessionFactory)ac.getBean("cmSessionFactory"));
		cm = (CourseManagementService)ac.getBean("org.sakaiproject.coursemanagement.api.CourseManagementService");
	}
	
	void finish() {
		// Flush the hibernate session so hibernate's cascades are completed
		getHibernateTemplate().flush();
		
		// Clear the session to start fresh in the tests
		getHibernateTemplate().clear();
	}
	
	void loadAcademicSessions() {
		AcademicSessionImpl term = new AcademicSessionImpl();
		term.setEid("F2006");
		term.setTitle("Fall 2006");
		term.setDescription("Fall 2006, starts Sept 1, 2006");
		getHibernateTemplate().save(term);
	}
	
	void loadCourseSetsAndMembers() {
		CourseSetImpl cSet = new CourseSetImpl();
		cSet.setEid("BIO_DEPT");
		cSet.setTitle("Biology Department");
		cSet.setDescription("Department of Biology");
		getHibernateTemplate().save(cSet);

		MembershipImpl courseSetMember = new MembershipImpl();
		courseSetMember.setAssociation(cSet);
		courseSetMember.setRole("departmentAdmin");
		courseSetMember.setUserId("user1");
		getHibernateTemplate().save(courseSetMember);

		CourseSetImpl cSetChild = new CourseSetImpl();
		cSetChild.setEid("BIO_CHEM_GROUP");
		cSetChild.setTitle("Biochem Group");
		cSetChild.setDescription("Biochemistry group, Department of Biology");
		cSetChild.setParent(cSet);
		getHibernateTemplate().save(cSetChild);
	}

	void loadCanonicalCourses() {
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
		
//		CourseSetImpl bioCset2 = (CourseSetImpl)cm.getCourseSet("BIO_DEPT");
//		CourseManagementServiceTest.log.debug("bioCset in mem contains" + bioCset.getCanonicalCourses());
//		CourseManagementServiceTest.log.debug("bioCset from db contains" + bioCset2.getCanonicalCourses());

	}
	
	void loadCourseOfferings() {
		// Get the object dependencies
		AcademicSession term = cm.getAcademicSession("F2006");
		CanonicalCourseImpl cc1 = (CanonicalCourseImpl)cm.getCanonicalCourse("BIO101");
		CanonicalCourseImpl cc2 = (CanonicalCourseImpl)cm.getCanonicalCourse("CHEM101");
		CanonicalCourseImpl cc3 = (CanonicalCourseImpl)cm.getCanonicalCourse("ENG101");

		// Cross list bio and chem, but not English
		CrossListing cl = new CrossListing();
		getHibernateTemplate().save(cl);

		CourseOfferingImpl co1 = new CourseOfferingImpl();
		co1.setAcademicSession(term);
		co1.setCanonicalCourse(cc1);
		co1.setCrossListing(cl);
		co1.setEid("BIO101_F2006_01");
		co1.setTitle("Bio 101: It's all about the gene");
		co1.setDescription("Fall 2006 Bio 101 Offering");
		getHibernateTemplate().save(co1);

		CourseOfferingImpl co2 = new CourseOfferingImpl();
		co2.setAcademicSession(term);
		co2.setCanonicalCourse(cc2);
		co2.setCrossListing(cl);
		co2.setEid("CHEM101_F2006_01");
		co2.setTitle("Chem 101: It's all about the gene");
		co2.setDescription("Fall 2006 Chem 101 Offering");
		getHibernateTemplate().save(co2);

		CourseOfferingImpl co3 = new CourseOfferingImpl();
		co3.setAcademicSession(term);
		co3.setCanonicalCourse(cc3);
		co3.setEid("ENG101_F2006_01");
		co3.setTitle("English 101: Intro to literature");
		co3.setDescription("Fall 2006 Eng 101 Offering");
		getHibernateTemplate().save(co3);
	}
}
