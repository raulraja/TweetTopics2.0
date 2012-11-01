package com.javielinux.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.database.EntitySearch;
import com.javielinux.database.EntityTweetUser;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.notifications.AlarmAsyncTask.AlarmAsyncTaskResponder;
import com.javielinux.preferences.IntegrationADW;
import com.javielinux.preferences.IntegrationADWAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.NotificationUtils;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import com.javielinux.widget.WidgetCounters2x1;
import com.javielinux.widget.WidgetCounters4x1;
import twitter4j.Twitter;

import java.util.List;

public class AlarmOneNotificationAsyncTask extends AsyncTask<Void, Void, Void> {
	
	private Twitter twitter;
	
	Context mContext;
	SharedPreferences mPreferences;
	
	// variables para mostrar la notificacion en android
	
    int mTotalSumMentions = 0;
    int mTotalSumDMs = 0;
    int mTotalSumSearches = 0;
    boolean showNotification = false;
	
    // variables para ADW Launcher
    
    int mTotalTimelineADW = 0;
    int mTotalMentionsADW = 0;
    int mTotalDMsAWD = 0;
    int mTotalSearchesAWD = 0;
    
    private int mType;

	private AlarmAsyncTaskResponder responder;

	public AlarmOneNotificationAsyncTask(AlarmAsyncTaskResponder responder, Context context, int type) {
		this.responder = responder;
		
		mContext = context;
		
		mType = type;

        ConnectionManager.getInstance().open(mContext);
        twitter = ConnectionManager.getInstance().getAnonymousTwitter();
    			
    	PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);
    	mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	@Override
	protected Void doInBackground(Void... args) {
		try {
			if (!PreferenceUtils.getStatusWorkApp(mContext)) {
				searchUser();
			}
			if (!PreferenceUtils.getStatusWorkApp(mContext)) {
				searchNotifications();
			}
			if (!PreferenceUtils.getStatusWorkApp(mContext)) {
				writeADWLauncher();
			}
			
			if (!PreferenceUtils.getStatusWorkApp(mContext)) {
				shouldSendNotificationAndroid();
			}
		} catch (Exception e) {
			e.printStackTrace();
            PreferenceUtils.saveStatusWorkAlarm(mContext, false);
		} finally {
            PreferenceUtils.saveStatusWorkAlarm(mContext, false);
		}

        PreferenceUtils.saveStatusWorkAlarm(mContext, false);

        WidgetCounters2x1.updateAll(mContext);
		WidgetCounters4x1.updateAll(mContext);
		
		Log.d(Utils.TAG_ALARM, "Finalizado notificaciones en background");
		
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.alarmLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.alarmCancelled();
	}

	@Override
	protected void onPostExecute(Void trends) {
		super.onPostExecute(trends);
		responder.alarmLoaded(trends);
	}
	
	public void loadUser(long id) {
		twitter = ConnectionManager.getInstance().getTwitter(id);
	}
	
