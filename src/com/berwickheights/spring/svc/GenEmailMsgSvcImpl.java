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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.berwickheights.spring.utils.RedirectingServletResponse;



public class GenEmailMsgSvcImpl implements GenEmailMsgSvc {
	private final Log logger = LogFactory.getLog(this.getClass());


	public String genEmailMsgFromJSP(HttpServletRequest request, HttpServletResponse response,
			String jspPagePath, Map<String, Object> extraPageArgs)
	throws IOException, ServletException {
		Map<String, Object> args = new HashMap<String, Object>();

		// Add extra arguments if provided
		if (extraPageArgs != null) {
			for (String key : extraPageArgs.keySet()) {
				args.put(key, extraPageArgs.get(key));
			}
		}

		// Set up data for jsp/jstl template
		request.setAttribute("args", args);

		// Create an output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Create the "dummy" response object
		RedirectingServletResponse dummyResponse;
		dummyResponse = new RedirectingServletResponse(response, baos);

		// Get a request dispatcher for the email msg body to load
		RequestDispatcher rd = request.getRequestDispatcher(jspPagePath);

		// Execute JSP
		rd.include(request, dummyResponse);

		//
		// Get email message body
		// NB: JSP page must have "out.flush()" at end of file to flush Tomcat buffers
		// so that ByteArrayOutputStream (baos) will contain the mail message text
		//
		dummyResponse.flushBuffer();
		String emailText = baos.toString();
		baos.close();

		if (logger.isDebugEnabled()) {
			logger.debug("Generated message: " + emailText);
		}

		return emailText;
	}
}
