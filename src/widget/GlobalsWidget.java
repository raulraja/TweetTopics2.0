package widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class GlobalsWidget {
	static final int WIDGET_VERSION = 1;
	static final int WIDGET_NOT_CONFIGURED = -1;
	static final int WIDGET_DELETED = 0;
	static final String WIDGET_UPDATE = "com.javielinux.WIDGET_UPDATE_TWEETTOPICS";
    static final String WIDGET_UPDATE_2x1 = "com.javielinux.WIDGET_UPDATE_TWEETTOPICS_2x1";
	static final String PREF_GLOBALS = "g_widget_";
	static final String PREF_WIDGET = "widget_";
	static final String SAVED = "saved";
	public static final String WIDGET_SIZE = "size_widget";
	/*
	public static final String WIDGET_ID = "widget_id";
	public static final int INVALID_WIDGET_ID = 0;
	public static final int WIDGET_COUNTERS_4X1 = 1;
	public static final int WIDGET_4X2 = 2;
	*/
	public static final String WIDGETS_COUNT = "widgets_count";
	
	public static final int BUTTON_WIDGET_AVATAR = 0;
	public static final int BUTTON_WIDGET_TIMELINE = 1;
	public static final int BUTTON_WIDGET_MENTIONS = 2;
	public static final int BUTTON_WIDGET_DMS = 3;
	public static final int BUTTON_WIDGET_SEARCH = 4;
	public static final int BUTTON_WIDGET_NEW_STATUS = 5;

	
	public static void onDeleted(Context context) {

		SharedPreferences widgetPrefs = context.getSharedPreferences(
				GlobalsWidget.PREF_GLOBALS, Context.MODE_PRIVATE);
		Editor editor = widgetPrefs.edit();
		editor.putInt(WIDGETS_COUNT, GlobalsWidget.WIDGET_DELETED);
		editor.commit();

	}
	
	public static int getWidgetsCount(Context cnt) {
		int[] appWidgetIds = AppWidgetManager.getInstance(cnt).getAppWidgetIds(new ComponentName(cnt, WidgetCounters4x1.class));
		return appWidgetIds.length;
	}
	
	/*
    public static int getWidgetsCount(Context cnt) {
    	SharedPreferences prefs = cnt.getSharedPreferences(PREF_GLOBALS, Context.MODE_PRIVATE);
        if (prefs.contains(WIDGETS_COUNT)) {
        	return prefs.getInt(WIDGETS_COUNT, 0);
        } else {
            Editor editor = prefs.edit();
           	editor.putInt(WIDGETS_COUNT, 0);
            editor.commit();
        	return 0;
        }
    }
    
    public static void setWidgetsCount(Context cnt, int count) {
    	SharedPreferences prefs = cnt.getSharedPreferences(PREF_GLOBALS, Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putInt(WIDGETS_COUNT, count);
    	editor.commit();
    }
    
    public static void addWidgetsCount(Context cnt) {
    	int count = getWidgetsCount(cnt);
    	SharedPreferences prefs = cnt.getSharedPreferences(PREF_GLOBALS, Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putInt(WIDGETS_COUNT, count+1);
    	editor.commit();
    }
    
    public static void removeWidgetsCount(Context cnt) {
    	int count = getWidgetsCount(cnt);
    	SharedPreferences prefs = cnt.getSharedPreferences(PREF_GLOBALS, Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	editor.putInt(WIDGETS_COUNT, count-1);
    	editor.commit();
    }
	*/
}
