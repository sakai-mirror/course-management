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

import org.sakaiproject.coursemanagement.api.Membership;

public class MembershipCmImpl extends AbstractPersistentCourseManagementObjectCmImpl
	implements Membership, Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String role;
	private AbstractMembershipContainerCmImpl memberContainer;
	
	public MembershipCmImpl() {}
	
	public MembershipCmImpl(String userId, String role, AbstractMembershipContainerCmImpl memberContainer) {
		this.userId = userId;
		this.role = role;
		this.memberContainer = memberContainer;
	}
	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public AbstractMembershipContainerCmImpl getMemberContainer() {
		return memberContainer;
	}

	public void setMemberContainer(AbstractMembershipContainerCmImpl memberContainer) {
		this.memberContainer = memberContainer;
	}
}
