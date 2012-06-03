package preferences;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.NewStatus;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.UploadTweetQuick;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.LocationUtils;
import com.javielinux.utils.Utils;
import task.DirectMessageAsyncTask;
import task.DirectMessageAsyncTask.DirectMessageAsyncTaskResponder;
import task.ImageUploadAsyncTask;
import task.ImageUploadAsyncTask.ImageUploadAsyncTaskResponder;
import task.ImageUploadAsyncTask.ImageUploadResult;
import task.UploadStatusAsyncTask;
import task.UploadStatusAsyncTask.UploadStatusAsyncTaskResponder;
import twitter4j.Twitter;

import java.util.List;

public class ServiceTweetQuick extends Service implements UploadStatusAsyncTaskResponder, ImageUploadAsyncTaskResponder, 
															DirectMessageAsyncTaskResponder {

	static public long mUserId = 0;
	static public long mTweetQuickId = 0;
	static public String mImageFile = "";
	
	private Entity mEntityTweetQuick = null;
	
	public static Twitter twitter;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    @Override
    public void onStart(Intent intent, int startId) {
    	
    	try {
        	DataFramework.getInstance().open(this, Utils.packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d(Utils.TAG, "Arrancando TweetQuick");

		ConnectionManager.getInstance().open(this);
		
    	List<Entity> ents = DataFramework.getInstance().getEntityList("users", DataFramework.KEY_ID + " = " + mUserId);
    	if (ents.size()==1) {
    		twitter = ConnectionManager.getInstance().getTwitter(mUserId, false);
    	}
    	
    	mEntityTweetQuick = new Entity("tweet_quick", mTweetQuickId);
    	
    	uploadImage(mImageFile);
    	
    }
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}

    /*
     * 
     * Update status
     * 
     */
        
    public void updateStatus(String text, long tweet_id) {
		new UploadStatusAsyncTask(this, this, twitter, NewStatus.MODE_TL_NONE).execute(text, tweet_id+"", "0");
    }
	
	@Override
	public void uploadStatusCancelled() {
		
	}

	@Override
	public void uploadStatusLoaded(boolean error) {
		if (error) {
			setMood(R.drawable.ic_stat_tweetquick, this.getString(R.string.photo_upload_problem), true);
		} else {
			setMood(R.drawable.ic_stat_tweetquick, this.getString(R.string.photo_upload_correct), false);
		}
		this.stopSelf();
	}

	@Override
	public void uploadStatusLoading() {
		
	}
	
    /*
     * 
     * Upload image
     * 
     */
	
	public void uploadImage(String file) {
		new ImageUploadAsyncTask(this, twitter).execute(file);
	}

	@Override
	public void imageUploadCancelled() {
		
	}

	@Override
	public void imageUploadLoaded(ImageUploadResult imageUploadResult) {
		boolean error = false;
		if (imageUploadResult.error) {
			error = true;
		} else {
			
			if ( (imageUploadResult.url!=null) && !imageUploadResult.url.equals("") ) {
							
				String text = "";
				
				String textTQ = "%PHOTO%";
				
				if (mEntityTweetQuick.getId()>0) {
					textTQ = mEntityTweetQuick.getString("text");
				}
				
				String[] ar = textTQ.split("%");
				
				for (String token : ar) {
					if (token.equals("ADDRESS")) {
						text += LocationUtils.getAddressFromLastLocation(this);
					} else if (token.equals("PHOTO")) {
						text += imageUploadResult.url;
					} else if (token.equals("COUNTER")) {
						text += mEntityTweetQuick.getString("count");
						mEntityTweetQuick.setValue("count", mEntityTweetQuick.getInt("count")+1);
						mEntityTweetQuick.save();
					} else {
						text += token;
					}
				}
				
				
				String direct_user = mEntityTweetQuick.getString("username_direct");
				
				if (direct_user.equals("")) {
					updateStatus(text, -1);
				} else {
					directMessage(text, direct_user);
				}
				
			} else {
				error = true;
			}
			
			
		}
		
		if (error) {
			setMood(R.drawable.ic_stat_tweetquick, this.getString(R.string.photo_upload_problem), true);
			this.stopSelf();
		}
		
	}

	@Override
	public void imageUploadLoading() {
		
	}
	
    /*
     * 
     * Direct message
     * 
     */
    
    public void directMessage(String text, String user) {
		new DirectMessageAsyncTask(this, twitter, NewStatus.MODE_TL_NONE).execute(text, user);	
    }

	@Override
	public void directMessageStatusCancelled() {
		
	}

	@Override
	public void directMessageStatusLoaded(boolean error) {
		if (error) {
			setMood(R.drawable.ic_stat_tweetquick, this.getString(R.string.photo_upload_problem), true);
		} else {
			setMood(R.drawable.ic_stat_tweetquick, this.getString(R.string.photo_upload_correct), false);
		}
		this.stopSelf();
	}

	@Override
	public void directMessageStatusLoading() {
		
	}
	
    private void setMood(int moodId, String text, boolean tryAgain) {
        Notification notification = new Notification(moodId, text, System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Intent i = new Intent();
        
        if (tryAgain && !mImageFile.equals("")) {
        	i = new Intent(this, UploadTweetQuick.class);
        	i.putExtra("image", mImageFile);
        }
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
        notification.setLatestEventInfo(this, this.getText(R.string.app_name), text, contentIntent);
        
        
        ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).notify(R.layout.tweet_list, notification);
    }
	
}
