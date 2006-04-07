/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of California
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 * 
 *      https://source.sakaiproject.org/svn/sakai/trunk/sakai_license_1_0.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
package org.sakaiproject.coursemanagement.impl;

import java.util.Set;

/**
 * Models a cross listing between two CrossListable entities.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class CrossListing extends AbstractPersistentCourseManagementObject {

	/**
	 * The set of canonicalCourses that are associated together in this CrossListing
	 */
	private Set canonicalCourses;

	/**
	 * The set of courseOfferings that are associated together in this CrossListing
	 */
	private Set courseOfferings;

	/**
	 * Whether this CrossListing is defined by the enterprise
	 */
	private boolean enterpriseManaged;

	public Set getCanonicalCourses() {
		return canonicalCourses;
	}
	public void setCanonicalCourses(Set canonicalCourses) {
		this.canonicalCourses = canonicalCourses;
	}
	public Set getCourseOfferings() {
		return courseOfferings;
	}
	public void setCourseOfferings(Set courseOfferings) {
		this.courseOfferings = courseOfferings;
	}
	public boolean isEnterpriseManaged() {
		return enterpriseManaged;
	}
	public void setEnterpriseManaged(boolean enterpriseManaged) {
		this.enterpriseManaged = enterpriseManaged;
	}
}
