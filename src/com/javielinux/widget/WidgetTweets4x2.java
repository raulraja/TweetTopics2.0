package com.javielinux.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.javielinux.utils.Utils;

public class WidgetTweets4x2 extends AppWidgetProvider {
			
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

//    	for (int i=0; i<appWidgetIds.length; i++) {
//    		Intent intent = new Intent(context, ServiceWidgetTweets4x2.class);
//    		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//    		context.startService(intent);
//    	}
    }

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(Utils.TAG_WIDGET, "OnReceive (widget): " + intent.getAction());
		if (Utils.ACTION_WIDGET_CONTROL.equals(intent.getAction())) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.putExtra("button", intent.getData());
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID));
			context.sendBroadcast(i);
		}

		super.onReceive(context, intent);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		context.stopService(new Intent(context, ServiceWidgetTweets4x2.class));
	}
}