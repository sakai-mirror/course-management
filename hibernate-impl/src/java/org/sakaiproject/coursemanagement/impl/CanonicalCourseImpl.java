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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;

public class CanonicalCourseImpl implements CanonicalCourse, CrossListable {
	
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
	private CrossListing crossListing;
	private Set courseSets;

	public Set getCourseSets() {
		return courseSets;
	}
	public void setCourseSets(Set courseSets) {
		this.courseSets = courseSets;
	}
	
	public CrossListing getCrossListing() {
		return crossListing;
	}
	public void setCrossListing(CrossListing crossListing) {
		this.crossListing = crossListing;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public boolean equals(Object o) {
		CanonicalCourse other = (CanonicalCourse)o;
		return new EqualsBuilder().append(eid, other.getEid()).isEquals();
	}
}
