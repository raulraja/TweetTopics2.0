package com.javielinux.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.RowColumnWidgetAdapter;
import com.javielinux.adapters.RowLinkWidgetAdapter;
import com.javielinux.adapters.RowSearchWidgetAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class WidgetTweetsLinks4x2 extends Activity {
		
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    ArrayList<String> links = new ArrayList<String>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setResult(RESULT_CANCELED);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            links = extras.getStringArrayList(GlobalsWidget.WIDGET_LINKS);
        }
                
    	try {
        	DataFramework.getInstance().open(this, Utils.packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showLinksDialog();
    }
    
    public void showLinksDialog() {
    	
        final RowLinkWidgetAdapter adapter = new RowLinkWidgetAdapter(this, links);

        AlertDialog builder = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(Utils.toCapitalize(this.getText(R.string.links).toString()))
                .setAdapter(adapter, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent defineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)adapter.getItem(which)));
                        startActivity(defineIntent);
                        finish();
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface arg0) {
                        finish();
                    }
                })
                .create();
        builder.show();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
    
}
