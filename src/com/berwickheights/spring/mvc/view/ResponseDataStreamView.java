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
	
package com.berwickheights.spring.mvc.view;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseDataStreamView implements org.springframework.web.servlet.View {
	private String contentType = "text/html; charset=utf-8";
	
	
	public static final String KEY = "result";
	

	@Override
	public String getContentType() {
		return contentType;
	}

	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void render(Map model, HttpServletRequest request, HttpServletResponse response) 
	throws Exception {
		response.setContentType(this.contentType);
		response.getWriter().write((String)model.get(KEY));
	}
}
