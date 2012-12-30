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

package com.javielinux.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.RowColumnWidgetAdapter;
import com.javielinux.adapters.RowSearchWidgetAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ColumnsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class WidgetTweetsConf4x2 extends Activity {
		
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setResult(RESULT_CANCELED);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
                
    	try {
        	DataFramework.getInstance().open(this, Utils.packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showDialogType();
    }
    
    public void showDialogType() {
    	
    	CharSequence[] cs = new CharSequence[3];
    	cs [0] = this.getText(R.string.timeline);
    	cs [1] = this.getText(R.string.mentions);
    	cs [2] = Utils.toCapitalize(this.getText(R.string.searchs).toString());
    	
		final ArrayList<Integer> arType = new ArrayList<Integer>();
		arType.add(ServiceWidgetTweets4x2.TIMELINE);
		arType.add(ServiceWidgetTweets4x2.MENTIONS);    	
		arType.add(ServiceWidgetTweets4x2.SEARCH);

        final RowColumnWidgetAdapter adapter = new RowColumnWidgetAdapter(this, ColumnsUtils.widgetColumnList());

        AlertDialog builder = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(Utils.toCapitalize(this.getText(R.string.options).toString()))
                .setAdapter(adapter, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                        i.putExtra("column_id",((Entity)adapter.getItem(which)).getId());
                        WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
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

    	/*AlertDialog builder = new AlertDialog.Builder(this)
    		.setCancelable(true)
            .setTitle(R.string.options)
            .setItems(cs, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int type = arType.get(which);
					if (type == ServiceWidgetTweets4x2.TIMELINE) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
						i.putExtra("id_user", ServiceWidgetTweets4x2.TIMELINE);
						WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
						finish();
					} else if (type == ServiceWidgetTweets4x2.MENTIONS) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
						i.putExtra("id_user", ServiceWidgetTweets4x2.MENTIONS);
						WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
						finish();
					} else if (type == ServiceWidgetTweets4x2.SEARCH) {
						showDialogSearch();
					}
				}
            	
            })
            .setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					finish();
				}
            })
           .create();  
    	builder.show();*/
        
    }
    
    public void showDialogSearch() {
    	
    	final RowSearchWidgetAdapter adapter = new RowSearchWidgetAdapter(this, DataFramework.getInstance().getEntityList("search", "is_temp=0"));

    	AlertDialog builder = new AlertDialog.Builder(this)
    		.setCancelable(true)
            .setTitle(Utils.toCapitalize(this.getText(R.string.searchs).toString()))
            .setAdapter(adapter, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
					i.putExtra("id_search", adapter.getItem(which).getId());
					WidgetTweetsConf4x2.this.sendOrderedBroadcast(i, null);
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
