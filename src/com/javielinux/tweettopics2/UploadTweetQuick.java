package com.javielinux.tweettopics2;

import adapters.TweetQuickAlertAdapter;
import adapters.TweetQuickUserAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.utils.Utils;
import preferences.ServiceTweetQuick;

import java.util.ArrayList;
import java.util.List;


public class UploadTweetQuick extends Activity {
		
	private long mUserId = 0;
	private long mTweetQuickId = 0;
	private String fileFromOtherApp = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();
                
        if (Intent.ACTION_SEND.equals(action)) {
        	if (extras!=null && extras.containsKey(Intent.EXTRA_STREAM)) {
        		try {
        			Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
        			Cursor c = managedQuery(uri, null, "", null, null);
        			if (c.getCount() > 0) {
        				c.moveToFirst();
        				int dataIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        				fileFromOtherApp = c.getString(dataIndex);
        			}
        		} catch (Exception e) {
        			e.printStackTrace();
        		}

        	}

        } else {
        	// segundo intento
        	if (extras!=null && extras.containsKey("image")) {
        		fileFromOtherApp = extras.getString("image");
        	}
        }
        
        if (fileFromOtherApp.equals("")) {
        	return;
        }
        
        
        try {
            DataFramework.getInstance().open(this, Utils.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        final List<Entity> ents = DataFramework.getInstance().getEntityList("users");
        
        if (ents.size()<=0) {
        	return;
        } else if (ents.size()==1) {
        	mUserId = ents.get(0).getId();
        	loadTweetQuick();
        } else {
        	TweetQuickUserAdapter users = new TweetQuickUserAdapter(this, ents);
        	AlertDialog builder = new AlertDialog.Builder(this)
	            .setTitle(R.string.users)
	            .setAdapter(users, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mUserId = ents.get(which).getId();
						loadTweetQuick();
					}
	            	
	            })
	           .create();  
	    	builder.show();
        }

    }

    
    public void loadTweetQuick() {
    	final ArrayList<Entity> quicks = DataFramework.getInstance().getEntityList("tweet_quick");
    	Entity normal = new Entity("tweet_quick"); 
    	normal.setValue("name", this.getString(R.string.photo_only));
    	normal.setValue("count", 1);
    	normal.setValue("text", "%PHOTO%");
    	normal.setValue("type_id", 1);
    	quicks.add(0, normal);
    	
    	TweetQuickAlertAdapter adapter = new TweetQuickAlertAdapter(this, quicks);
    	AlertDialog builder = new AlertDialog.Builder(this)
	        .setTitle(R.string.text)
	        .setAdapter(adapter, new OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					upload(quicks.get(which).getId());					
				}
	        	
	        })
	       .create();  
		builder.show();
    }
    
    public void upload(long id) {
    	
    	mTweetQuickId = id;
    	
    	ServiceTweetQuick.mUserId = mUserId;
    	ServiceTweetQuick.mTweetQuickId = mTweetQuickId;
    	ServiceTweetQuick.mImageFile = fileFromOtherApp;
    	
    	this.startService(new Intent(this, ServiceTweetQuick.class));
    	
    	Utils.showMessage(this, this.getString(R.string.upload_photo));
    	setResult(RESULT_OK);
    	finish();
    }

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataFramework.getInstance().close();
    }
    
    
}
