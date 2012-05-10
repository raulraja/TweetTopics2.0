package task;

import android.os.AsyncTask;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.TwitterException;


public class TrendsLocationAsyncTask extends AsyncTask<Void, Void, ResponseList<Location>> {

	public interface TrendsLocationAsyncTaskResponder {
		public void trendsLocationLoading();
		public void trendsLocationCancelled();
		public void trendsLocationLoaded(ResponseList<Location> locations);
	}

	private TrendsLocationAsyncTaskResponder responder;
	private TweetTopicsCore mTweetTopicsCore;

	public TrendsLocationAsyncTask(TweetTopicsCore responder) {
		mTweetTopicsCore = responder;
		this.responder = (TrendsLocationAsyncTaskResponder)responder;
	}

	@Override
	protected ResponseList<Location> doInBackground(Void... args) {
		try {
			ConnectionManager.getInstance().open(mTweetTopicsCore.getTweetTopics());
			return ConnectionManager.getInstance().getTwitter().getAvailableTrends();
		} catch (TwitterException e) {
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.trendsLocationLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.trendsLocationCancelled();
	}

	@Override
	protected void onPostExecute(ResponseList<Location> locations) {
		super.onPostExecute(locations);
		responder.trendsLocationLoaded(locations);
	}

}
