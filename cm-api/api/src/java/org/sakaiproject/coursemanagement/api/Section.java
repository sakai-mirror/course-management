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

/**
 * Models a "cohort" (a stable group which enrolls in multiple courses as a unit)
 * as well as officially delimited course "groups" and "sections".
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface Section {

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
	 * A category
	 * @return
	 */
	public String getCategory();
	
	/**
	 * Gets the parent Section for this Section, or null if this is not a subSection.
	 * @return
	 */
	public Section getParent();

	/**
	 * Gets the EnrollmentSet associated with this Section, if any.
	 * @return
	 */
	public EnrollmentSet getEnrollmentSet();
	
}
