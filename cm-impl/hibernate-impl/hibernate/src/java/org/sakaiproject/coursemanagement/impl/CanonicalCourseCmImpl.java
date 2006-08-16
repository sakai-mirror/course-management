/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
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
