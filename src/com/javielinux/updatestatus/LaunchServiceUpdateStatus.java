package com.javielinux.updatestatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

public class LaunchServiceUpdateStatus extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		showDialog();
	}
	
	private void saveToDrafts() {
		
		try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (Entity ent : DataFramework.getInstance().getEntityList("send_tweets")) {
        	Entity draft = new Entity("tweets_draft");
        	draft.setValue("text", ent.getString("text"));
        	draft.save();
        	Utils.showMessage(this, R.string.draft_save);
        }
    	
    	DataFramework.getInstance().close();
	}
	
	private void resend() {
		
		try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        for (Entity ent : DataFramework.getInstance().getEntityList("send_tweets")) {
        	ent.setValue("is_sent", 0);
        	ent.save();
        }
    	
    	DataFramework.getInstance().close();
    	startService(new Intent(LaunchServiceUpdateStatus.this, ServiceUpdateStatus.class));
	}
	
	private int whichSelected = 0;	
	
    public void showDialog() {
    	whichSelected = 0;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				finish();				
			}
    		
    	});
		builder.setTitle(R.string.msg_err_send);
		builder.setSingleChoiceItems(R.array.items_err_send, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	whichSelected = whichButton;
            }
        });
		builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	if (whichSelected==0) {
            		resend();
            	} else if (whichSelected==1) {
            		saveToDrafts();
            	}
            	finish();
            }
        });

		AlertDialog alert = builder.create();
		alert.show();
    } 
	
}
