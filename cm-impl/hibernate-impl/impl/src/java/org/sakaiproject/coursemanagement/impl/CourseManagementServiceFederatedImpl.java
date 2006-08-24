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
package org.sakaiproject.coursemanagement.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * <p>
 * Federates multiple CourseManagementService implementations.  Each individual implementation
 * must follow the following pattern to participate in federation:
 * </p>
 * 
 * <p>
 * If the implementation doesn't have any information about a particular method and would
 * like to defer to other impls in the chain, it should:
 * 
 * <ul>
 * 	<li>Throw an IdNotFoundException if the return type is an object and the method throws this exception</li>
 * 	<li>Return null if the return type is an object and the method does not throw IdNotFoundException</li>
 * 	<li>Throw UnsupportedOperationException if the return type is a primitive</li>
 * </ul>
 * 
 * Please ensure that your implementation is internally consistent.  If you implement
 * getEnrollments(String enrollmentSetEid), for instance, you should also implement
 * the isEnrolled() methods.  If you implement one but not the other, the data
 * provided by the CourseManagementService will be dependent on <i>how</i>
 * the client calls the API, rather than getting a consistent picture no matter which
 * methods are called.
 * </p>
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class CourseManagementServiceFederatedImpl implements
		CourseManagementService {

	private static final Log log = LogFactory.getLog(CourseManagementServiceFederatedImpl.class);

	private List implList;
	
	/**
	 * Sets the list of implementations to consult.  Implementations earlier in the list will override later ones.
	 * @param implList
	 */
	public void setImplList(List implList) {
		this.implList = implList;
	}
	
	public Set findCourseOfferings(String courseSetEid,
			String academicSessionEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.findCourseOfferings(courseSetEid, academicSessionEid);
			} catch (IdNotFoundException ide) {
				if(log.isDebugEnabled()) log.debug(cm + " could not find course set " + courseSetEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		return resultSet;
	}

	public List findCourseSets(String category) {
		List resultSet = new ArrayList();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			List list = cm.findCourseSets(category);
			if(list != null) {
				resultSet.addAll(list);
			}
		}
		
		// The federated list should be sorted by title.
		Collections.sort(resultSet, new Comparator() {
			public int compare(Object o1, Object o2) {
				CourseSet cs1 = (CourseSet)o1;
				CourseSet cs2 = (CourseSet)o2;
				return cs1.getTitle().compareTo(cs2.getTitle());
			}
		});
		return resultSet;
	}

//	public Set findSectionsWithMember(String userId) {
//		Set resultSet = new HashSet();
//		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
//			CourseManagementService cm = (CourseManagementService)implIter.next();
//			Set set = cm.findSectionsWithMember(userId);
//			if(set != null) {
//				resultSet.addAll(set);
//			}
//		}
//		return resultSet;
//	}

	public Set findCurrentlyEnrolledEnrollmentSets(String userId) {
		Set resultSet = new HashSet();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = cm.findCurrentlyEnrolledEnrollmentSets(userId);
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		return resultSet;
	}

	public Set findCurrentlyInstructingEnrollmentSets(String userId) {
		Set resultSet = new HashSet();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = cm.findCurrentlyInstructingEnrollmentSets(userId);
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		return resultSet;
	}

	public Enrollment findEnrollment(String userId, String enrollmentSetEid) {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Enrollment enr = cm.findEnrollment(userId, enrollmentSetEid);
			if(enr != null) {
				return enr;
			}
		}
		return null;
	}

	public Set findInstructingSections(String userId) {
		Set resultSet = new HashSet();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = cm.findInstructingSections(userId);
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		return resultSet;
	}

	public Set findInstructingSections(String userId, String academicSessionEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.findInstructingSections(userId, academicSessionEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not find academic session " + academicSessionEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the academic session, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(academicSessionEid, AcademicSession.class.getName());
		}
		return resultSet;
	}

	public AcademicSession getAcademicSession(String eid) throws IdNotFoundException {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				return cm.getAcademicSession(eid);
			} catch (IdNotFoundException ide) {
				if(log.isDebugEnabled()) log.debug(cm + " could not locate academic session " + eid);
			}
		}
		throw new IdNotFoundException(eid, AcademicSession.class.getName());
	}

	public List getAcademicSessions() {
		List resultSet = new ArrayList();
		
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			List list = cm.getAcademicSessions();
			if(list != null) {
				resultSet.addAll(list);
			}
		}
		// The federated list should be sorted by start date.
		Collections.sort(resultSet, startDateComparator);
		return resultSet;
	}

	public CanonicalCourse getCanonicalCourse(String canonicalCourseEid) throws IdNotFoundException {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				return cm.getCanonicalCourse(canonicalCourseEid);
			} catch (IdNotFoundException ide) {
				if(log.isDebugEnabled()) log.debug(cm + " could not locate canonical course " + canonicalCourseEid);
			}
		}
		throw new IdNotFoundException(canonicalCourseEid, CanonicalCourse.class.getName());
	}

	public Set getCanonicalCourses(String courseSetEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getCanonicalCourses(courseSetEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not find course set " + courseSetEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the course set, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
		}
		return resultSet;
	}

	public Set getChildCourseSets(String parentCourseSetEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getChildCourseSets(parentCourseSetEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate parent course set " + parentCourseSetEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the course set, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(parentCourseSetEid, CourseSet.class.getName());
		}
		return resultSet;
	}

	public Set getChildSections(String parentSectionEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getChildSections(parentSectionEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate parent section " + parentSectionEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the section, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(parentSectionEid, Section.class.getName());
		}
		return resultSet;
	}

	public CourseOffering getCourseOffering(String courseOfferingEid) throws IdNotFoundException {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				return cm.getCourseOffering(courseOfferingEid);
			} catch (IdNotFoundException ide) {
				if(log.isDebugEnabled()) log.debug(cm + " could not locate course offering " + courseOfferingEid);
			}
		}
	throw new IdNotFoundException(courseOfferingEid, CanonicalCourse.class.getName());
	}

	public Set getCourseOfferingMemberships(String courseOfferingEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getCourseOfferingMemberships(courseOfferingEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate course offering " + courseOfferingEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the course offering, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
		}
		return resultSet;
	}

	public Set getCourseOfferings(String courseSetEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getCourseOfferings(courseSetEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate course set " + courseSetEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		return resultSet;
	}

	public CourseSet getCourseSet(String eid) throws IdNotFoundException {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				return cm.getCourseSet(eid);
			} catch (IdNotFoundException ide) {
				if(log.isDebugEnabled()) log.debug(cm + " could not locate course set " + eid);
			}
		}
	throw new IdNotFoundException(eid, CourseSet.class.getName());
	}

	public Set getCourseSetMemberships(String courseSetEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getCourseSetMemberships(courseSetEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate course set " + courseSetEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the course set, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
		}
		return resultSet;
	}

	public Set getCourseSets() {
		Set resultSet = new HashSet();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = cm.getCourseSets();
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		return resultSet;
	}

	public List getCurrentAcademicSessions() {
		List resultSet = new ArrayList();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			List list = cm.getCurrentAcademicSessions();
			if(list != null) {
				resultSet.addAll(list);
			}
		}
		// Sort the academic sessions by start date
		Collections.sort(resultSet, startDateComparator);
		return resultSet;
	}

	public EnrollmentSet getEnrollmentSet(String enrollmentSetEid) throws IdNotFoundException {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				return cm.getEnrollmentSet(enrollmentSetEid);
			} catch (IdNotFoundException ide) {
				if(log.isDebugEnabled()) log.debug(cm + " could not locate enrollmentSet " + enrollmentSetEid);
			}
		}
	throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
	}

	public Set getEnrollmentSets(String courseOfferingEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getEnrollmentSets(courseOfferingEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate course offering " + courseOfferingEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the course offering, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
		}
		return resultSet;
	}

	public Set getEnrollments(String enrollmentSetEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions =0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getEnrollments(enrollmentSetEid);
			} catch (IdNotFoundException ide) {
				if(log.isDebugEnabled()) log.debug(cm + " could not locate enrollment set " + enrollmentSetEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the enrollment set, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
		}
		return resultSet;
	}

	public Set getEquivalentCanonicalCourses(String canonicalCourseEid)  throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions =0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getEquivalentCanonicalCourses(canonicalCourseEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate canonical course " + canonicalCourseEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the canonical course, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(canonicalCourseEid, CanonicalCourse.class.getName());
		}
		return resultSet;
	}

	public Set getEquivalentCourseOfferings(String courseOfferingEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions =0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getEquivalentCourseOfferings(courseOfferingEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate course offering " + courseOfferingEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the course offering, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
		}
		return resultSet;
	}

	public Set getInstructorsOfRecordIds(String enrollmentSetEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions =0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getInstructorsOfRecordIds(enrollmentSetEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate enrollment set " + enrollmentSetEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the enrollment set, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
		}
		return resultSet;
	}

	public Section getSection(String sectionEid) throws IdNotFoundException {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				return cm.getSection(sectionEid);
			} catch (IdNotFoundException ide) {
				if(log.isDebugEnabled()) log.debug(cm + " could not locate section " + sectionEid);
			}
		}
		throw new IdNotFoundException(sectionEid, Section.class.getName());
	}

	public Set getSectionMemberships(String sectionEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getSectionMemberships(sectionEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate section " + sectionEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the section, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(sectionEid, Section.class.getName());
		}
		return resultSet;
	}

