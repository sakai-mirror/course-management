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

import java.util.Map;

import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Section;

/**
 * Resolves users roles in sections.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public interface RoleResolver {

	/**
	 * Gets users roles in a CM object.  A RoleResolver implementation
	 * will typically use the cmService to look "up" from the section in the CM
	 * hierarchy to find the object it's interested in, then find any membership roles
	 * associated with the user.
	 * 
	 * @param section The section from which to start searching "up" the hierarchy,
	 * if necessary
	 * @param cmService The CM service impl.  We pass this in rather than injecting
	 * it into every RoleResolver
	 * 
	 * @return The user's role, or null if the user has no role in this CM object
	 */
	public Map getUserRoles(CourseManagementService cmService, Section section);

	/**
	 * Gets a single user's roles in all sections with which s/he is associated.
	 * 
	 * @param userEid The user's enterprise ID
	 * @param cmService The CM service impl.  We pass this in rather than injecting
	 * it into every RoleResolver
	 * 
	 * @return The user's role, or null if the user has no role in this CM object
	 */
	public Map getGroupRoles(CourseManagementService cmService, String userEid);
	
	/**
	 * Converts a CM role to a Sakai role.
	 * 
	 * @param cmRole The role according to CM
	 * @return The role to use in a Sakai site or group, or null if the CM role should
	 * not be expressed as a role in a Sakai site or group.
	 */
	String convertRole(String cmRole);
}
