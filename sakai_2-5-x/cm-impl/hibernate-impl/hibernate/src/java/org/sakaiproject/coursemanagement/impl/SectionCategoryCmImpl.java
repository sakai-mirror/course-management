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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.sakaiproject.coursemanagement.api.SectionCategory;

public class SectionCategoryCmImpl implements SectionCategory {
	protected String categoryCode;
	protected String categoryDescription;
	
	public SectionCategoryCmImpl() {}
	
	public SectionCategoryCmImpl(String categoryCode, String categoryDescription) {
		this.categoryCode = categoryCode;
		this.categoryDescription = categoryDescription;
	}

	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCategoryDescription() {
		return categoryDescription;
	}
	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(categoryCode).append(categoryDescription).toHashCode();
	}

	public boolean equals(Object obj) {
		SectionCategoryCmImpl other = null;
		try {
			other = (SectionCategoryCmImpl)obj;
		} catch (ClassCastException cce) {
			return false;
		}
		
		return new EqualsBuilder().append(categoryCode, other.getCategoryCode()).
			append(categoryDescription, other.getCategoryDescription()).isEquals();
	}
	
}