//	public String getSectionRole(String sectionEid, String userId) {
//		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
//			CourseManagementService cm = (CourseManagementService)implIter.next();
//			String role = cm.getSectionRole(sectionEid, userId);
//			if(role != null) {
//				return role;
//			}
//		}
//		return null;
//	}

	public Set getSections(String courseOfferingEid) throws IdNotFoundException {
		Set resultSet = new HashSet();
		int exceptions = 0;
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = null;
			try {
				set = cm.getSections(courseOfferingEid);
			} catch (IdNotFoundException ide) {
				exceptions++;
				if(log.isDebugEnabled()) log.debug(cm + " could not locate course offering " + courseOfferingEid);
			}
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		// If none of the impls could find the course offering, throw an IdNotFoundException.
		if(exceptions == implList.size()) {
			throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
		}
		return resultSet;
	}

	public boolean isEmpty(String courseSetEid) {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				// If any implementation says that the course set is not empty, it's not empty!
				if(!cm.isEmpty(courseSetEid)) {
					return false;
				}
			} catch (UnsupportedOperationException uso) {
				if(log.isDebugEnabled()) log.debug(cm + " doesn't know whether " + courseSetEid + " is empty");
			}
		}
		return true;
	}

	public boolean isEnrolled(String userId, Set enrollmentSetEids) {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				// If any implementation says that this user is enrolled, they are enrolled!
				if(cm.isEnrolled(userId, enrollmentSetEids)) {
					return true;
				}
			} catch (UnsupportedOperationException uso) {
				if(log.isDebugEnabled()) log.debug(cm + " doesn't know whether " + userId + " is enrolled in any of these enrollment sets: " + enrollmentSetEids);
			}
		}
		return false;
	}

	public boolean isEnrolled(String userId, String enrollmentSetEid) {
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			try {
				// If any implementation says that this user is enrolled, they are enrolled!
				if(cm.isEnrolled(userId, enrollmentSetEid)) {
					return true;
				}
			} catch (UnsupportedOperationException uso) {
				if(log.isDebugEnabled()) log.debug(cm + " doesn't know whether " + userId + " is enrolled in enrollment sets: " + enrollmentSetEid);
			}
		}
		return false;
	}

	protected static Comparator startDateComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			AcademicSession as1 = (AcademicSession)o1;
			AcademicSession as2 = (AcademicSession)o2;
			if(as1.getStartDate() == null && as2.getStartDate() == null) {
				return 0;
			}
			if(as1.getStartDate() == null && as2.getStartDate() != null) {
				return -1;
			}
			if(as1.getStartDate() != null && as2.getStartDate() == null) {
				return 1;
			}
			return as1.getStartDate().compareTo(as2.getStartDate());
		}
	};

	public Set findEnrolledSections(String userId) {
		Set resultSet = new HashSet();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Set set = cm.findEnrolledSections(userId);
			if(set != null) {
				resultSet.addAll(set);
			}
		}
		return resultSet;
	}

	public Map findCourseOfferingRoles(String userEid) {
		Map courseOfferingRoleMap = new HashMap();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Map map = cm.findCourseOfferingRoles(userEid);
			if(map == null) {
				continue;
			}
			for(Iterator mapIter = map.keySet().iterator(); mapIter.hasNext();) {
				String courseSetEid = (String)mapIter.next();
				String role = (String)map.get(courseSetEid);
				// Earlier impls take precedence, so don't overwrite what's in the map
				if( ! courseOfferingRoleMap.containsKey(courseSetEid)) {
					courseOfferingRoleMap.put(courseSetEid, role);
				}
			}
		}
		return courseOfferingRoleMap;
	}

	public Map findCourseSetRoles(String userEid) {
		Map courseSetRoleMap = new HashMap();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Map map = cm.findCourseSetRoles(userEid);
			if(map == null) {
				continue;
			}
			for(Iterator mapIter = map.keySet().iterator(); mapIter.hasNext();) {
				String courseSetEid = (String)mapIter.next();
				String role = (String)map.get(courseSetEid);
				// Earlier impls take precedence, so don't overwrite what's in the map
				if( ! courseSetRoleMap.containsKey(courseSetEid)) {
					courseSetRoleMap.put(courseSetEid, role);
				}
			}
		}
		return courseSetRoleMap;
	}

	public Map findSectionRoles(String userEid) {
		Map sectionRoleMap = new HashMap();
		for(Iterator implIter = implList.iterator(); implIter.hasNext();) {
			CourseManagementService cm = (CourseManagementService)implIter.next();
			Map map = cm.findSectionRoles(userEid);
			if(map == null) {
				continue;
			}
			for(Iterator mapIter = map.keySet().iterator(); mapIter.hasNext();) {
				String sectionEid = (String)mapIter.next();
				String role = (String)map.get(sectionEid);
				// Earlier impls take precedence, so don't overwrite what's in the map
				if( ! sectionRoleMap.containsKey(sectionEid)) {
					sectionRoleMap.put(sectionEid, role);
				}
			}
		}
		return sectionRoleMap;
	}
}