	public void searchUser() {
		try {
            DataFramework.getInstance().open(mContext, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    	List<Entity> users = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

    	boolean mentions = mPreferences.getBoolean("prf_notif_in_mentions", true);
    	boolean dms = mPreferences.getBoolean("prf_notif_in_direct", true);
    	
    	for (int i=0; i<users.size(); i++) {
    		try {

    			if (!PreferenceUtils.getStatusWorkApp(mContext)) {
    				loadUser(users.get(i).getId());
    				Log.d(Utils.TAG_ALARM, "Cargar en background usuario " + twitter.getScreenName());
    			}
    			
    			// TIMELINE
    			
    			if (TweetTopicsUtils.hasColumn(users.get(i).getId(), TweetTopicsUtils.COLUMN_TIMELINE)) {
    				EntityTweetUser etuTimeline = new EntityTweetUser(users.get(i).getId(), TweetTopicsUtils.TWEET_TYPE_TIMELINE);
	    			if (!PreferenceUtils.getStatusWorkApp(mContext) && mType!=OnAlarmReceiver.ALARM_ONLY_OTHERS) {
	    				etuTimeline.saveTweets(mContext, twitter);
	    			}
   					mTotalTimelineADW += etuTimeline.getValueNewCount();
    			}
    			
    			// MENTIONS

                if (TweetTopicsUtils.hasColumn(users.get(i).getId(), TweetTopicsUtils.COLUMN_MENTIONS)) {
                    EntityTweetUser etuMentions = new EntityTweetUser(users.get(i).getId(), TweetTopicsUtils.TWEET_TYPE_MENTIONS);
                    if (!PreferenceUtils.getStatusWorkApp(mContext) && mType!=OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
                        InfoSaveTweets info = etuMentions.saveTweets(mContext, twitter);
                        if (info.getNewMessages()>0 && mentions) showNotification = true;
                    }
                    if (mentions) mTotalSumMentions += etuMentions.getValueNewCount();

                    mTotalMentionsADW += etuMentions.getValueNewCount();
                }
    			
    			// DIRECTOS
                if (TweetTopicsUtils.hasColumn(users.get(i).getId(), TweetTopicsUtils.COLUMN_DIRECT_MESSAGES)) {
                    EntityTweetUser etuDMs = new EntityTweetUser(users.get(i).getId(), TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES);
                    if (!PreferenceUtils.getStatusWorkApp(mContext) && mType!=OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
                        InfoSaveTweets info = etuDMs.saveTweets(mContext, twitter);
                        if (info.getNewMessages()>0 && dms) showNotification = true;
                    }
                    if (dms) mTotalSumDMs += etuDMs.getValueNewCount();
                    mTotalDMsAWD += etuDMs.getValueNewCount();
                }
    			
    			// DIRECTOS ENVIADOS
    			
    			if (!PreferenceUtils.getStatusWorkApp(mContext) && mType!=OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
    				EntityTweetUser etuSentDMs = new EntityTweetUser(users.get(i).getId(), TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES);
    				etuSentDMs.saveTweets(mContext, twitter);
    			}
    			
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	}
    	
		DataFramework.getInstance().close();
	}
	
	public void searchNotifications() {
		
		if (mType!=OnAlarmReceiver.ALARM_ONLY_OTHERS) {
			try {
	            DataFramework.getInstance().open(mContext, Utils.packageName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			List<Entity> searchs = DataFramework.getInstance().getEntityList("search");
			
			for (int i=0; i<searchs.size(); i++) {
				if (searchs.get(i).getInt("notifications")==1 && !PreferenceUtils.getStatusWorkApp(mContext)) {
					EntitySearch es = new EntitySearch(searchs.get(i).getId());
					
					InfoSaveTweets info = es.saveTweets(mContext, true, -1);
					
					int count = es.getValueNewCount();
					
					mTotalSearchesAWD += count;

					if (info.getNewMessages()>0 && searchs.get(i).getInt("notifications_bar")==1) {
						mTotalSumSearches += count;
					}
					
				}
			}
			DataFramework.getInstance().close();
		}


	}
	
	public void writeADWLauncher() {
    	
    	if (!PreferenceUtils.getStatusWorkApp(mContext)) {
	    	boolean noread_adw = mPreferences.getBoolean("prf_no_read_adw", true);
			
	    	if (noread_adw) {
	    		IntegrationADW.createPreferences(mContext);
	    		IntegrationADW.verifyPreferences();
	    		
	    		String color = "";
	    		int number = 0;
	    		
	    		for (int i=1; i<=4; i++) {
	    			String pref = IntegrationADW.getPreference(i);
	    			if (pref.equals(IntegrationADWAdapter.PREFERENCES_SEARCH)) {
	    				if (mTotalSumSearches>0) {
	    					color = IntegrationADW.getColor(IntegrationADWAdapter.PREFERENCES_SEARCH);
	    					number = mTotalSumSearches;
	    					i = 4;
	    					Log.d(Utils.TAG_ALARM, number + " nuevos en busqueda. Enviando a ADWLauncher a paquete " + Utils.packageName + " y color " + color);
	    				}
	    			}
	    			if (pref.equals(IntegrationADWAdapter.PREFERENCES_TIMELINE)) {
	    				if (mTotalTimelineADW>0) {
	    					color = IntegrationADW.getColor(IntegrationADWAdapter.PREFERENCES_TIMELINE);
	    					number = mTotalTimelineADW;
	    					i = 4;
	    					Log.d(Utils.TAG_ALARM, number + " nuevos en timeline. Enviando a ADWLauncher a paquete " + Utils.packageName + " y color " + color);
	    				}
	    			}
	    			if (pref.equals(IntegrationADWAdapter.PREFERENCES_MENTIONS)) {
	    				if (mTotalMentionsADW>0) {
	    					color = IntegrationADW.getColor(IntegrationADWAdapter.PREFERENCES_MENTIONS);
	    					number = mTotalMentionsADW;
	    					i = 4;
	    					Log.d(Utils.TAG_ALARM, number + " nuevos en menciones. Enviando a ADWLauncher a paquete " + Utils.packageName + " y color " + color);
	    				}
	    			}
	    			if (pref.equals(IntegrationADWAdapter.PREFERENCES_DIRECTS)) {
	    				if (mTotalDMsAWD>0) {
	    					color = IntegrationADW.getColor(IntegrationADWAdapter.PREFERENCES_DIRECTS);
	    					number = mTotalDMsAWD;
	    					i = 4;
	    					Log.d(Utils.TAG_ALARM, number + " nuevos en directos. Enviando a ADWLauncher a paquete " + Utils.packageName + " y color " + color);
	    				}
	    			}
	    		}
	    		
	    		if (number>0) {
					Intent intent=new Intent();
					intent.setAction("org.adw.launcher.counter.SEND");
					intent.putExtra("PNAME", Utils.packageName);
					intent.putExtra("COUNT", number);
					if (!color.equals("")) intent.putExtra("COLOR", Color.parseColor(color));
					mContext.sendBroadcast(intent);
	    		}
	    		
	    		
	    	}
	    	
			
    	}
    	
	}
	
	public void shouldSendNotificationAndroid() {

		if (showNotification && !PreferenceUtils.getStatusWorkApp(mContext)) {
			String text = mContext.getString(R.string.notif_mentions) + ": " + mTotalSumMentions + " " + mContext.getString(R.string.notif_directs) + ": " + mTotalSumDMs + " " + mContext.getString(R.string.notif_searches) + ": " + mTotalSearchesAWD;
            NotificationUtils.sendNotification(mContext, mContext.getString(R.string.app_name), text, "", false, true);
		}
		
	}

}
