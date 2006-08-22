package org.sakaiproject.coursemanagement.api;

/**
 * A time and a place for a Section to meet.  Meetings are completely controlled by
 * their sections.  To add a Meeting to a Section, call section.getMeetings() and operate
 * on the List of meetings.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public interface Meeting {
	public String getLocation();
	public void setLocation(String location);
	public String getTime();
	public void setTime(String time);
	public String getNotes();
	public void setNotes(String notes);
}
