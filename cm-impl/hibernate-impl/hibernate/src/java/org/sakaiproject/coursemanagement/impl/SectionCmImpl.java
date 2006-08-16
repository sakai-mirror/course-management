package org.sakaiproject.coursemanagement.impl;

import java.io.Serializable;

import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;

public class SectionCmImpl extends AbstractMembershipContainerCmImpl
	implements Section, Serializable {

	private static final long serialVersionUID = 1L;

	private String category;
	private CourseOffering courseOffering;
	private Section parent;
	private EnrollmentSet enrollmentSet;
	
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
}
