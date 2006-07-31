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
 *      http://www.opensource.org/licenses/ecl1.txt
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 **********************************************************************************/
package org.sakaiproject.coursemanagement.api;

/**
 * Models "School" and "Department" as well as more ad hoc groupings.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface CourseSet {

	/**
	 * Gets the unique enterprise id of this MembershipContainer.
	 * @return
	 */
	public String getEid();

	/**
	 * What authority defines this object?
	 * @return 
	 */
	public String getAuthority();

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
	 * A category
	 * @return
	 */
	public String getCategory();

	/**
	 * Gets the parent CourseSet for this CourseSet, or null if this is a top-level CourseSet.
	 * 
	 * @return
	 */
	public CourseSet getParent();
}
