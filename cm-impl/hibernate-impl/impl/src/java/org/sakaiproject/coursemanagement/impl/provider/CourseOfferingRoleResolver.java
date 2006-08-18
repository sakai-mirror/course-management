package org.sakaiproject.coursemanagement.impl.provider;

import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Section;

public class CourseOfferingRoleResolver implements RoleResolver {

	/**
	 * {@inheritDoc}
	 */
	public String getUserRole(CourseManagementService cmService, String userEid, Section section) {
		// TODO Implement this (as well as other "higher than section" role resolvers) once CM provides methods to search "up" the hierarchy
		return null;
	}

}
