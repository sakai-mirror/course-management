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

import java.util.Date;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseOffering;

public class CourseOfferingImpl extends CrossListable
	implements CourseOffering {

	private CanonicalCourse canonicalCourse;
	private AcademicSession academicSession;
	private CrossListing crossListing;
	private Set courseSets;
	private Date startDate;
	private Date endDate;

	public CourseOfferingImpl() {}
	
	public CourseOfferingImpl(String eid, String title, String description, AcademicSession academicSession, Date startDate, Date endDate) {
		this.eid = eid;
		this.title = title;
		this.description = description;
		this.academicSession = academicSession;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
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

	public CanonicalCourse getCanonicalCourse() {
		return canonicalCourse;
	}
	public void setCanonicalCourse(CanonicalCourse canonicalCourse) {
		this.canonicalCourse = canonicalCourse;
	}
	
	public AcademicSession getAcademicSession() {
		return academicSession;
	}
	public void setAcademicSession(AcademicSession academicSession) {
		this.academicSession = academicSession;
	}

	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
