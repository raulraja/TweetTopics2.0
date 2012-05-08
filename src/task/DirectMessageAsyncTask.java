package task;

import java.util.ArrayList;

import com.javielinux.tweettopics.NewStatus;
import com.javielinux.tweettopics.Utils;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.os.AsyncTask;

public class DirectMessageAsyncTask extends AsyncTask<String, Void, Boolean> {

	public interface DirectMessageAsyncTaskResponder {
		public void directMessageStatusLoading();
		public void directMessageStatusCancelled();
		public void directMessageStatusLoaded(boolean error);
	}

	private DirectMessageAsyncTaskResponder responder;
	private Twitter twitter;
	private int mModeTweetLonger = NewStatus.MODE_TL_NONE;

	public DirectMessageAsyncTask(DirectMessageAsyncTaskResponder responder, Twitter twitter, int modeTweetLonger) {
		this.responder = responder;
		this.twitter = twitter;
		mModeTweetLonger = modeTweetLonger;
	}

	@Override
	protected Boolean doInBackground(String... args) {
		try {
			String text = args[0];
			String user = args[1];
			if (mModeTweetLonger == NewStatus.MODE_TL_NONE) {
				twitter.sendDirectMessage(user, text);
			} else {
				ArrayList<String> ar = Utils.getDivide140(text, "");
				for (String t : ar) {
					twitter.sendDirectMessage(user, t);	
				}
			}
		} catch (TwitterException e) {
			e.printStackTrace();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.directMessageStatusLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.directMessageStatusCancelled();
	}

	@Override
	protected void onPostExecute(Boolean error) {
		super.onPostExecute(error);
		responder.directMessageStatusLoaded(error);
	}

}
