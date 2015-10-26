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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;



/**
 * Service that provides data access services to the Spring Security
 * authentication mechanism. Returns user details for a given user name.
 * 
 * @author stuart
 * 
 */
public abstract class UserDetailSvcBase implements UserDetailsService, InitializingBean {
	private UserRolesSvc userRolesSvc;
	private final Log logger = LogFactory.getLog(this.getClass());

	
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(userRolesSvc, "userRolesSvc must be set");
	}

	/**
	 * Service used to map user roles from database to user roles used in Spring Security.
	 */
	public void setUserRolesSvc(UserRolesSvc userRolesSvc) {
		this.userRolesSvc = userRolesSvc;
	}
	
	

	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		// Find user record in database
		UserRecord userRec = findUserRecord(userName);
		if (userRec == null) {
			return null;
		}
		
		// Map user type to user roles used in Spring Security
		GrantedAuthority[] authorities = userRolesSvc.getUserRoles(userRec.getRoles());
		
		// Set up authentication info to be used by Spring Security
		UserDetails user = getUserDetails(userRec, authorities);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Returning user: " + user);
		}

		return user;
	}

	protected abstract UserRecord findUserRecord(String userName);
	
	protected abstract UserDetails getUserDetails(UserRecord userRec, GrantedAuthority[] authorities);
}
