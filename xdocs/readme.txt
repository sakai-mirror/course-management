$URL: $
$Id: $

I. Introduction

	The /course-management module contains the Course Management API, a hibernate
	implementation, and a federating implementation that allows for federating from
	multiple data sources.  See the section on federating CM implementations below.

II. Supplying CM with data

	There are several options for supplying Sakai with CM data via the
	CourseManagement APIs.
	
	A. Loading test data
		To load data into the default hibernate implementation:
		1) Modify cm-impl/hibernate-impl/src/test/hibernate.dataload.properties to match
		your database connection info.
		2) cd course-management/cm-impl/hibernate-impl/impl ; maven load-data;
		3) You may customize this dataloading procedure here:
		org.sakaiproject.coursemanagement.test.CourseManagementAdministrationDataLoader.

	B.Reconciiation
		In order to use the hibernate implementation, you need some way to populate
		the hibernate tables with your enterprise data.  This can be accomplished by
		preiodically running a quartz job to add, update, or remove CM objects from
		the hibernate tables.  A sample job is distributed with Sakai 2.3:
		org.sakaiproject.coursemanagement.impl.job.SampleCMSyncJob.  This job
		reads in simulated enterprise data from an xml file and reconciles the data with
		entries in the hibernate-managed tables.  As of code freeze, this sample
		job is incomplete, so consider it just as a template to use in constructing your
		own reconciliation job.

	C. Re-implementation
		If you prefer to access your enterprise data directly and avoid reconciliation,
		you should write your own implementation of
		org.sakaiproject.coursemanagement.api.CourseManagementService.  As of
		Sakai 2.3, the CourseManagementService is called only via the CM implementation
		of Sakai's GroupProvider.  As more tools start to make direct use of the
		CourseManagementService, the performance requirements of a CM Service
		implementation will change.  This is something to keep in mind when deciding
		on an implementation technology.   If some kind of remoting is used in your
		CM implementation, you should consider adding a caching mechanism as well.

	D. Federating multiple data sources
		Some institutions may use a custom CM Service implementation to connect
		to an external data source which is 'read only' from the perspective of the
		Sakai administrators.  Using the federated implementation allows an admin to
		append to or override some of the data provided by the custom implementation.
		Sakai's default configuration for CM uses the
		org.sakaiproject.coursemanagement.impl.CourseManagementServiceFederatedImpl
		implementation to demonstrate how to configure a federated service.  See the
		javadocs on this class and on
		org.sakaiproject.coursemanagement.impl.CourseManagementServiceSampleChainImpl
		for more details on writing an implementation that can participate in a federated
		configuration.

III. Configuring Sakai to use CM
	A. CourseManagementProvider
		The legacy org.sakaiproject.site.api.CourseManagementProvider interface delivers
		course information to the legacy org.sakaiproject.site.api.CourseManagement
		service, and hence to the Site Info and Worksite Setup tools.  To avoid reworking
		these tools to utilize the new CourseManagementService, we've simply added
		an adapter to translate calls from the legacy provider to the new service.  See
		org.sakaiproject.coursemanagement.impl.provider.CourseManagementProviderCMImpl.
		Because there is a mis-match between the two APIs, this adapter makes some
		assumptions about the data returned by the CM service.  The most eggregious
		is in getInstructorCourses(), where AcademicSessionEids are expected to
		contain the termYear and termTerm strings as substrings.  Please review this
		implementation to ensure that it adequately converts between the legacy and
		the current crop of CM objects.

	B. GroupProvider
		The CourseManagementGroupProvider uses memberships, enrollments,
		and official instructing status to determine what role, if any, a user has in a site.
		It uses an ordered list of resolvers to check for roles at different levels of the
		CM hierarchy.  To configure Sakai to use this GroupProvider:
		1) Uncomment the block of xml containing the bean for class
		org.sakaiproject.coursemanagement.impl.provider.CourseManagementGroupProvider
		in /providers/component/src/webapp/WEB-INF/components.xml
		2) Configure the role resolvers you want to use in resolving users memberships
		in sites linked to CM-defined Sections.
		
		In the following example, only the SectionRoleResolver will be used to find users
		and their memberships in Sections.  Any memberships defined above this level
		in the CM hierarchy will not be resolved, and hence will not be added to sites
		linked to a Section.
		
		<property name="roleResolvers">
			<list>
				<bean class="org.sakaiproject.coursemanagement.impl.provider.SectionRoleResolver">
					<property name="roleMap">
						<map>
							<entry key="I" value="Instructor" />
							<entry key="S" value="Student" />
							<entry key="GSI" value="Teaching Assistant"/>
						</map>
					</property>
					<property name="officialInstructorRole" value="Instructor" />
					<property name="enrollmentRole" value="Student" />
				</bean>
			</list>
		</property>

		You may add as many RoleResolvers as you like.  Sakai's default configuration
		includes a SectionRoleResolver, a CourseOfferingRoleResolver, and a
		CourseSetRoleResolver.
		
		3) Configure the roleMap for each role resolver.  In the example above, if a
		Section member in CM has a role of 'I', this will be translated to the 'Instructor'
		Sakai role for this site.  The ordering of the RoleResolvers is important.  Earlier
		entries override later ones.
