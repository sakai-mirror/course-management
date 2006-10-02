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
package org.sakaiproject.coursemanagement.api.mapping;

import java.util.Map;
import java.util.Set;

/**
 * The CourseManagementMappingService provides clients of the CM API with convenient
 * method of accessing CM data based on the current Sakai context.  Note that
 * this service is only available when using a CM-based GroupProvider.
 * 
 * A typical use case involved listing the members of a site.  Some of the site members
 * may be "provided" by an official Section (the section EID is the site's provider id),
 * while others were manually added to the site.  In the list of site members, the UI
 * displays "Enrollment Status".  The enrollment status is defined in the Enrollment
 * object from CourseManagement.  To find the Enrollment objects associated with
 * this site, you would call CourseManagementMappingService.getSakaiEnrollments(siteContext).
 * 
 * By using this service in our CM clients, we encapsulate the mechanism by which
 * we are currently mapping Sakai Sites and Groups to CM Sections.
 * 
 * TODO:  The name of this service is not very clear... rename it.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public interface CourseManagementMappingService {

	
	/**
	 * A convenience method used to get the enterprise-defined sections associated with a site.
	 * 
	 * @param siteContext
	 * @return
	 */
	public Set getOfficialSections(String siteContext);
	
	/**
	 * Gets the enterprise-defined sections associated with the authzGroup.
	 * 
	 * @param authzGroupId The ID of the AuthzGroup
	 * @return A set of section EIDs
	 */
	public Set getOfficialSectionsByAuthzGroup(String authzGroupId);
	
	/**
	 * Gets the Sakai authzGroupIds with are associated with an enterprise-defined section.
	 * 
	 * @param sectionEid The section's EID
	 * @return A set of authzGroup IDs
	 */
	public Set getAuthzGroupIds(String sectionEid);
	
	/**
	 * Gets the Map of user IDs (not EIDs) to SakaiEnrollment objects for a given
	 * AuthzGroup.
	 * 
	 * @param authzGroupId The authzGroup ID
	 * @return A Map of User IDs (not EIDs) to SakaiEnrollment objects
	 */
	public Map getSakaiEnrollmentsByAuthzGroupId(String authzGroupId);

	/**
	 * A convenience method used to get the SakaiEnrollment objects associated with a Site.
	 * 
	 * @param authzGroupId The authzGroup ID
	 * @return A Map of User IDs (not EIDs) to SakaiEnrollment objects
	 */
	public Map getSakaiEnrollments(String siteContext);

}
