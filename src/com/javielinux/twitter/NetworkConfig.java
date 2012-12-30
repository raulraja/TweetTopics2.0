/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * 
 * Original code by aknoxx
 * 
 * https://github.com/aknoxx/StatusNetAndroid
 * 
 */

package com.javielinux.twitter;

public class NetworkConfig {

	private String name;
	private String consumerKey;
	private String consumerSecret;
	private String accessTokenURL;
	private String authorizationURL;
	private String requestTokenURL;
	private String restBaseURL;
	private String searchBaseURL;
	private String authenticationURL;
	
	public NetworkConfig copy() {
		NetworkConfig nc = new NetworkConfig();
		nc.name = this.name;
		nc.consumerKey = this.consumerKey;
		nc.accessTokenURL = this.accessTokenURL;
		nc.authorizationURL = this.authorizationURL;
		nc.requestTokenURL = this.requestTokenURL;
		nc.restBaseURL = this.restBaseURL;
		nc.searchBaseURL = this.searchBaseURL;
		nc.authenticationURL = this.authenticationURL;
		
		return nc;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getConsumerKey() {
		return consumerKey;
	}
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	public String getConsumerSecret() {
		return consumerSecret;
	}
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	public String getAccessTokenURL() {
		return accessTokenURL;
	}
	public void setAccessTokenURL(String accessTokenURL) {
		this.accessTokenURL = accessTokenURL;
	}
	public String getAuthorizationURL() {
		return authorizationURL;
	}
	public void setAuthorizationURL(String authorizationURL) {
		this.authorizationURL = authorizationURL;
	}
	public String getRequestTokenURL() {
		return requestTokenURL;
	}
	public void setRequestTokenURL(String requestTokenURL) {
		this.requestTokenURL = requestTokenURL;
	}
	public void setRestBaseURL(String restBaseURL) {
		this.restBaseURL = restBaseURL;
	}

	public String getRestBaseURL() {
		return restBaseURL;
	}

	public void setSearchBaseURL(String searchBaseURL) {
		this.searchBaseURL = searchBaseURL;
	}

	public String getSearchBaseURL() {
		return searchBaseURL;
	}

	public void setAuthenticationURL(String authenticationURL) {
		this.authenticationURL = authenticationURL;
	}

	public String getAuthenticationURL() {
		return authenticationURL;
	}
}