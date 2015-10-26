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
	
package com.berwickheights.spring.svc.security;


import java.util.Set;

import org.springframework.security.GrantedAuthority;

/**
 * Maps user roles stored in the database to Spring Security roles used in authorizing
 * which parts of the web application the user is allowed to view.
 *   
 * @author stuart
 *
 */
public interface UserRolesSvc {
	/**
	 * Returns the Spring Security user roles for the given user roles stored in database. 
	 * The roles are Spring Security roles as an array of <code>GrantedAuthority<code>'s.
	 *  
	 * @param userRoles The list of user roles stored in database; used to map to 
	 * Spring Security roles. 
	 */
	public GrantedAuthority[] getUserRoles(Set<Short> userRoles);
}
