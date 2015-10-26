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
	
package com.berwickheights.spring.mvc.controller;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ErrorPageController extends AbstractController implements InitializingBean {

	private String viewName;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(viewName, "viewName must be set");
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		
		Throwable t = (Throwable)request.getAttribute("javax.servlet.error.exception");
		if (t != null) {
			LogFactory.getLog("ErrorPage").error("Exception caught by error page", t);
		} else if ("500".equals(request.getAttribute("javax.servlet.error.status_code"))) {
			LogFactory.getLog("ErrorPage").error(
					"An internal error (500) has occurred but no Exception object was found in request");
		}
		
		if (request.getAttribute("javax.servlet.error.status_code") != null) {
			args.put("statusCode", request.getAttribute("javax.servlet.error.status_code"));
		}
		else {
			args.put("statusCode", request.getParameter("err"));
		}
		return new ModelAndView(getViewName(), "args", args);
	}
}
