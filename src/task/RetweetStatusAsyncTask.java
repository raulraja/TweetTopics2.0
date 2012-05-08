package task;

import twitter4j.TwitterException;
import android.os.AsyncTask;

import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;


public class RetweetStatusAsyncTask extends AsyncTask<Long, Void, Boolean> {

	public interface RetweetStatusAsyncTaskResponder {
		public void retweetStatusLoading();
		public void retweetStatusCancelled();
		public void retweetStatusLoaded(boolean error);
	}

	private RetweetStatusAsyncTaskResponder responder;
	private TweetTopicsCore mTweetTopicsCore;

	public RetweetStatusAsyncTask(TweetTopicsCore responder) {
		mTweetTopicsCore = responder;
		this.responder = (RetweetStatusAsyncTaskResponder)responder;
	}

	@Override
	protected Boolean doInBackground(Long... args) {
		try {
			ConnectionManager.getInstance().open(mTweetTopicsCore.getTweetTopics());
			ConnectionManager.getInstance().getTwitter().retweetStatus(args[0]);
		} catch (TwitterException e) {
			return true;
		}
		return false;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.retweetStatusLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.retweetStatusCancelled();
	}

	@Override
	protected void onPostExecute(Boolean error) {
		super.onPostExecute(error);
		responder.retweetStatusLoaded(error);
	}

}
