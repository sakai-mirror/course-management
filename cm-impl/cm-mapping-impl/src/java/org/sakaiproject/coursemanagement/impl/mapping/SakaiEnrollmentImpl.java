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
package org.sakaiproject.coursemanagement.impl.mapping;

import java.util.Map;

import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.mapping.SakaiEnrollment;

public class SakaiEnrollmentImpl implements SakaiEnrollment {

	protected String authzGroupId;
	protected String userEid;
	protected Map enrollmentMap;
	
	/**
	 * Constructs a SakaiEnrollmentImpl.
	 * 
	 * @param userEid The user EID
	 * @param authzGroupId The id of the authzGroup for this SakaiEnrollment
	 * @param sectionEnrollments The map of enrollmentSet EIDs to enrollments
	 */
	public SakaiEnrollmentImpl(String userEid, String authzGroupId, Map enrollmentMap) {
		this.userEid = userEid;
		this.authzGroupId = authzGroupId;
		this.enrollmentMap = enrollmentMap;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getAuthzGroupId() {
		return authzGroupId;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserEid() {
		return userEid;
	}

	/**
	 * {@inheritDoc}
	 */
	public Enrollment getEnrollment(String enrollmentSetEid) {
		return (Enrollment)enrollmentMap.get(enrollmentSetEid);
	}
}
