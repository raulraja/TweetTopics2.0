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
