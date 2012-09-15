package widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.database.EntitySearch;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.List;

public class WidgetCounters4x1 extends AppWidgetProvider {

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
		} else if (GlobalsWidget.WIDGET_UPDATE.equals(action)) {

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
				AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, WidgetCounters4x1.updateViews(context, appWidgetId));
        	}
        	
		} else {
			super.onReceive(context, intent);

			if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
				execIntent(context, intent);
			}
		}
	}
	
	static public void updateAll(Context context) {
		Log.d(Utils.TAG_WIDGET, "Update all 4x1");
		
		AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
		ComponentName widgetComponent = new ComponentName(context, WidgetCounters4x1.class);
		int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
		Intent update = new Intent();
		update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
		update.setAction(GlobalsWidget.WIDGET_UPDATE);
		context.sendOrderedBroadcast(update, null);
		/*
		int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetCounters4x1.class));
		for (int appWidgetId : appWidgetIds) {
	    	Intent i = new Intent(Intent.ACTION_VIEW);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			i.putExtra(GlobalsWidget.WIDGET_ID, GlobalsWidget.WIDGET_COUNTERS_4X1);
			context.sendOrderedBroadcast(i, null);
		}*/
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		Log.d(Utils.TAG_WIDGET, "Creando widget(s) de 4x1");

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
        
        Entity e = null;
        
        if (id<0) {
        	e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
        } else {
        	e = DataFramework.getInstance().getTopEntity("users", DataFramework.KEY_ID+"="+id, "");
        }
        
        DataFramework.getInstance().close();
        
        return e;
	}
	
	private static PendingIntent getLaunchPendingIntent(Context context, int appWidgetId, int buttonId) {
		Intent launchIntent = new Intent();
		launchIntent.setClass(context, WidgetCounters4x1.class);
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
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main_4x1);
			
			if (userId>0) {
				
				try {
		            DataFramework.getInstance().open(context, Utils.packageName);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
				
				views.setImageViewBitmap(R.id.img_avatar, ImageUtils.getBitmapAvatar(userId, Utils.AVATAR_LARGE));
				
				views.setOnClickPendingIntent(R.id.img_avatar,
						getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_AVATAR));
				
				views.setOnClickPendingIntent(R.id.timeline,
						getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_TIMELINE));
				
				views.setOnClickPendingIntent(R.id.mentions,
						getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_MENTIONS));
				
				views.setOnClickPendingIntent(R.id.direct_messages,
						getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_DMS));
				
				views.setOnClickPendingIntent(R.id.searches,
						getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_SEARCH));
				
				views.setOnClickPendingIntent(R.id.new_status,
						getLaunchPendingIntent(context, appWidgetId, GlobalsWidget.BUTTON_WIDGET_NEW_STATUS));
				
				// poner número de mensajes
				
		    	int totalTimeline = 0;
		    	int totalMentions = 0;
		    	int totalDirectMessages = 0;
		    	
		    	Entity e = new Entity("users", userId);
		    	if (e!=null) {
	
		    		if (e.getInt("no_save_timeline")!=1) {
		    			totalTimeline = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_TIMELINE
		       				+ " AND user_tt_id="+e.getId() + " AND tweet_id >'" + Utils.fillZeros(""+e.getString("last_timeline_id"))+"'");
		    		}
	
		    		totalMentions = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_MENTIONS
		       				+ " AND user_tt_id="+e.getId() + " AND tweet_id >'" + Utils.fillZeros(""+e.getString("last_mention_id"))+"'");
	
		    		totalDirectMessages = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES
		       				+ " AND user_tt_id="+e.getId() + " AND tweet_id >'" + Utils.fillZeros(""+e.getString("last_direct_id"))+"'");
	
		    	}
				
		    	if (totalTimeline>0) {
		    		views.setViewVisibility(R.id.count_timeline, View.VISIBLE);
		    		views.setImageViewBitmap(R.id.count_timeline, ImageUtils.getBitmapNumber(context, totalTimeline, Color.RED, Utils.TYPE_RECTANGLE));
		    	} else {
		    		views.setViewVisibility(R.id.count_timeline, View.GONE);
		    	}
		    	
		    	if (totalMentions>0) {
		    		views.setViewVisibility(R.id.count_mentions, View.VISIBLE);
		    		views.setImageViewBitmap(R.id.count_mentions, ImageUtils.getBitmapNumber(context, totalMentions, Color.RED, Utils.TYPE_RECTANGLE));
		    	} else {
		    		views.setViewVisibility(R.id.count_mentions, View.GONE);
		    	}
		    	
		    	if (totalDirectMessages>0) {
		    		views.setViewVisibility(R.id.count_directmessages, View.VISIBLE);
		    		views.setImageViewBitmap(R.id.count_directmessages, ImageUtils.getBitmapNumber(context, totalDirectMessages, Color.RED, Utils.TYPE_RECTANGLE));
		    	} else {
		    		views.setViewVisibility(R.id.count_directmessages, View.GONE);
		    	}
		    	
		    	List<Entity> searchs = DataFramework.getInstance().getEntityList("search");
				int totalNotification = 0;
				for (int i=0; i<searchs.size(); i++) {
					if (searchs.get(i).getInt("notifications")==1) {
						EntitySearch es = new EntitySearch(searchs.get(i).getId());
						totalNotification += es.getInt("new_tweets_count");
					}
				}
				if (totalNotification>0) {
					views.setViewVisibility(R.id.count_searches, View.VISIBLE);
					views.setImageViewBitmap(R.id.count_searches, ImageUtils.getBitmapNumber(context, totalNotification, Color.RED, Utils.TYPE_RECTANGLE));
				} else {
					views.setViewVisibility(R.id.count_searches, View.GONE);
				}
				
				Log.d(Utils.TAG_WIDGET, "TL: " + totalTimeline + " M: " + totalMentions + " DMs: " + totalDirectMessages + " B: " + totalNotification);
				
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
			i.setClass(context, WidgetCountersConf4x1.class);
			i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			//i.putExtra(TAG_CONFIG, true);
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
		
		if (buttonId==GlobalsWidget.BUTTON_WIDGET_SEARCH) {
			Intent i = new Intent(context, TweetTopicsActivity.class);
			i.setAction(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(TweetTopicsActivity.KEY_EXTRAS_GOTO_COLUMN_TYPE, TweetTopicsUtils.COLUMN_MY_ACTIVITY);
			context.startActivity(i);		
		}
		
		if (buttonId==GlobalsWidget.BUTTON_WIDGET_NEW_STATUS) {
			Intent i = new Intent(context, NewStatusActivity.class);
			i.setAction(Intent.ACTION_VIEW);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("start_user_id", userId);
			context.startActivity(i);		
		}
		
	}

	
}
