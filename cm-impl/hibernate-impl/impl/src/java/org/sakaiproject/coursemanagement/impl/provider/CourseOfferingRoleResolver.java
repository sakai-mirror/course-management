package org.sakaiproject.coursemanagement.impl.provider;

import java.util.Iterator;
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
	public String getUserRole(CourseManagementService cmService, String userEid, Section section) {
		Set coMembers = cmService.getCourseOfferingMemberships(section.getCourseOfferingEid());
		if(coMembers != null) {
			for(Iterator iter = coMembers.iterator(); iter.hasNext();) {
				Membership membership = (Membership)iter.next();
				if(membership.getUserId().equals(userEid)) {
					return membership.getRole();
				}
			}
		}
		if(log.isDebugEnabled()) log.debug(userEid + " is has no membership in CourseOffering" + section.getCourseOfferingEid());
		return null;
	}

}
