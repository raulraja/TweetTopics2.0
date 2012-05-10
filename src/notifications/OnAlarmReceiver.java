package notifications;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.Utils;


public class OnAlarmReceiver {
	
	public static final int ALARM_ONLY_TIMELINE = 0;
	public static final int ALARM_ONLY_OTHERS = 1;
	public static final int ALARM_ALL = 2;
	
	private static final int ALARM_REQUEST_CODE = 0;

    public static void callAlarm(Context cnt) {
    	callAlarm(cnt, -1, -1);
    }
    
    public static void callAlarmTimeline(Context cnt, int minutesTimeline) {
    	callAlarm(cnt, minutesTimeline, -1);
    }
    
    public static void callAlarmOthers(Context cnt, int minutesOthers) {
    	callAlarm(cnt, -1, minutesOthers);
    }

	public static void callAlarm(Context cnt, int minutesTimeline, int minutesOthers) {
		
		stopAlarms(cnt);
		
		PreferenceManager.setDefaultValues(cnt, R.xml.preferences, false);
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(cnt);
		if (minutesTimeline<0) minutesTimeline = Integer.parseInt(preference.getString("prf_time_notifications", "15"));
		if (minutesOthers<0) minutesOthers = Integer.parseInt(preference.getString("prf_time_notifications_mentions_dm", "15"));
		
		if (minutesTimeline==minutesOthers) {
			call(cnt, minutesTimeline, ALARM_ALL);
		} else {
			if (minutesTimeline>0) {
				call(cnt, minutesTimeline, ALARM_ONLY_TIMELINE);
			}
			
			if (minutesOthers>0) {
				callOthers(cnt, minutesOthers);			
			}
		}

	}
	
	private static void call(Context cnt, int minutes, int type) {
		Intent intent = new Intent(cnt, Alarm1.class);
		intent.putExtra("type", type);
		
		
		if (type==ALARM_ONLY_TIMELINE) {
			Log.d(Utils.TAG_ALARM, "Establecemos alarma (timeline) para dentro de " + minutes  + " minutos");
		} else if (type==ALARM_ONLY_OTHERS) {
			Log.d(Utils.TAG_ALARM, "Establecemos alarma (menciones y dms) para dentro de " + minutes  + " minutos");
		} else if (type==ALARM_ALL) {
			Log.d(Utils.TAG_ALARM, "Establecemos alarma (todas) para dentro de " + minutes  + " minutos");
		}
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);
		
		long interval = minutes*60*1000;
		long time = System.currentTimeMillis() + interval;

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
	}
	
	private static void callOthers(Context cnt, int minutes) {
		Intent intent = new Intent(cnt, Alarm2.class);
		intent.putExtra("type", ALARM_ONLY_OTHERS);
		
		Log.d(Utils.TAG_ALARM, "Establecemos alarma (menciones y dms) para dentro de " + minutes  + " minutos");
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(cnt, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) cnt.getSystemService(Context.ALARM_SERVICE);
		
		long interval = minutes*60*1000+(10*1000);
		long time = System.currentTimeMillis() + interval;

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
	}

	public static void stopAlarms(Context cnt) {
		
		Intent intent = new Intent(cnt, Alarm1.class);
        PendingIntent sender = PendingIntent.getBroadcast(cnt, 0, intent, 0);

        AlarmManager am = (AlarmManager)cnt.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
		
        Intent intent2 = new Intent(cnt, Alarm2.class);
        PendingIntent sender2 = PendingIntent.getBroadcast(cnt, 0, intent2, 0);

        am.cancel(sender2);
        
		Utils.saveStatusWorkAlarm(cnt, false);

	}
      
 }