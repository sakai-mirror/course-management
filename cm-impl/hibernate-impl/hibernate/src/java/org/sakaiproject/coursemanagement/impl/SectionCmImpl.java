package org.sakaiproject.coursemanagement.impl;

import java.io.Serializable;
import java.util.Set;

import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;

public class SectionCmImpl extends AbstractMembershipContainerCmImpl
	implements Section, Serializable {

	private static final long serialVersionUID = 1L;

	private String category;
	private Set meetings;
	private CourseOffering courseOffering;
	private Section parent;
	private EnrollmentSet enrollmentSet;
        private Integer maxSize;
	
	public SectionCmImpl() {}
	
	public SectionCmImpl(String eid, String title, String description, String category, Section parent, CourseOffering courseOffering, EnrollmentSet enrollmentSet) {
		this.eid = eid;
		this.title = title;
		this.description = description;
		this.category = category;
		this.parent = parent;
		this.courseOffering = courseOffering;
		this.enrollmentSet = enrollmentSet;
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
	public String getCourseOfferingEid() {
		if(courseOffering == null) {
			return null;
		}
		return courseOffering.getEid();
	}
	public void setCourseOffering(CourseOffering courseOffering) {
		this.courseOffering = courseOffering;
	}
	public Section getParent() {
		return parent;
	}
	public void setParent(Section parent) {
		this.parent = parent;
	}
	public EnrollmentSet getEnrollmentSet() {
		return enrollmentSet;
	}
	public void setEnrollmentSet(EnrollmentSet enrollmentSet) {
		this.enrollmentSet = enrollmentSet;
	}
	public Set getMeetings() {
		return meetings;
	}
	public void setMeetings(Set meetings) {
		this.meetings = meetings;
	}
	public Integer getMaxSize() {
		return maxSize;
	}
        public void setMaxSize(Integer maxSize) {
	    this.maxSize = maxSize;
	}
}
