package com.javielinux.tweettopics2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.flurry.android.FlurryAgent;
import error_reporter.ErrorReporter;

public class TweetTopics extends BaseActivity {
			
	private TweetTopicsCore mTweetTopicsOrientation;
	
	private SharedPreferences mPreference;

    @Override
    protected Dialog onCreateDialog(int id) {
    	return mTweetTopicsOrientation.onCreateDialog(id);
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utils.getFinishForceClose(this)){
            Utils.setFinishForceClose(this, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_crash);
            builder.setMessage(R.string.msg_crash);
            builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Utils.sendLastCrash(TweetTopics.this);
                }
            });
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        }

        Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter(currentHandler, getApplication()));
        }

    	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    	mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        

        mTweetTopicsOrientation = new TweetTopicsPortrait(this);

        
        mTweetTopicsOrientation.onCreate(savedInstanceState);

        int access_count = Utils.getApplicationAccessCount(this);

        if (access_count <= 20) {
            if (access_count == 20) {
                try {
                    AlertDialog dialog = Utils.RateAppDialogBuilder.create(TweetTopics.this);
                    dialog.show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            Utils.setApplicationAccessCount(this, access_count + 1);
        }
		
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mTweetTopicsOrientation.onCreateOptionsMenu(menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (mTweetTopicsOrientation.onMenuItemSelected(featureId, item)) {
        	return true;
        } else {       
        	return super.onMenuItemSelected(featureId, item);
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mTweetTopicsOrientation.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mTweetTopicsOrientation.onPause();
    }
    
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mTweetTopicsOrientation.onActivityResult(requestCode, resultCode, intent);
                
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTweetTopicsOrientation.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mTweetTopicsOrientation.onKeyDown(keyCode, event)) {
        	return super.onKeyDown(keyCode, event);        	
        } else {
        	return false;
        }        
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	return mTweetTopicsOrientation.onRetainNonConfigurationInstance();
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mTweetTopicsOrientation.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}


	public SharedPreferences getPreference() {
		return mPreference;
	}
	
	public int getIntPreference(String key, int defValue) {
		return Integer.parseInt(mPreference.getString(key, ""+defValue));
	}	
	
	public String getStringPreference(String key, String defValue) {
		return mPreference.getString(key, defValue);
	}
	
	public boolean getBooleanPreference(String key, boolean defValue) {
		return mPreference.getBoolean(key, defValue);
	}
	
	public Entity getActiveUser() {
		Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
    	if (e!=null) {
    		return e;
    	}
    	return null;
	}

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "YD8WID25ZW6JHC2ZI89P");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);

    }
}