package com.javielinux.preferences;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Window;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class IntegrationADW extends ListActivity {
	
	private static SharedPreferences mPreferences = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.pref_adw);
        
        setTitle(R.string.title_prf_adw_launcher);
        
        createPreferences(this);
        
        verifyPreferences();
        
        refresh();
        
    }
    
    private void refresh () {
        ArrayList<InfoADWIntegration> statii = new ArrayList<InfoADWIntegration>();
        
        statii.add(new InfoADWIntegration(getPreference(1)));
        statii.add(new InfoADWIntegration(getPreference(2)));
        statii.add(new InfoADWIntegration(getPreference(3)));
        statii.add(new InfoADWIntegration(getPreference(4)));
        
        IntegrationADWAdapter adapter = new IntegrationADWAdapter(this, statii);
        
        this.setListAdapter(adapter);
        
    }
    
    public void color(int position, String color) {
    	String pref = getPreference(position);
        Editor editor = mPreferences.edit();
    	editor.putString(IntegrationADWAdapter.getPreferenceColor(pref), color);
        editor.commit();
        refresh();
    }
    
    public void up(int position) {
    	String prefUp = getPreference(position-1);
    	String prefDown = getPreference(position);
    	
        Editor editor = mPreferences.edit();
    	editor.putInt(prefUp, position);
    	editor.putInt(prefDown, position-1);
        editor.commit();
        refresh();
    }
    
    public void down(int position) {
    	String prefUp = getPreference(position);
    	String prefDown = getPreference(position+1);
    	
        Editor editor = mPreferences.edit();
    	editor.putInt(prefUp, position+1);
    	editor.putInt(prefDown, position);
        editor.commit();	
        refresh();
    }
    
    public static void createPreferences(Context cnt) {
    	mPreferences = cnt.getSharedPreferences(Utils.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
    }
    
    public static void verifyPreferences() {
    	
        Editor editor = mPreferences.edit();
        if (!mPreferences.contains(IntegrationADWAdapter.PREFERENCES_SEARCH)) {
        	editor.putInt(IntegrationADWAdapter.PREFERENCES_SEARCH, 1);
        	editor.putString(IntegrationADWAdapter.getPreferenceColor(IntegrationADWAdapter.PREFERENCES_SEARCH), "#FFFF0000");
        	
        	editor.putInt(IntegrationADWAdapter.PREFERENCES_DIRECTS, 2);
        	editor.putString(IntegrationADWAdapter.getPreferenceColor(IntegrationADWAdapter.PREFERENCES_DIRECTS), "#FFf0ff00");
        	
        	editor.putInt(IntegrationADWAdapter.PREFERENCES_MENTIONS, 3);
        	editor.putString(IntegrationADWAdapter.getPreferenceColor(IntegrationADWAdapter.PREFERENCES_MENTIONS), "#FF0000ff");
        	
        	editor.putInt(IntegrationADWAdapter.PREFERENCES_TIMELINE, 4);
        	editor.putString(IntegrationADWAdapter.getPreferenceColor(IntegrationADWAdapter.PREFERENCES_TIMELINE), "#FF00ff00");
        	
            editor.commit();	
        }
    }
    
    public static String getPreference(int pos) {
    	if ( mPreferences.getInt(IntegrationADWAdapter.PREFERENCES_SEARCH, 0) == pos ) {
    		return IntegrationADWAdapter.PREFERENCES_SEARCH;
    	}
    	if ( mPreferences.getInt(IntegrationADWAdapter.PREFERENCES_TIMELINE, 0) == pos ) {
    		return IntegrationADWAdapter.PREFERENCES_TIMELINE;
    	}
    	if ( mPreferences.getInt(IntegrationADWAdapter.PREFERENCES_MENTIONS, 0) == pos ) {
    		return IntegrationADWAdapter.PREFERENCES_MENTIONS;
    	}
    	if ( mPreferences.getInt(IntegrationADWAdapter.PREFERENCES_DIRECTS, 0) == pos ) {
    		return IntegrationADWAdapter.PREFERENCES_DIRECTS;
    	}
    	return "";
    }
    
    public static int getPosition(String pref) {
    	if (IntegrationADWAdapter.PREFERENCES_SEARCH.equals(pref)) {
    		return mPreferences.getInt(IntegrationADWAdapter.PREFERENCES_SEARCH, 0);
    	}
    	if (IntegrationADWAdapter.PREFERENCES_TIMELINE.equals(pref)) {
    		return mPreferences.getInt(IntegrationADWAdapter.PREFERENCES_TIMELINE, 0);
    	}
    	if (IntegrationADWAdapter.PREFERENCES_MENTIONS.equals(pref)) {
    		return mPreferences.getInt(IntegrationADWAdapter.PREFERENCES_MENTIONS, 0);
    	}
    	if (IntegrationADWAdapter.PREFERENCES_DIRECTS.equals(pref)) {
    		return mPreferences.getInt(IntegrationADWAdapter.PREFERENCES_DIRECTS, 0);
    	}
    	return 0;
    }
    
    public static String getColor(String pref) {
    	if (IntegrationADWAdapter.PREFERENCES_SEARCH.equals(pref)) {
    		return mPreferences.getString(IntegrationADWAdapter.getPreferenceColor(IntegrationADWAdapter.PREFERENCES_SEARCH), "#FF000000");
    	}
    	if (IntegrationADWAdapter.PREFERENCES_TIMELINE.equals(pref)) {
    		return mPreferences.getString(IntegrationADWAdapter.getPreferenceColor(IntegrationADWAdapter.PREFERENCES_TIMELINE), "#FF000000");
    	}
    	if (IntegrationADWAdapter.PREFERENCES_MENTIONS.equals(pref)) {
    		return mPreferences.getString(IntegrationADWAdapter.getPreferenceColor(IntegrationADWAdapter.PREFERENCES_MENTIONS), "#FF000000");
    	}
    	if (IntegrationADWAdapter.PREFERENCES_DIRECTS.equals(pref)) {
    		return mPreferences.getString(IntegrationADWAdapter.getPreferenceColor(IntegrationADWAdapter.PREFERENCES_DIRECTS), "#FF000000");
    	}
    	return "#FF000000";
    }
    
}