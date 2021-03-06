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
import com.javielinux.adapters.UsersAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

public class WidgetCountersConf4x1 extends Activity {
	
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setResult(RESULT_CANCELED);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
                
    	try {
        	DataFramework.getInstance().open(this, Utils.packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showDialogUsers();

		
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
	
    public void showDialogUsers() {
    	    	
    	final UsersAdapter adapter = new UsersAdapter(this, DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\""));

    	AlertDialog builder = new AlertDialog.Builder(this)
    		.setCancelable(true)
            .setTitle(R.string.users)
            .setAdapter(adapter, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					changeUser(adapter.getItem(which).getId());
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
    
    private void changeUser(long id) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setAction(GlobalsWidget.WIDGET_UPDATE);
		i.putExtra("id_user", id);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		WidgetCountersConf4x1.this.sendOrderedBroadcast(i, null);
		finish();
    }
    
}
