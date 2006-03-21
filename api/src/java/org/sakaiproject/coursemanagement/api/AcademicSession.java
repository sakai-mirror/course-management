/**********************************************************************************
 * $$URL$$
 * $$Id$$
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
 * An institutional context for CourseOfferings, distinguishing one instance of
 * a course from another.  In higher educational institutions, it almost always
 * includes a time range. However, self-paced "sessions" are apparently also
 * possible.
 * 
 * AcademicSession includes a notion of ordering and currency to support queries
 * such as "Find all current course offerings" and "Sort past course offerings
 * in reverse session order".
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public interface AcademicSession {
	/**
	 * A unique id
	 * @return
	 */
	public String getId();

	/**
	 * The title
	 * @return
	 */
	public String getTitle();
	
	/**
	 * A description
	 * @return
	 */
	public String getDescription();
}
