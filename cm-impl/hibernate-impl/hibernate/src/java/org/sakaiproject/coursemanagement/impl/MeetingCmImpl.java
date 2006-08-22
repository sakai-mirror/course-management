package org.sakaiproject.coursemanagement.impl;

import java.io.Serializable;

import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Section;

public class MeetingCmImpl extends AbstractPersistentCourseManagementObjectCmImpl implements Meeting, Serializable {
	private static final long serialVersionUID = 1L;

	private Section section;
	
	private String location;
	private String time;
	private String notes;
	
	public MeetingCmImpl() {}
	
	public MeetingCmImpl(Section section, String location, String time) {
		this.section = section;
		this.location = location;
		this.time = time;
	}
	public MeetingCmImpl(Section section, String location, String time, String notes) {
		this.section = section;
		this.location = location;
		this.time = time;
		this.notes = notes;
	}
	
	public Section getSection() {
		return section;
	}
	public void setSection(Section section) {
		this.section = section;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
