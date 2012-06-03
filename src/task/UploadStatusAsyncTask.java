package task;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import com.javielinux.tweettopics2.NewStatus;
import com.javielinux.utils.LocationUtils;
import com.javielinux.utils.Utils;
import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class UploadStatusAsyncTask extends AsyncTask<String, Void, Boolean> {

	public interface UploadStatusAsyncTaskResponder {
		public void uploadStatusLoading();
		public void uploadStatusCancelled();
		public void uploadStatusLoaded(boolean error);
	}
	
	private Context mContext;
	private Twitter twitter;
	private int mModeTweetLonger = NewStatus.MODE_TL_NONE;

	private UploadStatusAsyncTaskResponder responder;

	public UploadStatusAsyncTask(Context context, UploadStatusAsyncTaskResponder responder, Twitter twitter, int modeTweetLonger) {
		this.mContext = context;
		this.responder = responder;
		this.twitter = twitter;
		mModeTweetLonger = modeTweetLonger;
	}
	
	private boolean updateText(String text, long tweet_id, boolean useGeo) {
		StatusUpdate su = new StatusUpdate(text);
		if (useGeo) {
			Location loc = LocationUtils.getLastLocation(mContext);
			GeoLocation gl = new GeoLocation(loc.getLatitude(), loc.getLongitude());
			su.setLocation(gl);
		}
		if (tweet_id>0) su.inReplyToStatusId(tweet_id);
		try {
			twitter.updateStatus(su);
		} catch (TwitterException e) {
			e.printStackTrace();
            return false;
		} catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
	}

	@Override
	protected Boolean doInBackground(String... args) {
        boolean error = false;
		try {
			String text = args[0];
			Log.d(Utils.TAG, "Enviando a twitter: " + text);
			long tweet_id = Long.parseLong(args[1]);
			boolean useGeo = args[2].equals("1")?true:false;
			
			if (mModeTweetLonger == NewStatus.MODE_TL_NONE) {
                error = !updateText(text, tweet_id, useGeo);
			} else {
				String replyuser = "";
				if (tweet_id>0) {
					replyuser =  text.substring(0, text.indexOf(" ")).trim();
				}					
				ArrayList<String> ar = Utils.getDivide140(text, replyuser);
				for (String t : ar) {
                    error = !updateText(t, tweet_id, useGeo);
				}	
			}

		} catch (Exception e) {
			e.printStackTrace();
            error = true;
		}
		return error;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.uploadStatusLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.uploadStatusCancelled();
	}

	@Override
	protected void onPostExecute(Boolean error) {
		super.onPostExecute(error);
		responder.uploadStatusLoaded(error);
	}

}
