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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.authz.api.GroupProvider;

/**
 * A Sakai GroupProvider that utilizes the CourseManagementService and the
 * CmMappingService to supply authz data to Sakai.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class CourseManagementGroupProvider implements GroupProvider {
	private static final Log log = LogFactory.getLog(CourseManagementGroupProvider.class);
	
	CourseManagementService cmService;
	Map roleMap;
	String defaultSakaiRole;
	String enrollmentRole;
	
	// GroupProvider methods
	
	/**
	 * Supplies the Sakai role for user in an AuthzGroup by querying the user's membership
	 * in any Sections mapped to the AuthzGroup.  Although Sections may have
	 * child/parent relationships to other Sections, and there may be memberships
	 * "above" the section (e.g. members of the CourseOffering), this implementation
	 * ignores any membership not directly attached to the mapped Section(s).
	 * 
	 * Section membership roles take precedence over enrollments, so if a student
	 * is enrolled in an EnrollmentSet attached to a mapped Section, but is also a
	 * member of a Section, the membership role is returned rather than the enrollment
	 * role.
	 * 
	 * If multiple Sections are mapped to this AuthzGroup, the first Section membership
	 * role is returned.  TODO What should happen in this case?
	 */
	public String getRole(String id, String user) {
		if(log.isDebugEnabled()) log.debug("------------------CMGP.getRole(" + id + ", " + user + ")");
		String[] sectionEids = unpackId(id);
		if(log.isDebugEnabled()) log.debug("Found " + sectionEids.length + " mapped sections for " + id);
		for(int i=0; i < sectionEids.length; i++) {
			String sectionEid = sectionEids[i];
			
			// If this user has a role in a higher-level structure, return that role

			// If this user has a role as a section member, return it
			String sectionRole = cmService.getSectionRole(sectionEid, user);
			if(log.isDebugEnabled()) log.debug(user + "'s section role is " + sectionRole);
			if(sectionRole != null) {
				return convertRole(sectionRole);
			}
			
			// Even though they are not a member, they may be enrolled in an attached EnrollmentSet
			Section section = cmService.getSection(sectionEid);
			EnrollmentSet enrSet = section.getEnrollmentSet();
			if(log.isDebugEnabled()) log.debug( "EnrollmentSet  " + enrSet + " is attached to section " + sectionEid);
			if(enrSet != null && cmService.isEnrolled(user, enrSet.getEid())) {
				return enrollmentRole;
			}
		}
		// The user isn't a member of the section, and isn't enrolled in any attached EnrollmentSet
		if(log.isDebugEnabled()) log.debug("User " + user + " is not associated with any sections in " + sectionEids + " , sakai Ref=" + id);
		return null;
	}
		
	/**
	 * Provides a Map of a user ids to (Sakai) roles for a given AuthzGroup.  Since a
	 * user may be enrolled in a mapped EnrollmentSet and have a Membership role
	 * in a mapped Section, the following order of precedence is applied: Enrollment
	 * is overridden by membership.
	 */
	public Map getUserRolesForGroup(String id) {
		if(log.isDebugEnabled()) log.debug("------------------CMGP.getUserRolesForGroup(" + id + ")");
		Map userRoleMap = new HashMap();
		
		String[] sectionEids = unpackId(id);
		if(log.isDebugEnabled()) log.debug(id + " is mapped to " + sectionEids.length + " sections");
		for(int i=0; i < sectionEids.length; i++) {
			String sectionEid = sectionEids[i];
			if(log.isDebugEnabled()) log.debug("Looking for roles in section " + sectionEid);
			Section section = cmService.getSection(sectionEid);
			
			// Add all Enrollments and graders in any attached EnrollmentSets to the role map
			EnrollmentSet enrSet = section.getEnrollmentSet();
			if(enrSet != null) {
				if(log.isDebugEnabled()) log.debug("EnrollmentSet " + enrSet.getEid() + "  is attached  to section " + sectionEid);
				// Add the enrollments
				Set enrollments = cmService.getEnrollments(enrSet.getEid());
				if(enrollments != null && !enrollments.isEmpty()) {
					for(Iterator enrollmentIter = enrollments.iterator(); enrollmentIter.hasNext();) {
						Enrollment enr = (Enrollment)enrollmentIter.next();
						if(log.isDebugEnabled()) log.debug("Adding " + enr.getUserId() + " to user/role map with role " + enrollmentRole);
						userRoleMap.put(enr.getUserId(), enrollmentRole);
					}
				}
			}
			// Add all memberships to the role map, overriding the enrollments and the graders in case of overlap
			Set sectionMembers = cmService.getSectionMemberships(sectionEid);
			for(Iterator memberIter = sectionMembers.iterator(); memberIter.hasNext();) {
				Membership member = (Membership)memberIter.next();
				if(log.isDebugEnabled()) log.debug("Adding " + member.getUserId() + " to user/role map with role " + member.getRole());
				userRoleMap.put(member.getUserId(), convertRole(member.getRole()));
			}
		}
		return userRoleMap;
	}

	/**
	 * Provides a map of AuthzGroup ids to Sakai roles for a given user.  Enrollment
	 * is overridden by a membership role.
	 */
	public Map getGroupRolesForUser(String userId) {
		if(log.isDebugEnabled()) log.debug("------------------CMGP.getGroupRolesForUser(" + userId + ")");
		Map groupRoleMap = new HashMap();
		
		// Add all enrollments to the role map
		Set enrolledSections = cmService.findEnrolledSections(userId);
		if(log.isDebugEnabled()) log.debug("Found " + enrolledSections.size() + " currently enrolled sections for user " + userId);
		for(Iterator secIter = enrolledSections.iterator(); secIter.hasNext();) {
			Section sec = (Section)secIter.next();
			if(log.isDebugEnabled()) log.debug(userId + " is enrolled in an enrollment set attached to section " + sec.getEid());
			groupRoleMap.put(sec.getEid(),enrollmentRole);
		}
		
		// Next add the section memberships to the role map, overriding enrollments if necessary
		Set memberSections= cmService.findCurrentSectionsWithMember(userId);
		if(log.isDebugEnabled()) log.debug(userId + " is a member of " + memberSections.size() + " sections");
		for(Iterator secIter = memberSections.iterator(); secIter.hasNext();) {
			Section section = (Section)secIter.next();
			String cmRole = cmService.getSectionRole(section.getEid(), userId);
			groupRoleMap.put(section.getEid(), convertRole(cmRole));
		}
		return groupRoleMap;
	}

	public String[] unpackId(String id) {
		if(id == null) {
			return new String[0];
		}
		return id.split("\\+");
	}
	

	// Utility methods

	public void init() {
		if(log.isInfoEnabled()) log.info("initializing " + this.getClass().getName());
	}
	
	public void destroy() {
		if(log.isInfoEnabled()) log.info("destroying " + this.getClass().getName());
	}
	
	private String convertRole(String cmRole) {
		if (cmRole != null) {
			String sakaiRole = (String)roleMap.get(cmRole);
			if(sakaiRole != null) {
				return sakaiRole;
			}
		}
		log.warn("Unable to find sakai role for CM role " + cmRole + ".  Using " + defaultSakaiRole);
		return defaultSakaiRole;
	}

	// Dependency injection
	
	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}

	public void setRoleMap(Map roleMap) {
		this.roleMap = roleMap;
	}
	
	public void setDefaultSakaiRole(String defaultSakaiRole) {
		this.defaultSakaiRole = defaultSakaiRole;
	}
	
	public void setEnrollmentRole(String enrollmentRole) {
		this.enrollmentRole = enrollmentRole;
	}
}