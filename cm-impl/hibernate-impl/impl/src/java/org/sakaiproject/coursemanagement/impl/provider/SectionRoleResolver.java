package org.sakaiproject.coursemanagement.impl.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;

public class SectionRoleResolver implements RoleResolver {
	private static final Log log = LogFactory.getLog(SectionRoleResolver.class);

	/**
	 * {@inheritDoc}
	 */
	public String getUserRole(CourseManagementService cmService, String userEid, Section section) {
		
		// If this user is enrolled in a section, return the enrollment role
		EnrollmentSet enrSet = section.getEnrollmentSet();
		if(log.isDebugEnabled()) log.debug( "EnrollmentSet  " + enrSet + " is attached to section " + section.getEid());
		if(enrSet != null && cmService.isEnrolled(userEid, enrSet.getEid())) {
			return ENROLLMENT_ROLE;
		}

		// If this user has a role as a section member, return it
		String sectionRole = cmService.getSectionRole(section.getEid(), userEid);
		if(log.isDebugEnabled()) log.debug(userEid + "'s section role is " + sectionRole);
		if(sectionRole != null) {
			return sectionRole;
		}
		
		// This user is not enrolled in the section, and doesn't have a section membership
		return null;
	}
}
