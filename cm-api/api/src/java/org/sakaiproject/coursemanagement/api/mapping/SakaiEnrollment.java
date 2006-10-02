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

import org.sakaiproject.coursemanagement.api.Enrollment;

/**
 * A SakaiEnrollment is a bridge between a sakai membership (either in a site or a group)
 * and an Enrollment (if one exists) in an enterprise-defined EnrollmentSet that is
 * associated with the sakai site or group.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public interface SakaiEnrollment {

	/**
	 * Gets the user's enterprise ID.
	 * 
	 * @return
	 */
	public String getUserEid();
	
	/**
	 * Gets the authzGroup ID with which this SakaiEnrollment is associated.
	 * 
	 * @return
	 */
	public String getAuthzGroupId();
	
	/**
	 * Gets the Enrollment for this user in the specified EnrollmentSet.
	 * 
	 * @return
	 */
	public Enrollment getEnrollment(String enrollmentSetEid);
	
}
