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

package com.javielinux.preferences;

import com.javielinux.tweettopics2.R;


public class InfoADWIntegration {
		
	private String color = "";
	private String type = "";
	private int resTitle = 0;
	private int resDescription = 0;
	
	public InfoADWIntegration(String type) {
		this.type = type;
		this.color = IntegrationADW.getColor(type);
		if (type.equals(IntegrationADWAdapter.PREFERENCES_SEARCH)) {
			resTitle = R.string.adw_integration_search_title;
			resDescription = R.string.adw_integration_search_desc;
		}
		if (type.equals(IntegrationADWAdapter.PREFERENCES_TIMELINE)) {
			resTitle = R.string.adw_integration_timeline_title;
			resDescription = R.string.adw_integration_timeline_desc;
		}
		if (type.equals(IntegrationADWAdapter.PREFERENCES_MENTIONS)) {
			resTitle = R.string.adw_integration_mentions_title;
			resDescription = R.string.adw_integration_mentions_desc;
		}
		if (type.equals(IntegrationADWAdapter.PREFERENCES_DIRECTS)) {
			resTitle = R.string.adw_integration_directs_title;
			resDescription = R.string.adw_integration_directs_desc;
		}
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public int getResTitle() {
		return resTitle;
	}

	public int getResDescription() {
		return resDescription;
	}

	public String getType() {
		return type;
	}

	public int getPosition() {
		return IntegrationADW.getPosition(type);
	}
	
}
