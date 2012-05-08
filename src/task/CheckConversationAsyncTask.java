package task;

import infos.InfoTweet;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.AsyncTask;

import com.javielinux.twitter.ConnectionManager;


public class CheckConversationAsyncTask extends AsyncTask<Long, Void, twitter4j.Status> {

	public interface CheckConversationAsyncTaskResponder {
		public void checkConversationLoading();
		public void checkConversationCancelled();
		public void checkConversationLoaded(twitter4j.Status status);
	}

	private CheckConversationAsyncTaskResponder responder;
	private int from = InfoTweet.FROM_STATUS;
	private Context mContext;

	public CheckConversationAsyncTask(Context cnt, CheckConversationAsyncTaskResponder responder, int from) {
		this.responder = responder;
		this.from = from;
		mContext = cnt;
	}

	@Override
	protected twitter4j.Status doInBackground(Long... args) {
		try {
			ConnectionManager.getInstance().open(mContext);
			if (from == InfoTweet.FROM_STATUS) {
				return ConnectionManager.getInstance().getTwitter().showStatus(args[0]);
			} else {
				long i = ConnectionManager.getInstance().getTwitter().showStatus(args[0]).getInReplyToStatusId();
				if (i>0) {
					return ConnectionManager.getInstance().getTwitter().showStatus(i);
				} else {
					return null;
				}
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.checkConversationLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.checkConversationCancelled();
	}

	@Override
	protected void onPostExecute(twitter4j.Status status) {
		super.onPostExecute(status);
		responder.checkConversationLoaded(status);
	}

}
