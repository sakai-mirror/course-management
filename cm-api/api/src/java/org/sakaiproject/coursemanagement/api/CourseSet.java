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
package org.sakaiproject.coursemanagement.api;

/**
 * Models "School" and "Department" as well as more ad hoc groupings.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface CourseSet {

	/**
	 * Gets the unique enterprise id of this MembershipContainer.
	 * @return
	 */
	public String getEid();

	/**
	 * What authority defines this object?
	 * @return 
	 */
	public String getAuthority();

	/**
	 * Gets the title of this MembershipContainer.
	 * @return
	 */
	public String getTitle();
	
	/**
	 * Gets the description of this MembershipContainer.
	 * @return
	 */
	public String getDescription();

	/**
	 * A category
	 * @return
	 */
	public String getCategory();

	/**
	 * Gets the parent CourseSet for this CourseSet, or null if this is a top-level CourseSet.
	 * 
	 * @return
	 */
	public CourseSet getParent();
}
