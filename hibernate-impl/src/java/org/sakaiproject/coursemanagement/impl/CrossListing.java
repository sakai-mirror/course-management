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

import java.util.Set;

/**
 * Models a cross listing between two CrossListable entities.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 */
public class CrossListing {
	/**
	 * The DB's primary key for this object / record.
	 */
	private long key;

	/**
	 * The object instance version for optimistic locking.
	 */
	private int version;

	/**
	 * The set of entities that are associated together in this CrossListing
	 */
	private Set crossListables;
	
	/**
	 * Whether this CrossListing is defined by the enterprise
	 */
	private boolean enterpriseManaged;

	
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public Set getCrossListables() {
		return crossListables;
	}
	public void setCrossListables(Set crossListables) {
		this.crossListables = crossListables;
	}
	public boolean isEnterpriseManaged() {
		return enterpriseManaged;
	}
	public void setEnterpriseManaged(boolean enterpriseManaged) {
		this.enterpriseManaged = enterpriseManaged;
	}
}
