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

import android.content.res.XmlResourceParser;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class NetworkConfigParser {

    public List<NetworkConfig> parse(XmlResourceParser xmlResourceParser) {
        List<NetworkConfig> configs = null;
        
        try {
            int eventType = xmlResourceParser.getEventType();
            NetworkConfig currentConfig = null;
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT && !done){
                String name = null;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                    	configs = new ArrayList<NetworkConfig>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = xmlResourceParser.getName();
                        if (name.equalsIgnoreCase("network")){
                            currentConfig = new NetworkConfig();
                        } else if (currentConfig != null){
                            if (name.equalsIgnoreCase("Name")){
                                currentConfig.setName(xmlResourceParser.nextText());
                            } else if (name.equalsIgnoreCase("ConsumerKey")){
                                currentConfig.setConsumerKey(xmlResourceParser.nextText());
	                        } else if (name.equalsIgnoreCase("ConsumerSecret")){
	                            currentConfig.setConsumerSecret(xmlResourceParser.nextText());
			                } else if (name.equalsIgnoreCase("AccessTokenURL")){
			                    currentConfig.setAccessTokenURL(xmlResourceParser.nextText());
			                } else if (name.equalsIgnoreCase("AuthorizationURL")){
			                    currentConfig.setAuthorizationURL(xmlResourceParser.nextText());
			                } else if (name.equalsIgnoreCase("RequestTokenURL")){
			                    currentConfig.setRequestTokenURL(xmlResourceParser.nextText());
			                } else if (name.equalsIgnoreCase("RestBaseURL")){
			                    currentConfig.setRestBaseURL(xmlResourceParser.nextText());
			                } else if (name.equalsIgnoreCase("SearchBaseURL")){
			                    currentConfig.setSearchBaseURL(xmlResourceParser.nextText());
			                } else if (name.equalsIgnoreCase("AuthenticationURL")){
			                    currentConfig.setAuthenticationURL(xmlResourceParser.nextText());
			                }						                
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = xmlResourceParser.getName();
                        if (name.equalsIgnoreCase("network") && currentConfig != null){
                        	configs.add(currentConfig);
                        } else if (name.equalsIgnoreCase("networks")){
                            done = true;
                        }
                        break;
                }
                eventType = xmlResourceParser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return configs;
    }
}
