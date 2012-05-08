package task;

import twitter4j.Trend;
import twitter4j.TwitterException;
import android.os.AsyncTask;

import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;


public class TrendsAsyncTask extends AsyncTask<Integer, Void, Trend[]> {

	public interface TrendsAsyncTaskResponder {
		public void trendsLoading();
		public void trendsCancelled();
		public void trendsLoaded(Trend[] trends);
	}

	private TrendsAsyncTaskResponder responder;
	private TweetTopicsCore mTweetTopicsCore;

	public TrendsAsyncTask(TweetTopicsCore responder) {
		mTweetTopicsCore = responder;
		this.responder = responder;
	}

	@Override
	protected Trend[] doInBackground(Integer... args) {
		try {
			ConnectionManager.getInstance().open(mTweetTopicsCore.getTweetTopics());
			if (args.length>0) {
				return ConnectionManager.getInstance().getTwitter().getLocationTrends(args[0]).getTrends();
			} else {
				return ConnectionManager.getInstance().getTwitter().getTrends().getTrends();
			}
		} catch (TwitterException e) {
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.trendsLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.trendsCancelled();
	}

	@Override
	protected void onPostExecute(Trend[] trends) {
		super.onPostExecute(trends);
		responder.trendsLoaded(trends);
	}

}
