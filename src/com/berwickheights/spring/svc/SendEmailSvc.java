/*
 * Copyright 2009 Berwick Heights Software, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is 
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License.
 *  
 */
	
package com.berwickheights.spring.svc;


import java.io.File;

public interface SendEmailSvc {
	public void sendEmail(String emailAddr, String subject, String msgText,
			String attachmentFileName, File attachment);
	public void sendEmail(String emailAddr, String subject, String msgText,
			String attachmentFileName, File attachment, boolean runInBackground);
	public void sendEmail(String[] emailAddrs, String subject, String msgText,
			String attachmentFileName, File attachment);
	public void sendEmail(String[] emailAddrs, String subject, String msgText,
			String attachmentFileName, File attachment, boolean runInBackground);
}
