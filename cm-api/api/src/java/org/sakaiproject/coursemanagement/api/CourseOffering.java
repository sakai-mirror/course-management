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
package org.sakaiproject.coursemanagement.api;

import java.util.Date;

/**
 * An instance of a course.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface CourseOffering {

	/**
	 * Gets the unique enterprise id of this MembershipContainer.
	 * @return
	 */
	public String getEid();

	/**
	 * Gets the title of this MembershipContainer.
	 * @return
	 */
	public String getTitle();
	
	/**
	 * Gets the description of this MembershipContainer.
	 * @return
	 */
	public String getDescription();

	/**
	 * The AcademicSession for this course
	 * @return
	 */
	public AcademicSession getAcademicSession();

	/**
	 * The date this CourseOffering starts (if any).  Typically, a CourseOffering
	 * starts when its AcademicSession starts.  Since this isn't necessarily true
	 * for every CourseOffering, the startDate can be set explicitly here.
	 * 
	 * @return
	 */
	public Date getStartDate();
	
	/**
	 * The date this CourseOffering ends (if any).  Typically, a CourseOffering
	 * ends when its AcademicSession ends.  Since this isn't necessarily true
	 * for every CourseOffering, the endDate can be set explicitly here.
	 * 
	 * @return
	 */
	public Date getEndDate();	
}