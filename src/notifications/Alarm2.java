package notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import notifications.AlarmAsyncTask.AlarmAsyncTaskResponder;

public class Alarm2 extends BroadcastReceiver implements AlarmAsyncTaskResponder {
	public AsyncTask<Void, Void, Void> task;  
    @Override
    public void onReceive(Context context, Intent intent) { 
    	
    	PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
		int minutes = Integer.parseInt(preference.getString("prf_time_notifications_mentions_dm", "15"));
    	
    	if ( (minutes>0) && (PreferenceUtils.getNotificationsApp(context)) ) {
	    	if (!PreferenceUtils.getStatusWorkApp(context)) {
	    		
	    		int type = intent.getIntExtra("type", OnAlarmReceiver.ALARM_ALL);
	    		
	    		if (type==OnAlarmReceiver.ALARM_ONLY_TIMELINE) {
	    			Log.d(Utils.TAG_ALARM, "Arrancado notificaciones en background (timeline)");
	    		} else if (type==OnAlarmReceiver.ALARM_ONLY_OTHERS) {
	    			Log.d(Utils.TAG_ALARM, "Arrancado notificaciones en background (menciones y dms)");
	    		} else if (type==OnAlarmReceiver.ALARM_ALL) {
	    			Log.d(Utils.TAG_ALARM, "Arrancado notificaciones en background (todas)");
	    		}
   
		    	if (task!=null) task.cancel(true);

                PreferenceUtils.saveStatusWorkAlarm(context, true);
		    	if (preference.getBoolean("prf_notif_type_one_notification", false)) {
		    		task = new AlarmOneNotificationAsyncTask(this, context, type).execute();
		    	} else {
		    		task = new AlarmAsyncTask(this, context, type).execute();
		    	}
	    	}	    	
    	} else {
    		Log.d(Utils.TAG_ALARM, "Notificaciones desactivadas");
    	}
    }
	@Override
	public void alarmCancelled() {
		
	}

	@Override
	public void alarmLoaded(Void trends) {
		task = null;
	}

	@Override
	public void alarmLoading() {
		
	}
}
