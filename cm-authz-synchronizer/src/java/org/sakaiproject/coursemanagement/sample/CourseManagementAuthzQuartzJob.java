/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
* Copyright (c) 2008 The Regents of the University of California
*
* Licensed under the Educational Community License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.osedu.org/licenses/ECL-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**********************************************************************************/

package org.sakaiproject.coursemanagement.sample;

import java.util.regex.Pattern;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 */
public class CourseManagementAuthzQuartzJob implements Job {
	protected static Pattern termEidPattern = Pattern.compile(".*(?i)term=");
	protected CourseManagementAuthzSynchronizer synchronizer;

	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobName = context.getJobDetail().getName();
		if (jobName != null) {
			String[] splitJobName = termEidPattern.split(jobName);
			if (splitJobName.length == 2) {
				synchronizer.setTermEid(splitJobName[1]);
			}
		}
		synchronizer.execute();
	}

	public void setSynchronizer(CourseManagementAuthzSynchronizer synchronizer) {
		this.synchronizer = synchronizer;
	}

}
