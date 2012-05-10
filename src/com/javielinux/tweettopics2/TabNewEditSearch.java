package com.javielinux.tweettopics2;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import database.EntitySearch;
import infos.InfoSaveTweets;
import task.SaveFirstTweetsAsyncTask;
import task.SaveFirstTweetsAsyncTask.SaveFirstTweetsAsyncTaskResponder;

public class TabNewEditSearch extends TabActivity implements SaveFirstTweetsAsyncTaskResponder
{
	private static final int SAVE_ID = Menu.FIRST;
	private static final int SAVE_LAUNCH_ID = Menu.FIRST+1;
	
	public static Context StaticContext = null;
	
	private long mCurrentId = -1;
	
	private EntitySearch ent;
	
	private boolean mViewSearch = false;
	
	private TabHost tabs;
	
	protected ProgressDialog progressDialog;
	
	private ThemeManager mThemeManager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
                
        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        StaticContext = this;
        
        String name = "";
        String search = "";
        String searchOR = "";
        String user = "";
        
        if (savedInstanceState != null) {
        	if (savedInstanceState.containsKey(DataFramework.KEY_ID)) mCurrentId = savedInstanceState.getLong(DataFramework.KEY_ID);
        	if (savedInstanceState.containsKey("name")) name = savedInstanceState.getString("name");
        	if (savedInstanceState.containsKey("search")) search = savedInstanceState.getString("search");
        	if (savedInstanceState.containsKey("search_or")) searchOR = savedInstanceState.getString("search_or");
        	if (savedInstanceState.containsKey("user")) user = savedInstanceState.getString("user");
       	} else {
       		Bundle extras = getIntent().getExtras();  
       		if (extras != null) {
       			if (extras.containsKey(DataFramework.KEY_ID)) mCurrentId = extras.getLong(DataFramework.KEY_ID);
       			if (extras.containsKey("name")) name = extras.getString("name");
       			if (extras.containsKey("search")) search = extras.getString("search");
       			if (extras.containsKey("search_or")) searchOR = extras.getString("search_or");
       			if (extras.containsKey("user")) user = extras.getString("user");
       		}
       	}
                
        mThemeManager = new ThemeManager(this);
        mThemeManager.setTheme();
        setContentView(R.layout.tab_newedit);
        
        ent = new EntitySearch(mCurrentId);

        setDefaultTab(0);

        tabs = getTabHost();

    	TabHost.TabSpec tabGeneral = tabs.newTabSpec("TabGeneral");

    	ComponentName generalActivity =	new ComponentName(Utils.packageName, Utils.packageName + ".TabGeneral");

    	Intent generalIntent = new Intent().setComponent(generalActivity);
    	generalIntent.putExtra(DataFramework.KEY_ID, mCurrentId);
    	generalIntent.putExtra("name", name);
    	generalIntent.putExtra("search", search);
    	generalIntent.putExtra("search_or", searchOR);
    	generalIntent.putExtra("user", user);
    	
    	tabGeneral.setContent(generalIntent);
    	tabGeneral.setIndicator(this.getString(R.string.tab_general), this.getResources().getDrawable(R.drawable.ic_tab_general));

    	tabs.addTab(tabGeneral);
    	
    	TabHost.TabSpec tabAvanced = tabs.newTabSpec("TabAvanced");

    	ComponentName advActivity = new ComponentName(Utils.packageName, Utils.packageName + ".TabAvanced");
    	
    	Intent advIntent = new Intent().setComponent(advActivity);
    	advIntent.putExtra(DataFramework.KEY_ID, mCurrentId);

    	tabAvanced.setContent(advIntent);
    	tabAvanced.setIndicator(this.getString(R.string.tab_avanced), this.getResources().getDrawable(R.drawable.ic_tab_avanced));

    	tabs.addTab(tabAvanced);
    	
    	TabHost.TabSpec tabGeo = tabs.newTabSpec("TabGeo");

    	ComponentName geoActivity = new ComponentName(Utils.packageName, Utils.packageName + ".TabGeo");
    	
    	Intent geoIntent = new Intent().setComponent(geoActivity);
    	geoIntent.putExtra(DataFramework.KEY_ID, mCurrentId);

    	tabGeo.setContent(geoIntent);
    	tabGeo.setIndicator(this.getString(R.string.tab_geo), this.getResources().getDrawable(R.drawable.ic_tab_geo));

    	tabs.addTab(tabGeo);
    	
    	// meter publicidad
    	
