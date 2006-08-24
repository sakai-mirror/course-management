package org.sakaiproject.coursemanagement.impl.provider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;

/**
 * Resolves user roles in CourseOfferings.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class CourseOfferingRoleResolver implements RoleResolver {
	private static final Log log = LogFactory.getLog(CourseOfferingRoleResolver.class);
	
	/**
	 * {@inheritDoc}
	 */
	public Map getUserRoles(CourseManagementService cmService, Section section) {
		Map userRoleMap = new HashMap();
		Set coMembers = cmService.getCourseOfferingMemberships(section.getCourseOfferingEid());
		if(coMembers != null) {
			if(log.isDebugEnabled()) log.debug(coMembers.size() +" members in course offering " + section.getCourseOfferingEid());
			for(Iterator iter = coMembers.iterator(); iter.hasNext();) {
				Membership membership = (Membership)iter.next();
				if(log.isDebugEnabled()) log.debug("Adding " + membership.getUserId() +
						" with role " + membership.getRole() + " for section " + section.getEid());
				userRoleMap.put(membership.getUserId(), membership.getRole());
			}
		}
		return userRoleMap;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getGroupRoles(CourseManagementService cmService, String userEid) {
		Map sectionRoles = new HashMap();
		Map courseOfferingRoles = cmService.findCourseOfferingRoles(userEid);
		if(log.isDebugEnabled()) log.debug("Found " + courseOfferingRoles.size() + " course offering roles for " + userEid);
		for(Iterator coIter = courseOfferingRoles.keySet().iterator(); coIter.hasNext();) {
			String coEid = (String)coIter.next();
			String coRole = (String)courseOfferingRoles.get(coEid);
			if(log.isDebugEnabled()) log.debug(userEid + " has role=" + coRole + " in course offering " + coEid);
			// Get the sections in each course offering
			Set sections = cmService.getSections(coEid);
			for(Iterator secIter = sections.iterator(); secIter.hasNext();) {
				// Add the section EIDs and *CourseOffering* role to the sectionRoles map
				Section section = (Section)secIter.next();
				sectionRoles.put(section.getEid(), coRole);
			}
		}
		return sectionRoles;
	}

}
