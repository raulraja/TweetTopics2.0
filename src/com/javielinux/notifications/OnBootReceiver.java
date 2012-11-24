package com.javielinux.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.javielinux.tweetprogrammed.OnAlarmReceiverTweetProgrammed;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;

public class OnBootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(Utils.TAG_ALARM, "Cargando alarma de TweetTopics al arrancar el telefono");
        PreferenceUtils.saveStatusWorkAlarm(context, false);
		OnAlarmReceiver.callAlarm(context);

      	OnAlarmReceiverTweetProgrammed.callNextAlarm(context);
                
	}
}
