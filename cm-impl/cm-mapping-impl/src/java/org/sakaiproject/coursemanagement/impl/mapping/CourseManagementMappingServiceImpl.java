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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Enrollment;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.coursemanagement.api.mapping.CourseManagementMappingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;

public class CourseManagementMappingServiceImpl implements CourseManagementMappingService {

	private static final Log log = LogFactory.getLog(CourseManagementMappingServiceImpl.class);
	
	protected AuthzGroupService authzGroupService;
	protected SiteService siteService;
	protected CourseManagementService courseManagementService;
	
	/**
	 * {@inheritDoc}
	 */
	public Set getAuthzGroupIds(String sectionEid) {
		return authzGroupService.getAuthzGroupIds(sectionEid);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getSakaiEnrollments(String siteContext) {
		Site site = null;
		try {
			site = siteService.getSite(siteContext);
		} catch (IdUnusedException e) {
			log.error("Unable to find site with id = " + siteContext);
			return new HashMap();
		}
		String siteReference = site.getReference();
		return getSakaiEnrollmentsByAuthzGroupId(siteReference);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getSakaiEnrollmentsByAuthzGroupId(String authzGroupId) {
		Map sakaiEnrollmentsMap = new HashMap();
		
		// Get the sections associated with this authz group
		Set sections = getOfficialSectionsByAuthzGroup(authzGroupId);
		
		// Get the members of this authz group
		AuthzGroup azg = null;
		try {
			azg = authzGroupService.getAuthzGroup(authzGroupId);
		} catch (GroupNotDefinedException e) {
			log.error("Could not find an authzGroup with ID " + authzGroupId);
			return sakaiEnrollmentsMap;
		}
		
		// For each member, find which enrollments they have in the relevant sections
		for(Iterator memberIter = azg.getMembers().iterator(); memberIter.hasNext();) {
			Member member = (Member)memberIter.next();
			Map enrollmentMap = new HashMap();
			for(Iterator sectionIter = sections.iterator(); sectionIter.hasNext();) {
				Section section = (Section)sectionIter.next();
				EnrollmentSet enrollmentSet = section.getEnrollmentSet();
				if(enrollmentSet == null) {
					continue;
				}
				Enrollment enrollment = courseManagementService.findEnrollment(member.getUserEid(), section.getEnrollmentSet().getEid());
				if(enrollment != null) {
					enrollmentMap.put(enrollmentSet.getEid(), enrollment);
				}
			}
			// Add a new SakaiEnrollment for this user if there's anything in the enrollmentMap
			if(enrollmentMap.size() > 0) {
				sakaiEnrollmentsMap.put(member.getUserId(), new SakaiEnrollmentImpl(member.getUserEid(), authzGroupId, enrollmentMap));
			}
		}
		return sakaiEnrollmentsMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getOfficialSections(String siteContext) {
		Site site = null;
		try {
			site = siteService.getSite(siteContext);
		} catch (IdUnusedException e) {
			log.error("Unable to find site with id = " + siteContext);
			return new HashSet();
		}
		String siteReference = site.getReference();
		return getOfficialSectionsByAuthzGroup(siteReference);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getOfficialSectionsByAuthzGroup(String authzGroupId) {
		Set sectionEids =  authzGroupService.getProviderIds(authzGroupId);
		Set sections = new HashSet();
		for(Iterator iter = sectionEids.iterator(); iter.hasNext();) {
			String sectionEid = (String)iter.next();
			try {
				sections.add(courseManagementService.getSection(sectionEid));
			} catch (IdNotFoundException idnfe) {
				log.error("Sakai has a mapping to a section that, according to CM, does not exist: " + sectionEid);
			}
		}
		return sections;
	}

	// Dependency injection

	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}

	public void setCourseManagementService(
			CourseManagementService courseManagementService) {
		this.courseManagementService = courseManagementService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

}
