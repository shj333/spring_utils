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


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.util.Assert;


/**
 * Maps the given number (assumed from some data source) into a Spring Security role
 * according to configuration (rolesMap).
 * 
 * @author stuart
 *
 */
public class UserRolesSvcImpl implements UserRolesSvc, InitializingBean {
	private Map<Short, Set<GrantedAuthority>> rolesMap;
	private final Log logger = LogFactory.getLog(this.getClass());
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(rolesMap, "rolesMap must be set");
	}
	
	
	
	@Override
	public GrantedAuthority[] getUserRoles(Set<Short> userRoles) {
		Set<GrantedAuthority> auths = new TreeSet<GrantedAuthority>(); 
		for (Short userRole : userRoles) {
			Set<GrantedAuthority> authsForRole = rolesMap.get(userRole);
			if (authsForRole == null) {
				logger.warn("No authorities for given userRole: " + userRole);
			}
			else {
				auths.addAll(authsForRole);
			}
		}
		
		return auths.toArray(new GrantedAuthority[auths.size()]);
	}



	/**
	 * The map of user types stored in database to user roles used in Spring Security.
	 */
	public void setRolesMap(Map<Short, String> rolesMap) {
		this.rolesMap = new HashMap<Short, Set<GrantedAuthority>>();
		for (Map.Entry<Short, String> entry : rolesMap.entrySet()) {
			String[] rolesArray = StringUtils.split(entry.getValue(), ",");
			Set<GrantedAuthority> roles = new TreeSet<GrantedAuthority>();
			for (String role : rolesArray) {
				roles.add(new GrantedAuthorityImpl(role));
			}
			
			this.rolesMap.put(entry.getKey(), roles);
		}
	}
}
