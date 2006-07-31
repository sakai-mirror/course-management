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
package org.sakaiproject.coursemanagement.impl;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;

public class CanonicalCourseCmImpl extends CrossListableCmImpl
	implements CanonicalCourse, Serializable {
	
	private static final long serialVersionUID = 1L;

	private CrossListingCmImpl crossListingCmImpl;
	private Set courseSets;

	public CanonicalCourseCmImpl() {}
	public CanonicalCourseCmImpl(String eid, String title, String description) {
		this.eid = eid;
		this.title = title;
		this.description = description;
	}
	
	public Set getCourseSets() {
		return courseSets;
	}
	public void setCourseSets(Set courseSets) {
		this.courseSets = courseSets;
	}
	
	public CrossListingCmImpl getCrossListing() {
		return crossListingCmImpl;
	}
	public void setCrossListing(CrossListingCmImpl crossListingCmImpl) {
		this.crossListingCmImpl = crossListingCmImpl;
	}
	
	public boolean equals(Object o) {
		CanonicalCourse other = (CanonicalCourse)o;
		return new EqualsBuilder().append(this.eid, other.getEid()).isEquals();
	}
	
	public int hashCode() {
		return new HashCodeBuilder().append(eid).toHashCode();
	}
}
