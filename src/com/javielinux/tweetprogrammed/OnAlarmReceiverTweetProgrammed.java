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

package com.javielinux.tweetprogrammed;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.updatestatus.ServiceUpdateStatus;
import com.javielinux.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;


public class OnAlarmReceiverTweetProgrammed extends BroadcastReceiver {
	
	private static final int ALARM_REQUEST_CODE = 0;

	
    @Override
    public void onReceive(Context context, Intent intent) {
		Log.d(Utils.TAG, "Lanzando tweet programado");   

		try {
            DataFramework.getInstance().open(context, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
    	if (intent.getExtras().containsKey(DataFramework.KEY_ID)) {
    		long id = intent.getExtras().getLong(DataFramework.KEY_ID);
    		Entity prog = new Entity("tweets_programmed", id);
    		
    		Entity ent = new Entity("send_tweets");
	    	ent.setValue("users", prog.getString("users"));
	    	ent.setValue("text", prog.getString("text"));
	    	ent.setValue("is_sent", 0);
	    	ent.setValue("type_id", prog.getString("type_id"));
	    	ent.setValue("username_direct", prog.getString("type_id"));
	    	ent.setValue("photos", prog.getString("photos"));
	    	ent.setValue("tweet_programmed_id", id);
    		ent.setValue("reply_tweet_id",  "-1");
	    	
	    	ent.setValue("use_geo", "0");
	    	ent.save();
	    	
	    	context.startService(new Intent(context, ServiceUpdateStatus.class));
	    	
    	}
    	
    	DataFramework.getInstance().close();
    	
    }


	public static void callNextAlarm(Context cnt) {
		
		try {
            DataFramework.getInstance().open(cnt, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Entity ent = null;
        
        Calendar now = Calendar.getInstance();
        		
		ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("tweets_programmed", "is_sent = 0", "date asc");
		
		for (Entity e : ents) {
			if (ent==null) {
				if (e.getLong("date")>now.getTimeInMillis()) {
					ent = e;
				}
			}
		}
		
		if (ent!=null) {
			
			long id = ent.getId();
			long date = ent.getLong("date");
		
			Intent intent = new Intent(cnt, OnAlarmReceiverTweetProgrammed.class);
			intent.putExtra(DataFramework.KEY_ID, id);
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			
			AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);
					
			//Date d = new Date(date);
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(date);
			
			alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
			
			Log.d(Utils.TAG, "Programa alarma con id " + id);
			
		}
		
		DataFramework.getInstance().close();

	}
/*
	public static void stopAlarm(Context cnt, long id) {
		
		Intent intent = new Intent(cnt, OnAlarmReceiverTweetProgrammed.class);
		intent.putExtra(DataFramework.KEY_ID, id);
        PendingIntent sender = PendingIntent.getBroadcast(cnt, 0, intent, 0);

        AlarmManager am = (AlarmManager)cnt.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
        
        Log.d(Utils.TAG, "Parar alarma con id " + id);

	}
*/

      
 }