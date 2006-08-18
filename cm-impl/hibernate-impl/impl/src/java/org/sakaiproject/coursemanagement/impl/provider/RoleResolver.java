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

import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Section;

public interface RoleResolver {

	/**
	 * Used to communicate with the CM Group Provider
	 */
	public static final String ENROLLMENT_ROLE="sakai.cm.enrollment";
	
	/**
	 * Gets the user's role in a CM object.  A RoleResolver implementation
	 * will typically use the cmService to look "up" from the section in the CM
	 * hierarchy to find the object it's interested in, then find any membership roles
	 * associated with the user.
	 * 
	 * @param userEid The user's enterprise ID
	 * @param section The section from which to start searching "up" the hierarchy
	 * @return The user's role, or null if the user has no role in this CM object
	 */
	public String getUserRole(CourseManagementService cmService, String userEid, Section section);
}
