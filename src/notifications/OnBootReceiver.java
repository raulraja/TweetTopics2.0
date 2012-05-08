package notifications;

import tweetprogrammed.OnAlarmReceiverTweetProgrammed;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.javielinux.tweettopics.Utils;

public class OnBootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(Utils.TAG_ALARM, "Cargando alarma de TweetTopics al arrancar el telefono");
		Utils.saveStatusWorkAlarm(context, false);
		OnAlarmReceiver.callAlarm(context, 1, 1);

      	OnAlarmReceiverTweetProgrammed.callNextAlarm(context);
                
	}
}
