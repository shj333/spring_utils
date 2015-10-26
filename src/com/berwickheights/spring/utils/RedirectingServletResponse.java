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
	
package com.berwickheights.spring.utils;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class RedirectingServletResponse extends HttpServletResponseWrapper {

    RedirectServletStream out;

    public RedirectingServletResponse(HttpServletResponse response, OutputStream out) {
        super(response);
        this.out = new RedirectServletStream(out);
    }

    public void flushBuffer() throws IOException {
    	out.flush();
    	super.flushBuffer();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(out);
    }



	public ServletResponse getResponse() {
		return super.getResponse();
	}

	public void setResponse(ServletResponse arg0) {
		super.setResponse(arg0);
	}



	private static class RedirectServletStream extends ServletOutputStream {
        OutputStream out;

        RedirectServletStream(OutputStream out) {
            this.out = out;
        }

        public void write(int param) throws java.io.IOException {
            out.write(param);
        }
    }

}
