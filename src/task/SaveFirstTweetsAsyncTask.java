package task;

import android.os.AsyncTask;
import com.javielinux.database.EntitySearch;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.tweettopics2.TabGeneral;
import com.javielinux.tweettopics2.TabNewEditSearch;

public class SaveFirstTweetsAsyncTask extends AsyncTask<Long, Void, InfoSaveTweets> {

	public interface SaveFirstTweetsAsyncTaskResponder {
		public void firstTweetLoading();
		public void firstTweetCancelled();
		public void firstTweetLoaded(InfoSaveTweets values);
	}

	private SaveFirstTweetsAsyncTaskResponder responder;

	public SaveFirstTweetsAsyncTask(SaveFirstTweetsAsyncTaskResponder responder) {
		this.responder = responder;
	}

	@Override
	protected InfoSaveTweets doInBackground(Long... args) {
		EntitySearch ent = new EntitySearch(args[0]);
		return ent.saveTweets(TabNewEditSearch.StaticContext, TabGeneral.twitter, false, -1);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.firstTweetLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.firstTweetCancelled();
	}

	@Override
	protected void onPostExecute(InfoSaveTweets values) {
		super.onPostExecute(values);
		responder.firstTweetLoaded(values);
	}

}
