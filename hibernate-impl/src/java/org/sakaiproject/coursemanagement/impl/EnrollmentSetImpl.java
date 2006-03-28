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

import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;

public class EnrollmentSetImpl extends AbstractPersistentCourseManagementObject
	implements EnrollmentSet {

	/**
	 * The DB's primary key for this object / record.
	 */
	private long key;
	
	/**
	 * The object instance version for optimistic locking.
	 */
	private int version;

	private String eid;
	private String title;
	private String description;
	private String category;
	private String defaultEnrollmentCredits;
	private CourseOffering courseOffering;
	private Set officialGraders;
	
	/**
	 * The Section associated with this EnrollmentSet.  This may be null.
	 */
	private Section section;

	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}

	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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

	public Section getSection() {
		return section;
	}
	public void setSection(Section section) {
		this.section = section;
	}

	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public Set getOfficialGraders() {
		return officialGraders;
	}
	public void setOfficialGraders(Set officialGraders) {
		this.officialGraders = officialGraders;
	}
	
}
