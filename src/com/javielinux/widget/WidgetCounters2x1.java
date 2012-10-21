package com.javielinux.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

public class WidgetCounters2x1 extends AppWidgetProvider {
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// v1.5 fix that doesn't call onDelete Action
		final String action = intent.getAction();

        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {

            final int appWidgetId = intent.getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(context, new int[] { appWidgetId });
            }
        } else if (GlobalsWidget.WIDGET_UPDATE_2x1.equals(action)) {

            int[] appWidgetIds = new int[0];
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)) {
                appWidgetIds  = intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            }
            if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                int id  = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                if (id!=AppWidgetManager.INVALID_APPWIDGET_ID) {
                    appWidgetIds = new int[1];
                    appWidgetIds[0] = id;
                }
            }

            for (int appWidgetId : appWidgetIds) {

                if (intent.hasExtra("id_user")) {
                    long user_id  = intent.getLongExtra("id_user", 0);

                    SharedPreferences widgetPrefs = context.getSharedPreferences(
                            GlobalsWidget.PREF_WIDGET + appWidgetId, Context.MODE_PRIVATE);
                    Editor editor = widgetPrefs.edit();
                    editor.putLong(DataFramework.KEY_ID, user_id);
                    editor.commit();
                }
                AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, WidgetCounters2x1.updateViews(context, appWidgetId));
            }
        } else {
            super.onReceive(context, intent);

            if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
                execIntent(context, intent);
            }
        }
	}
	
	static public void updateAll(Context context) {
        Log.d(Utils.TAG_WIDGET, "Update all 2x1");

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, WidgetCounters2x1.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        Intent update = new Intent();
        update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        update.setAction(GlobalsWidget.WIDGET_UPDATE_2x1);
        context.sendOrderedBroadcast(update, null);
	}
	
    @Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.d(Utils.TAG_WIDGET, "Creando widget(s) de 2x1");
		BuildWidget(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Log.d(Utils.TAG_WIDGET, "Eliminando widget " + appWidgetId);

            SharedPreferences widgetPrefs = context.getSharedPreferences(
                    GlobalsWidget.PREF_WIDGET + appWidgetId, Context.MODE_PRIVATE);
            Editor editor = widgetPrefs.edit();

            editor.clear();
            editor.putInt(GlobalsWidget.SAVED, GlobalsWidget.WIDGET_DELETED);
            editor.commit();
        }
    }
	
	public static Entity getEntity(Context context) {
        return getEntity(context, -1);
    }

    public static Entity getEntity(Context context, long id) {

        try {
            DataFramework.getInstance().open(context, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Entity user_entity = null;

        if (id<0) {
            user_entity = DataFramework.getInstance().getTopEntity("users", "", "");
        } else {
            user_entity = DataFramework.getInstance().getTopEntity("users", DataFramework.KEY_ID + "=" + id, "");
        }

        DataFramework.getInstance().close();

        return user_entity;
	}
	
	private static PendingIntent getLaunchPendingIntent(Context context, int appWidgetId, int buttonId) {

        Intent launchIntent = new Intent();
        launchIntent.setClass(context, WidgetCounters2x1.class);
        launchIntent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        launchIntent.setData(Uri.parse("custom:" + buttonId));
        launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        launchIntent.putExtra(GlobalsWidget.WIDGET_SIZE, 1);

        PendingIntent pi = PendingIntent.getBroadcast(context, appWidgetId,
                launchIntent, 0);
        return pi;
	}
	
	public static void BuildWidget(Context context,	AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Log.d(Utils.TAG_WIDGET, "Creando widget " + appWidgetId);

            try {
                SharedPreferences widgetPrefs = context.getSharedPreferences(
                        GlobalsWidget.PREF_WIDGET + appWidgetId, Context.MODE_PRIVATE);
                Editor editor = widgetPrefs.edit();

                editor.clear();
                editor.putLong(DataFramework.KEY_ID, getEntity(context).getId());
                editor.commit();

                RemoteViews views = updateViews(context, appWidgetId);
                if (views != null) {
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            } catch (NullPointerException e) {
                Log.d(Utils.TAG_WIDGET, "Error al crear el widget " + appWidgetId);
                e.printStackTrace();
            }
		}
	}

	public static RemoteViews updateViews(Context context,	int appWidgetId) {

        if ( Utils.isLite(context) ) {
            RemoteViews mRemoteLoading = new RemoteViews(context.getPackageName(), R.layout.widget_lite);

            Uri uri = Uri.parse("market://search?q=pname:com.javielinux.tweettopics.pro");
            Intent buyProIntent = new Intent(Intent.ACTION_VIEW, uri);
            PendingIntent configurePendingIntent = PendingIntent.getActivity(context, 0, buyProIntent, 0);
            mRemoteLoading.setOnClickPendingIntent(R.id.btn_w_lite, configurePendingIntent);

            return mRemoteLoading;
    	} else {
            SharedPreferences widgetPrefs = context.getSharedPreferences(
                    GlobalsWidget.PREF_WIDGET + appWidgetId, Context.MODE_PRIVATE);

            long userId = widgetPrefs.getLong(DataFramework.KEY_ID, -1);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main_2x1);

            if (userId>0) {
                try {
                    DataFramework.getInstance().open(context, Utils.packageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                views.setImageViewBitmap(R.id.img_avatar2x1, ImageUtils.getBitmapAvatar(userId, Utils.AVATAR_SMALL));

                views.setOnClickPendingIntent(R.id.img_avatar2x1,
                        getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_AVATAR));

                views.setOnClickPendingIntent(R.id.timeline2x1,
                        getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_TIMELINE));

                views.setOnClickPendingIntent(R.id.mentions2x1,
                        getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_MENTIONS));

                views.setOnClickPendingIntent(R.id.direct_messages2x1,
                        getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_DMS));

                // poner número de mensajes
                int totalTimeline = 0;
                int totalMentions = 0;
                int totalDirectMessages = 0;

                Entity user_entity = null;
                try {
                    user_entity = new Entity("users", userId);
                } catch (CursorIndexOutOfBoundsException e) {}

		    	if (user_entity!=null) {
                    views.setTextViewText(R.id.txt_user_name2x1, user_entity.getString("name"));

                    if (user_entity.getInt("no_save_timeline")!=1) {
                        totalTimeline = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_TIMELINE
		       				+ " AND user_tt_id="+user_entity.getId() + " AND tweet_id >'" + Utils.fillZeros(""+user_entity.getString("last_timeline_id"))+"'");
                    }

                    totalMentions = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_MENTIONS
                            + " AND user_tt_id="+user_entity.getId() + " AND tweet_id >'" + Utils.fillZeros(""+user_entity.getString("last_mention_id"))+"'");

                    totalDirectMessages = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES
		       				+ " AND user_tt_id="+user_entity.getId() + " AND tweet_id >'" + Utils.fillZeros(""+user_entity.getString("last_direct_id"))+"'");
                }

                if (totalTimeline>0) {
                    views.setViewVisibility(R.id.count_timeline2x1, View.VISIBLE);
                    views.setImageViewBitmap(R.id.count_timeline2x1, ImageUtils.getBitmapNumber(context, totalTimeline, Color.RED, Utils.TYPE_RECTANGLE, 11));
                } else {
                    views.setViewVisibility(R.id.count_timeline2x1, View.GONE);
                }

                if (totalMentions>0) {
                    views.setViewVisibility(R.id.count_mentions2x1, View.VISIBLE);
                    views.setImageViewBitmap(R.id.count_mentions2x1, ImageUtils.getBitmapNumber(context, totalMentions, Color.RED, Utils.TYPE_RECTANGLE, 11));
                } else {
                    views.setViewVisibility(R.id.count_mentions2x1, View.GONE);
                }

                if (totalDirectMessages>0) {
                    views.setViewVisibility(R.id.count_directmessages2x1, View.VISIBLE);
                    views.setImageViewBitmap(R.id.count_directmessages2x1, ImageUtils.getBitmapNumber(context, totalDirectMessages, Color.RED, Utils.TYPE_RECTANGLE, 11));
                } else {
                    views.setViewVisibility(R.id.count_directmessages2x1, View.GONE);
                }

                Log.d(Utils.TAG_WIDGET, "TL: " + totalTimeline + " M: " + totalMentions + " DMs: " + totalDirectMessages);

                DataFramework.getInstance().close();
			}
			
			return views;
    	}
	}
	
	public static void execIntent(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        SharedPreferences widgetPrefs = context.getSharedPreferences(
                GlobalsWidget.PREF_WIDGET + appWidgetId, Context.MODE_PRIVATE);

        long userId = widgetPrefs.getLong(DataFramework.KEY_ID, -1);

        Uri data = intent.getData();
        int buttonId = Integer.parseInt(data.getSchemeSpecificPart());

        if (buttonId==GlobalsWidget.BUTTON_WIDGET_AVATAR) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setClass(context, WidgetCountersConf2x1.class);
            i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            context.startActivity(i);
		}
		
		if (buttonId==GlobalsWidget.BUTTON_WIDGET_TIMELINE) {
            Intent i = new Intent(context, TweetTopicsActivity.class);
			i.setAction(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_USER, userId);
			i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_TYPE, TweetTopicsUtils.COLUMN_TIMELINE);
			context.startActivity(i);
		}
		
		if (buttonId==GlobalsWidget.BUTTON_WIDGET_MENTIONS) {
			Intent i = new Intent(context, TweetTopicsActivity.class);
			i.setAction(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_USER, userId);
            i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_TYPE, TweetTopicsUtils.COLUMN_MENTIONS);
			context.startActivity(i);		
		}
		
		if (buttonId==GlobalsWidget.BUTTON_WIDGET_DMS) {
			Intent i = new Intent(context, TweetTopicsActivity.class);
			i.setAction(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_USER, userId);
            i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_TYPE, TweetTopicsUtils.COLUMN_DIRECT_MESSAGES);
			context.startActivity(i);		
		}
	}
}
