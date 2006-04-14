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

import org.sakaiproject.coursemanagement.api.CourseSet;

public class CourseSetImpl extends AbstractMembershipContainer
	implements CourseSet {

	private CourseSet parent;
	private Set courseOfferings;
	private Set canonicalCourses;

	public CourseSetImpl() {}
	
	public CourseSetImpl(String eid, String title, String description, CourseSet parent) {
		this.eid = eid;
		this.title = title;
		this.description = description;
		this.parent = parent;
	}
	
	public CourseSet getParent() {
		return parent;
	}
	public void setParent(CourseSet parent) {
		this.parent = parent;
	}
	
	public Set getCourseOfferings() {
		return courseOfferings;
	}
	public void setCourseOfferings(Set courseOfferings) {
		this.courseOfferings = courseOfferings;
	}

	public Set getCanonicalCourses() {
		return canonicalCourses;
	}
	public void setCanonicalCourses(Set canonicalCourses) {
		this.canonicalCourses = canonicalCourses;
	}
}