    	if (Utils.isLite(this) ) {
    	
    		LinearLayout pub = (LinearLayout) this.findViewById(R.id.pub);
    		
	    	LayoutInflater inflater = LayoutInflater.from(this);
	    	LinearLayout mLayoutAd = (LinearLayout) inflater.inflate(R.layout.ad, null, false);
	
	    	pub.addView(mLayoutAd);
	    	
			AdView adView = (AdView)mLayoutAd.findViewById(R.id.adView);
		    adView.loadAd(new AdRequest());
		    
    	}

    	/*
    	TabHost.TabSpec tabNotifications = tabs.newTabSpec("TabNotifications");

    	ComponentName notificationsActivity = new ComponentName(Utils.packageName, Utils.packageName + ".TabNotifications");
    	
    	Intent notificationsIntent = new Intent().setComponent(notificationsActivity);
    	notificationsIntent.putExtra(DataFramework.KEY_ID, mCurrentId);

    	tabNotifications.setContent(notificationsIntent);
    	tabNotifications.setIndicator(this.getString(R.string.tab_notifications));

    	tabs.addTab(tabNotifications);*/
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SAVE_ID, 0,  R.string.save)
			.setIcon(android.R.drawable.ic_menu_save);
        menu.add(0, SAVE_LAUNCH_ID, 0,  R.string.save_and_view)
			.setIcon(android.R.drawable.ic_menu_directions);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case SAVE_ID:
        	save(false);
            return true;
        case SAVE_LAUNCH_ID:
        	save(true);
            return true;
        }
       
        return super.onOptionsItemSelected(item);
	}
    
    private void save(boolean view) {
    	    	    	
    	mViewSearch = view;
    	
    	String error = "";
    	
    	int nTabGeneral = -1;
    	int nTabAdvanced = -1;
    	int nTabGeo = -1;
    	
    	boolean saveTweets = false; 
    	
    	for (int i=0; i<tabs.getTabContentView().getChildCount(); i++) {
    		EditText widgetGeneral = (EditText)tabs.getTabContentView().getChildAt(i).findViewById(R.id.et_name);
    		EditText widgetAdvanced = (EditText)tabs.getTabContentView().getChildAt(i).findViewById(R.id.et_source);
    		CheckBox widgetGeo = (CheckBox)tabs.getTabContentView().getChildAt(i).findViewById(R.id.cb_use_geo);
    		if (widgetGeneral!=null) nTabGeneral = i;
    		if (widgetAdvanced!=null) nTabAdvanced = i;
    		if (widgetGeo!=null) nTabGeo = i;
    	}
    	    	
    	if (nTabGeneral>=0) {
    		String name = "";  	
	    	
	    	EditText mSearchAND = (EditText)tabs.getTabContentView().getChildAt(nTabGeneral).findViewById(R.id.et_words_and);
			ent.setValue("words_and", mSearchAND.getText().toString());
			
			if (!mSearchAND.getText().toString().equals("")) if (name.length()<=0) name = mSearchAND.getText().toString();
			
			EditText mSearchOR = (EditText)tabs.getTabContentView().getChildAt(nTabGeneral).findViewById(R.id.et_words_or);
			ent.setValue("words_or", mSearchOR.getText().toString());
			
			if (!mSearchOR.getText().toString().equals("")) if (name.length()<=0) name = mSearchOR.getText().toString();
			
			EditText mSearchNOT = (EditText)tabs.getTabContentView().getChildAt(nTabGeneral).findViewById(R.id.et_words_not);
			ent.setValue("words_not", mSearchNOT.getText().toString());
			
			if (!mSearchNOT.getText().toString().equals("")) if (name.length()<=0) name = mSearchNOT.getText().toString();
			
			EditText mSearchFromUser = (EditText)tabs.getTabContentView().getChildAt(nTabGeneral).findViewById(R.id.et_from_user);
			ent.setValue("from_user", mSearchFromUser.getText().toString());
			
			if (!mSearchFromUser.getText().toString().equals("")) if (name.length()<=0) name = mSearchFromUser.getText().toString();
			
			EditText mSearchToUser = (EditText)tabs.getTabContentView().getChildAt(nTabGeneral).findViewById(R.id.et_to_user);
			ent.setValue("to_user", mSearchToUser.getText().toString());
			
			if (!mSearchToUser.getText().toString().equals("")) if (name.length()<=0) name = mSearchToUser.getText().toString();
				
			if (mSearchAND.getText().toString().equals("") && mSearchOR.getText().toString().equals("") 
					&& mSearchNOT.getText().toString().equals("") && mSearchFromUser.getText().toString().equals("")  
					&& mSearchToUser.getText().toString().equals("") ) {
				error = this.getString(R.string.error_search_text);
			}
	    	
	    	
	    	EditText mName = (EditText)tabs.getTabContentView().getChildAt(nTabGeneral).findViewById(R.id.et_name);
	    	if (mName.getText().toString().equals("")) {
	    		if (name.length()>1) {
	    			name = name.substring(0, 1).toUpperCase() + name.substring(1);
	    		}
	    		ent.setValue("name", name);
	    	} else {
	    		name = mName.getText().toString();
	    		ent.setValue("name", name);
	    	}
	    	
	    	EditText mEdIcon = (EditText)tabs.getTabContentView().getChildAt(nTabGeneral).findViewById(R.id.icon_id);
	    	long icon_id = Long.parseLong(mEdIcon.getText().toString());
	    	EditText mEdFile = (EditText)tabs.getTabContentView().getChildAt(nTabGeneral).findViewById(R.id.icon_file);
	    	String token_file = mEdFile.getText().toString();

	    	ent.setValue("icon_id", icon_id);
	    	ent.setValue("icon_token_file", token_file);
	    	
	    	if (icon_id>1) {
	    		Entity icon = new Entity("icons", icon_id);
	    		ent.setValue("icon_big", "drawable/"+icon.getValue("icon"));
	    		ent.setValue("icon_small", "drawable/"+icon.getValue("icon_small"));
	    	} else if (icon_id==1) {
	    		ent.setValue("icon_big", Utils.getIconGeneric(this, name));
	    		ent.setValue("icon_small", Utils.getIconGeneric(this, name)+"_small");
	    		/*String c = name.substring(0, 1).toLowerCase();
	    		int id = this.getResources().getIdentifier(Utils.packageName+":drawable/letter_"+c, null, null);
	    		if (id>0) {
	    			ent.setValue("icon_big", "drawable/letter_"+c);
		    		ent.setValue("icon_small", "drawable/letter_"+c+"_small");
	    		} else {
	    			ent.setValue("icon_big", "drawable/letter_az");
		    		ent.setValue("icon_small", "drawable/letter_az_small");
	    		}*/
	    	} else {
	    		ent.setValue("icon_big", "file/"+token_file+".png");
	    		ent.setValue("icon_small", "file/"+token_file+"_small.png");
	    	}
			
    	}
    	
    	if ( (nTabAdvanced>=0) && (error.length()==0) ) {
			
			Spinner mLanguages = (Spinner) tabs.getTabContentView().getChildAt(nTabAdvanced).findViewById(R.id.sp_languages);
			
			if (mLanguages!=null) {
				String[] res = TabNewEditSearch.this.getResources().getStringArray(R.array.languages_values);
				ent.setValue("lang", res[mLanguages.getSelectedItemPosition()]);
			}
			
			Spinner mAttitude = (Spinner) tabs.getTabContentView().getChildAt(nTabAdvanced).findViewById(R.id.sp_attitude);
			if (mAttitude!=null) ent.setValue("attitude", mAttitude.getSelectedItemPosition());
			
			Spinner mFilter = (Spinner) tabs.getTabContentView().getChildAt(nTabAdvanced).findViewById(R.id.sp_filter);
			if (mFilter!=null) ent.setValue("filter", mFilter.getSelectedItemPosition());
			
			CheckBox mNoRetweet = (CheckBox)tabs.getTabContentView().getChildAt(nTabAdvanced).findViewById(R.id.cb_no_retweet);

			if (mNoRetweet!=null) { 
		    	if (mNoRetweet.isChecked()) {
		    		ent.setValue("no_retweet", 1);
		    	} else{
		    		ent.setValue("no_retweet", 0);
		    	}
			}
			
			EditText mSearchSource = (EditText)tabs.getTabContentView().getChildAt(nTabAdvanced).findViewById(R.id.et_source);
			if (mSearchSource!=null) ent.setValue("source", mSearchSource.getText().toString());
			
    		CheckBox mNotifications = (CheckBox)tabs.getTabContentView().getChildAt(nTabAdvanced).findViewById(R.id.cb_notifications);
    		
    		// borrar todos los tweets en el caso que deje de notificarse la b�squeda
    		
    		if ((!mNotifications.isChecked()) && (ent.getInt("notifications")==1)) {
    			DataFramework.getInstance().getDB().execSQL("DELETE FROM tweets WHERE search_id = " + mCurrentId + " AND favorite = 0");
    			ent.setValue("last_tweet_id", "0");
    			ent.setValue("last_tweet_id_notifications", "0");
    			ent.setValue("new_tweets_count", "0");
    		}
    		
    		// guarda los primeros tweets en el caso de empezar a notificar
    		
    		if ((mNotifications.isChecked()) && (ent.getInt("notifications")==0)) {
    			saveTweets = true; 
    		}
    		
	    	if (mNotifications.isChecked()) {
	    		ent.setValue("notifications", 1);
	    	} else {
	    		ent.setValue("notifications", 0);
	    	}
	    	
	    	CheckBox mNotificationsBar = (CheckBox)tabs.getTabContentView().getChildAt(nTabAdvanced).findViewById(R.id.cb_notifications_bar);
	    	
	    	if (mNotificationsBar.isChecked()) {
	    		ent.setValue("notifications_bar", 1);
	    	} else {
	    		ent.setValue("notifications_bar", 0);
	    	}
			
    	}


    	if ( (nTabGeo>=0) && (error.length()==0) ) {
    		
	    	CheckBox mUseGeo = (CheckBox)tabs.getTabContentView().getChildAt(nTabGeo).findViewById(R.id.cb_use_geo);
	    	
	    	if (mUseGeo.isChecked()) {
	    		ent.setValue("use_geo", 1);
	    		
		    	RadioButton mTypeGeoGPS = (RadioButton)tabs.getTabContentView().getChildAt(nTabGeo).findViewById(R.id.rb_use_gps);
		    	if (mTypeGeoGPS.isChecked()) {
		    		ent.setValue("type_geo", 1);
		    	} else {
		    		ent.setValue("type_geo", 0);
		    				    		
			    	EditText mLatitude = (EditText)tabs.getTabContentView().getChildAt(nTabGeo).findViewById(R.id.et_latitude);
			    	EditText mLongitude = (EditText)tabs.getTabContentView().getChildAt(nTabGeo).findViewById(R.id.et_longitude);
			    	
			    	try {
			    		float latitude = Float.parseFloat(mLatitude.getText().toString());
			    		float longitude = Float.parseFloat(mLongitude.getText().toString());
			    		ent.setValue("latitude", latitude);
			    		ent.setValue("longitude", longitude);
			    	} catch (Exception e) {
			    		error = this.getString(R.string.error_search_coord);
			    	}
		    		
		    	}
		    	
		    	if (error.length()==0) {
			    	SeekBar mDistance = (SeekBar)tabs.getTabContentView().getChildAt(nTabGeo).findViewById(R.id.sb_distance);
			    	
			    	if (mDistance.getProgress()>0) {
				    	ent.setValue("distance", mDistance.getProgress());
				    	
				    	RadioButton mTypeDistance = (RadioButton)tabs.getTabContentView().getChildAt(nTabGeo).findViewById(R.id.rb_distance_km);
				    	if (mTypeDistance.isChecked()) {
				    		ent.setValue("type_distance", 1);
				    	} else {
				    		ent.setValue("type_distance", 0);
				    	}
			    	} else {
			    		error = this.getString(R.string.error_search_distance);
			    	}
		    	}
	    		
	    	} else {
	    		ent.setValue("use_geo", 0);
	    	}
	    	
    	}

    	
    	if (error.length()==0) {

    		if (saveTweets) {
    			saveTweets();
    		} else {
    			exit();
    		}
    		
    	} else {
    		Utils.showMessage(this, error);
    	}

    }
    
    private AsyncTask<Long, Void, InfoSaveTweets> saveTask;
    
    private void saveTweets() {
    	
    	progressDialog = ProgressDialog.show(
				this,
				getResources().getString(R.string.saved_tweet),
				getResources().getString(R.string.saved_tweet_description)
		);

		
		saveTask = new SaveFirstTweetsAsyncTask(this).execute(mCurrentId);
    	
    }

	@Override
	public void firstTweetCancelled() {
		if (saveTask!=null) saveTask.cancel(true);
	}

	@Override
	public void firstTweetLoaded(InfoSaveTweets info) {
		ent.setValue("last_tweet_id", info.getOlderId());
		ent.setValue("last_tweet_id_notifications", info.getNewerId());
		ent.setValue("new_tweets_count",info.getNewMessages());
		exit();
	}

	@Override
	public void firstTweetLoading() {
		
	}
	
    private void exit() {
    	if (ent.getId()<0) {
    		ent.setValue("date_create", Utils.now());
    		ent.setValue("last_modified", Utils.now());
        	ent.setValue("use_count", 0);	
    	}
    	ent.setValue("is_temp", 0);
    	ent.save();
    	
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
		intent.putExtra("view", mViewSearch);
		intent.putExtra(DataFramework.KEY_ID, ent.getId());
		setResult(RESULT_OK, intent);
		finish();
    }
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Utils.saveStatusWorkApp(this, true);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Utils.saveStatusWorkApp(this, false);
    }

}