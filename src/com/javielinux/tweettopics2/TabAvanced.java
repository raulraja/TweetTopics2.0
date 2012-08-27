package com.javielinux.tweettopics2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.utils.DialogUtils.PersonalDialogBuilder;
import com.javielinux.utils.Utils;

import java.util.Locale;

public class TabAvanced extends Activity {
	private long mCurrentId = -1;
	
	private Spinner mLanguages;
	private Spinner mAttitude;
	private Spinner mFilter;
	private CheckBox mNoRetweet;
	private EditText mSource;
	private CheckBox mNotifications;
	private CheckBox mNotificationsBar;
	private LinearLayout mLLNotifications;
	private Button mBtInfoNotifications;
	
	private boolean searchIsNotification = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        if (savedInstanceState != null) {
        	if (savedInstanceState.containsKey(DataFramework.KEY_ID)) mCurrentId = savedInstanceState.getLong(DataFramework.KEY_ID);
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey(DataFramework.KEY_ID)) mCurrentId = extras.getLong(DataFramework.KEY_ID);
       		}
       	}
        
        setContentView(R.layout.tab_newedit_avanced);
        
        mBtInfoNotifications = (Button) findViewById(R.id.bt_info_notifications); 
        
        mBtInfoNotifications.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String file = "notifications_use.txt"; 
				if (Locale.getDefault().getLanguage().equals("es")) {
					file = "notifications_use_es.txt";
				}
				try {
					AlertDialog builder = PersonalDialogBuilder.create(TabAvanced.this, TabAvanced.this.getString(R.string.notifications),file);
					builder.show();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}			
			}
        	
        });
        
        mLanguages = (Spinner) findViewById(R.id.sp_languages);
        mAttitude = (Spinner) findViewById(R.id.sp_attitude);
        mFilter = (Spinner) findViewById(R.id.sp_filter);
        mNoRetweet = (CheckBox) findViewById(R.id.cb_no_retweet);
        mSource = (EditText) findViewById(R.id.et_source);
        
                
        ArrayAdapter<?> adapterLanguages = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLanguages.setAdapter(adapterLanguages);    
        
        ArrayAdapter<?> adapterAttitude = ArrayAdapter.createFromResource(this, R.array.attitude, android.R.layout.simple_spinner_item);
        adapterAttitude.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAttitude.setAdapter(adapterAttitude);   
        
        ArrayAdapter<?> adapterFilter = ArrayAdapter.createFromResource(this, R.array.filter, android.R.layout.simple_spinner_item);
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFilter.setAdapter(adapterFilter);   
        
        
        mNotifications = (CheckBox) findViewById(R.id.cb_notifications);
        mNotificationsBar = (CheckBox) findViewById(R.id.cb_notifications_bar);
        
        mLLNotifications = (LinearLayout) findViewById(R.id.ll_notifications);

        
        mNotifications.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				if (isChecked) {
					
					if (searchIsNotification) {
						showFields();
					} else {
						int max = Utils.MAX_NOTIFICATIONS;
						if (Utils.isLite(TabAvanced.this) ) {
							max = Utils.MAX_NOTIFICATIONS_LITE;
						}
						int s = DataFramework.getInstance().getEntityList("search", "notifications=1").size()+1;
						if (s<=max) {
							showFields();
						} else {
							mNotifications.setChecked(false);
							if (Utils.isLite(TabAvanced.this) ) {
								Utils.showMessage(TabAvanced.this, TabAvanced.this.getString(R.string.max_notifications_lite));
							} else {
								Utils.showMessage(TabAvanced.this, TabAvanced.this.getString(R.string.max_notifications));
							}
						}
					}
				} else {
					hideFields();
				}
			}

        	
        });      
        
        hideFields();
        
        populateFields();
        
    }
    
    private void showFields() {
    	mLLNotifications.setVisibility(View.VISIBLE);
    }
    
    private void hideFields() {
    	mLLNotifications.setVisibility(View.GONE);
    }
    
    private void populateFields() {
    	if (mCurrentId != -1) {
    		Entity ent = new Entity("search", mCurrentId);
    		if (!ent.getString("lang").equals("")) {
    			String[] res = this.getResources().getStringArray(R.array.languages_values);
    			for (int i=0; i<res.length; i++) {
    				if (res[i].equals(ent.getString("lang"))) {
    					mLanguages.setSelection(i);
    				}
    			}
    		}
    		
    		mAttitude.setSelection(ent.getInt("attitude"));
    		mFilter.setSelection(ent.getInt("filter"));
    		
    		if (ent.getInt("no_retweet")==1) {
    			mNoRetweet.setChecked(true);
    		}
    		
    		mSource.setText(ent.getString("source"));
    		
    		if (ent.getInt("com/javielinux/notifications")==1) {
    			searchIsNotification = true;
    			mNotifications.setChecked(true);
    			showFields();
    		}
    		
    		if (ent.getInt("notifications_bar")==1) {
    			mNotificationsBar.setChecked(true);
    		}
    		
    	}
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
	
}
