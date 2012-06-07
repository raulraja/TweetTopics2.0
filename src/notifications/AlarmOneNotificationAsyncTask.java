package notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.twitter.ConnectionManager2;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.TweetTopicsConstants;
import com.javielinux.utils.Utils;
import database.EntitySearch;
import database.EntityTweetUser;
import infos.InfoSaveTweets;
import notifications.AlarmAsyncTask.AlarmAsyncTaskResponder;
import preferences.IntegrationADW;
import preferences.IntegrationADWAdapter;
import twitter4j.Twitter;
import widget.WidgetCounters2x1;
import widget.WidgetCounters4x1;

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

        ConnectionManager2.getInstance().open(mContext);
        twitter = ConnectionManager2.getInstance().getAnonymousTwitter();
    			
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
		twitter = ConnectionManager2.getInstance().getTwitter(id);
	}
	
	public void searchUser() {
		try {
            DataFramework.getInstance().open(mContext, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        long idActive = -1;
        
    	List<Entity> users = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");
    	for (int i=0; i<users.size(); i++) {
   			if (users.get(i).getInt("active")==1) {
   				idActive = users.get(i).getId();
   			}
    	}
    	
    	boolean allUsers = mPreferences.getBoolean("prf_no_read_adw_all_users", false);
    	boolean mentions = mPreferences.getBoolean("prf_notif_in_mentions", true);
    	boolean dms = mPreferences.getBoolean("prf_notif_in_direct", true);
    	
    	for (int i=0; i<users.size(); i++) {
    		try {

    			if (!PreferenceUtils.getStatusWorkApp(mContext)) {
    				loadUser(users.get(i).getId());
    				Log.d(Utils.TAG_ALARM, "Cargar en background usuario " + twitter.getScreenName());
    			}
    			
    			// TIMELINE
    			
    			if (users.get(i).getInt("no_save_timeline")!=1) {
    				EntityTweetUser etuTimeline = new EntityTweetUser(users.get(i).getId(), TweetTopicsConstants.TWEET_TYPE_TIMELINE);
	    			if (!PreferenceUtils.getStatusWorkApp(mContext) && mType!=OnAlarmReceiver.ALARM_ONLY_OTHERS) {
	    				etuTimeline.saveTweets(mContext, twitter, true);
	    			}
	    			if (allUsers) {
    					mTotalTimelineADW += etuTimeline.getValueNewCount();
    				} else {
    					if (idActive==users.get(i).getId()) mTotalTimelineADW += etuTimeline.getValueNewCount();
    				}
    			}
    			
    			// MENTIONS
    			EntityTweetUser etuMentions = new EntityTweetUser(users.get(i).getId(), TweetTopicsConstants.TWEET_TYPE_MENTIONS);
    			if (!PreferenceUtils.getStatusWorkApp(mContext) && mType!=OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
    				InfoSaveTweets info = etuMentions.saveTweets(mContext, twitter, true);
    				if (info.getNewMessages()>0 && mentions) showNotification = true;
    			}
				if (mentions) mTotalSumMentions += etuMentions.getValueNewCount();
				
				if (allUsers) {
					mTotalMentionsADW += etuMentions.getValueNewCount();
				} else {
					if (idActive==users.get(i).getId()) mTotalMentionsADW += etuMentions.getValueNewCount();
				}
    			
    			// DIRECTOS
				EntityTweetUser etuDMs = new EntityTweetUser(users.get(i).getId(), TweetTopicsConstants.TWEET_TYPE_DIRECTMESSAGES);
    			if (!PreferenceUtils.getStatusWorkApp(mContext) && mType!=OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
    				InfoSaveTweets info = etuDMs.saveTweets(mContext, twitter, true);
    				if (info.getNewMessages()>0 && dms) showNotification = true;
    			}
    			if (dms) mTotalSumDMs += etuDMs.getValueNewCount();
				if (allUsers) {
					mTotalDMsAWD += etuDMs.getValueNewCount();
				} else {
					if (idActive==users.get(i).getId()) mTotalDMsAWD += etuDMs.getValueNewCount();
				}
    			
    			// DIRECTOS ENVIADOS
    			
    			if (!PreferenceUtils.getStatusWorkApp(mContext) && mType!=OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
    				EntityTweetUser etuSentDMs = new EntityTweetUser(users.get(i).getId(), TweetTopicsConstants.TWEET_TYPE_SENT_DIRECTMESSAGES);
    				etuSentDMs.saveTweets(mContext, twitter, true);
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
					
					InfoSaveTweets info = es.saveTweets(mContext, twitter, true);
					
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
		
		//int total = mTotalSumMentions + mTotalSumDMs + mTotalSumSearches;
		
		if (showNotification && !PreferenceUtils.getStatusWorkApp(mContext)) {
			/*
			boolean vibrate = mPreferences.getBoolean("prf_vibrate_notifications", true);
			boolean sound = mPreferences.getBoolean("prf_sound_notifications", true);
			
			AudioManager audioManager = ((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE));
			
			if (vibrate && audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION)!=AudioManager.VIBRATE_SETTING_OFF) {
				int mode = Integer.parseInt(mPreferences.getString("prf_time_vibrate", "3"));
				if (mode==1) {
					((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
				}
				if (mode==2) {
					((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
				}
				if (mode==3) {
					long[] pattern = { 0, 500, 200, 500, 200 };
					((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, -1);  
				}
				if (mode==4) {
					long[] pattern = { 0, 250, 200, 250, 200, 250, 200, 250, 200 };
					((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, -1);  
				}
			}
			// sonar
			if (sound && audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)>0) {
				Ringtone rt = null;
				String lringtone = mPreferences.getString("prf_ringtone", "");
				if ( lringtone != "" ) {
					rt = RingtoneManager.getRingtone(mContext, Uri.parse(lringtone)); 			 	   		 		     
				} else {
					rt = RingtoneManager.getRingtone(mContext, RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_NOTIFICATION)); 
				}

				if (rt!=null) if (!rt.isPlaying()) rt.play();
                //Ringtone rt = RingtoneManager.getRingtone(this, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
                //if (!rt.isPlaying()) rt.play();
			}*/
			String text = mContext.getString(R.string.notif_mentions) + ": " + mTotalSumMentions + " " + mContext.getString(R.string.notif_directs) + ": " + mTotalSumDMs + " " + mContext.getString(R.string.notif_searches) + ": " + mTotalSearchesAWD;
			setMood(R.drawable.ic_stat_notification, text, -1);
		}
		
	}

	
    private void setMood(int moodId, String text, int type) {
        Notification notification = new Notification(moodId, text, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Intent i = new Intent(mContext, TweetTopicsActivity.class);
        //if (search_id>0) i.putExtra("notification_from_search_id", search_id);
        
        if (type>=0) i.putExtra("notification_from_type", type);
        
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, i, 0);
        notification.setLatestEventInfo(mContext, mContext.getText(R.string.app_name), text, contentIntent);
        
        boolean led = mPreferences.getBoolean("prf_led_notifications", true);
        if (led) {
        	String color = mPreferences.getString("prf_led_color", "#FFFF0000");
        	notification.ledARGB = Color.parseColor(color);//0xFFff0000;
        	notification.flags = Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
        	notification.ledOnMS = 300; 
        	notification.ledOffMS = 1000;
        }
        
        boolean vibrate = mPreferences.getBoolean("prf_vibrate_notifications", true);
        if (vibrate) {
        	int mode = Integer.parseInt(mPreferences.getString("prf_time_vibrate", "3"));
			if (mode==1) {
				long[] pattern = { 500 };
				notification.vibrate = pattern;
			}
			if (mode==2) {
				long[] pattern = { 1000 };
				notification.vibrate = pattern;
			}
			if (mode==3) {
				long[] pattern = { 0, 500, 200, 500, 200 };
				notification.vibrate = pattern;  
			}
			if (mode==4) {
				long[] pattern = { 0, 250, 200, 250, 200, 250, 200, 250, 200 };
				notification.vibrate = pattern;  
			}
        }
        
		boolean sound = mPreferences.getBoolean("prf_sound_notifications", true);
		if (sound) {
			String lringtone = mPreferences.getString("prf_ringtone", "");
			if ( lringtone != "" ) {
				notification.sound = Uri.parse(lringtone);			 	   		 		     
			} else {
				notification.sound = RingtoneManager.getActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_NOTIFICATION);
			}
					
		}
        
        ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(R.layout.tweet_list, notification);
    }

}
