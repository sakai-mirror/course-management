package org.sakaiproject.coursemanagement.api;

import java.sql.Time;

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
	public Time getStartTime();
	public void setStartTime(Time startTime);
	public Time getFinishTime();
	public void setFinishTime(Time finishTime);
	public String getNotes();
	public void setNotes(String notes);
	public boolean isFriday();
	public void setFriday(boolean friday);
	public boolean isMonday();
	public void setMonday(boolean monday);
	public boolean isSaturday();
	public void setSaturday(boolean saturday);
	public boolean isSunday();
	public void setSunday(boolean sunday);
	public boolean isThursday();
	public void setThursday(boolean thursday);
	public boolean isTuesday();
	public void setTuesday(boolean tuesday);
	public boolean isWednesday();
	public void setWednesday(boolean wednesday);
}
