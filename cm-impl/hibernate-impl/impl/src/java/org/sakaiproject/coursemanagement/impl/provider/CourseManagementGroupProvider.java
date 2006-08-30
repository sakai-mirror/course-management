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
package org.sakaiproject.coursemanagement.impl.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
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
	
	/** The course management service */
	CourseManagementService cmService;
	
	/** Map of CM roles to Sakai roles */
	Map roleMap;
	
	/** The role to use in sakai when a user has a CM role that is not mapped in the roleMap */
	String defaultSakaiRole;
	
	/** The Sakai role to use for official instructors of EnrollmentSets */
	String officialInstructorRole;

	/** The Sakai role to use for official enrollments in EnrollmentSets */
	String enrollmentRole;
	
	/** The role resolvers to use when looking for CM roles in the hierarchy*/
	List roleResolvers;
	
	// GroupProvider methods
	
	/**
	 * This method is not longer in use in Sakai.  It should be removed from the
	 * GroupProvider interface.
	 */
	public String getRole(String id, String user) {
		log.error("\n------------------------------------------------------------------\n");
		log.error("THIS METHOD IS NEVER CALLED IN SAKAI.  WHAT HAPPENED???");
		log.error("\n------------------------------------------------------------------\n");
		return null;
	}
		
	/**
	 * Provides a Map of a user ids to (Sakai) roles for a given AuthzGroup.  Since a
	 * user may be both enrolled in a mapped EnrollmentSet and have a Membership
	 * role in a mapped Section, the following order of precedence is applied:
	 * Official Instructor, Enrollment, membership
	 */
	public Map getUserRolesForGroup(String id) {
		if(log.isDebugEnabled()) log.debug("------------------CMGP.getUserRolesForGroup(" + id + ")");
		Map userRoleMap = new KeySplittingMap();
		
		String[] sectionEids = unpackId(id);
		if(log.isDebugEnabled()) log.debug(id + " is mapped to " + sectionEids.length + " sections");
		for(int i=0; i < sectionEids.length; i++) {
			String sectionEid = sectionEids[i];
			Section section = cmService.getSection(sectionEid);
			if(log.isDebugEnabled()) log.debug("Looking for roles in section " + sectionEid);
			for(Iterator rrIter = roleResolvers.iterator(); rrIter.hasNext();) {
				RoleResolver rr = (RoleResolver)rrIter.next();
				Map rrUserRoleMap = rr.getUserRoles(cmService, section);
				// Only add the roles if they aren't already in the map.  Earlier resolvers take precedence.
				for(Iterator rrRoleIter = rrUserRoleMap.keySet().iterator(); rrRoleIter.hasNext();) {
					String userEid = (String)rrRoleIter.next();
					String existingRole = (String)userRoleMap.get(userEid);
					String rrRole = (String)rrUserRoleMap.get(userEid);
					if(existingRole == null && rrRole != null) {
						String sakaiRole = convertRole(rrRole);
						if(log.isDebugEnabled()) log.debug("Adding "+ userEid + " to userRoleMap with role=" + sakaiRole);
						userRoleMap.put(userEid, sakaiRole);
					}
				}
			}
		}
		if(log.isDebugEnabled()) log.debug("_____________getUserRolesForGroup=" + userRoleMap);
		return userRoleMap;
	}

	/**
	 * Provides a map of AuthzGroup ids to Sakai roles for a given user.  Enrollment
	 * is overridden by a membership role.
	 */
	public Map getGroupRolesForUser(String userEid) {
		if(log.isDebugEnabled()) log.debug("------------------CMGP.getGroupRolesForUser(" + userEid + ")");
		Map groupRoleMap = new KeySplittingMap();
		
		for(Iterator rrIter = roleResolvers.iterator(); rrIter.hasNext();) {
			RoleResolver rr = (RoleResolver)rrIter.next();
			Map rrGroupRoleMap = rr.getGroupRoles(cmService, userEid);
			if(log.isDebugEnabled()) log.debug("Found " + rrGroupRoleMap.size() + " groups for " + userEid + " from resolver " + rr.getClass().getName());

			// Only add the section eids if they aren't already in the map.  Earlier resolvers take precedence.
			for(Iterator rrRoleIter = rrGroupRoleMap.keySet().iterator(); rrRoleIter.hasNext();) {
				String sectionEid = (String)rrRoleIter.next();
				if(groupRoleMap.containsKey(sectionEid)) {
					if(log.isDebugEnabled()) log.debug("User " + userEid + " is already in the groupRoleMap under section " + sectionEid + " with role " + groupRoleMap.get(sectionEid));
					continue;
				}
				String rrRole = (String)rrGroupRoleMap.get(sectionEid);
				if( rrRole != null) {
					String sakaiRole = convertRole(rrRole);
					if(log.isDebugEnabled()) log.debug("Adding " + sectionEid + " to groupRoleMap with sakai role" + sakaiRole + " for user " + userEid);
					groupRoleMap.put(sectionEid, sakaiRole);
				}
			}
		}
		if(log.isDebugEnabled()) log.debug("______________getGroupRolesForUser=" + groupRoleMap);
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
		if (cmRole == null) {
			log.warn("Can not convert CM role 'null' to a sakai role.  Using default: " + defaultSakaiRole);
			return defaultSakaiRole;
		}

		if(cmRole.equals(RoleResolver.OFFICIAL_INSTRUCTOR_ROLE)) {
			return this.officialInstructorRole;
		} else if(cmRole.equals(RoleResolver.ENROLLMENT_ROLE)) {
			return this.enrollmentRole;
		} else {
			String sakaiRole = (String)roleMap.get(cmRole);
			if(sakaiRole== null) {
				log.warn("Unable to find sakai role for CM role " + cmRole + ".  Using " + defaultSakaiRole);
				return defaultSakaiRole;
			} else {
				return sakaiRole;
			}
		}
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
	
	public void setOfficialInstructorRole(String officialInstructorRole) {
		this.officialInstructorRole = officialInstructorRole;
	}

	public void setEnrollmentRole(String enrollmentRole) {
		this.enrollmentRole = enrollmentRole;
	}

	public void setRoleResolvers(List roleResolvers) {
		this.roleResolvers = roleResolvers;
	}
	
	/**
	 * A custom map implementation, required by the authz / group provider architecture,
	 * that checks inside compound IDs when looking up keys.
	 *
	 */
	public class KeySplittingMap extends HashMap {
		private static final long serialVersionUID = -6810251970110503758L;

		public Object get(Object key) {
			// if we have this key exactly, use it
			Object value = super.get(key);
			if (value != null) return (String)value;

			// If the key wasn't found, break it up as a compound id
			// The role resolvers ensure that we have only one role per key, so just return the first one we find
			String[] ids = unpackId((String) key);
			for (int i = 0; i < ids.length; i++) {
				value = super.get(ids[i]);
				if ((value != null)) {
					return value;
				}
			}
			
			// If even the exploded keys can't be located, return null
			return null;
		}
	}
}