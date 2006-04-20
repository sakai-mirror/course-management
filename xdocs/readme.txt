The /course-management module contains the new Course Management API, a hibernate implementation, and (soon) a webservices implementation.

To load data into the default hibernate implementation,

1) Add your dataloading logic to org.sakaiproject.coursemanagement.test.CourseManagementAdministrationDataLoader.  Use the supplied cmAdmin and cmService fields to read and write CM data.
2) Modify cm-impl/hibernate-impl/src/test/hibernate.dataload.properties to match your database connection info.
3) Run 'maven load-data'.


Josh Holtzman
jholtzman@berkeley.edu