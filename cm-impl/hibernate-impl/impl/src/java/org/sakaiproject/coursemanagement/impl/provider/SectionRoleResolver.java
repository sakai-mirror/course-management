package org.sakaiproject.coursemanagement.impl.provider;

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

public class SectionRoleResolver implements RoleResolver {
	private static final Log log = LogFactory.getLog(SectionRoleResolver.class);

	/**
	 * {@inheritDoc}
	 */
	public Map getUserRoles(CourseManagementService cmService, Section section) {
		Map userRoleMap = new HashMap();

		EnrollmentSet enrSet = section.getEnrollmentSet();
		if(log.isDebugEnabled()) log.debug( "EnrollmentSet  " + enrSet + " is attached to section " + section.getEid());
		if(enrSet != null) {
			// Check for official instructors
			Set officialInstructors = cmService.getInstructorsOfRecordIds(enrSet.getEid());
			for(Iterator iter = officialInstructors.iterator(); iter.hasNext();) {
				userRoleMap.put(iter.next(), OFFICIAL_INSTRUCTOR_ROLE);
			}

			// Check for enrollments
			Set enrollments = cmService.getEnrollments(section.getEnrollmentSet().getEid());
			for(Iterator iter = enrollments.iterator(); iter.hasNext();) {
				Enrollment enr = (Enrollment)iter.next();
				if( ! userRoleMap.containsKey(enr.getUserId())) {
					// If they are an official instructor and also enrolled (huh?), defer to the instructor status
					userRoleMap.put(enr.getUserId(), ENROLLMENT_ROLE);
				}
			}
		}
		
		// Check for memberships
		Set memberships = cmService.getSectionMemberships(section.getEid());
		for(Iterator iter = memberships.iterator(); iter.hasNext();) {
			Membership membership = (Membership)iter.next();
			// Only add the membership role if the user isn't enrolled or an official instructor(these take precedence)
			if( ! userRoleMap.containsKey(membership.getUserId())) {
				userRoleMap.put(membership.getUserId(), membership.getRole());
			}
		}
		return userRoleMap;
	}

	public Map getGroupRoles(CourseManagementService cmService, String userEid) {
		// Start with the sectionEid->role map
		Map groupRoleMap = cmService.findSectionRoles(userEid);

		// Next add all enrollments to the sectionEid->role map, overriding memberships
		Set enrolledSections = cmService.findEnrolledSections(userEid);
		if(log.isDebugEnabled()) log.debug("Found " + enrolledSections.size() + " currently enrolled sections for user " + userEid);
		for(Iterator secIter = enrolledSections.iterator(); secIter.hasNext();) {
			Section section = (Section)secIter.next();
			if(log.isDebugEnabled()) log.debug(userEid + " is enrolled in an enrollment set attached to section " + section.getEid());
			groupRoleMap.put(section.getEid(), ENROLLMENT_ROLE);
		}

		// Finally, add the official instructors, overriding any other roles if necessary
		Set instructingSections = cmService.findInstructingSections(userEid);
		for(Iterator iter = instructingSections.iterator(); iter.hasNext();) {
			Section instructingSection = (Section)iter.next();
			groupRoleMap.put(instructingSection.getEid(), OFFICIAL_INSTRUCTOR_ROLE);
		}
		
		if(log.isDebugEnabled()) {
			for(Iterator iter = groupRoleMap.keySet().iterator(); iter.hasNext();) {
				String sectionEid = (String)iter.next();
				log.debug("User " + userEid + " has role " + groupRoleMap.get(sectionEid) + " in " + sectionEid);
			}
		}
		return groupRoleMap;
	}
}
