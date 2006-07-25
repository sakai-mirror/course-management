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

import java.io.Serializable;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;

public class EnrollmentSetCmImpl extends AbstractNamedCourseManagementObjectCmImpl
	implements EnrollmentSet, Serializable {

	private static final long serialVersionUID = 1L;

	private String category;
	private String defaultEnrollmentCredits;
	private CourseOffering courseOffering;
	private Set officialInstructors;
	
	public EnrollmentSetCmImpl () {}
	
	public EnrollmentSetCmImpl(String eid, String title, String description, String category,
			String defaultEnrollmentCredits, CourseOffering courseOffering, Set officialInstructors) {
		this.eid = eid;
		this.title = title;
		this.description = description;
		this.category = category;
		this.defaultEnrollmentCredits = defaultEnrollmentCredits;
		this.courseOffering = courseOffering;
		this.officialInstructors = officialInstructors;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public CourseOffering getCourseOffering() {
		return courseOffering;
	}

	public void setCourseOffering(CourseOffering courseOffering) {
		this.courseOffering = courseOffering;
	}

	public String getDefaultEnrollmentCredits() {
		return defaultEnrollmentCredits;
	}

	public void setDefaultEnrollmentCredits(String defaultEnrollmentCredits) {
		this.defaultEnrollmentCredits = defaultEnrollmentCredits;
	}

	public Set getOfficialInstructors() {
		return officialInstructors;
	}
	public void setOfficialInstructors(Set officialInstructors) {
		this.officialInstructors = officialInstructors;
	}
	
}
