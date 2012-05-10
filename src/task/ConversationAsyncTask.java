package task;

import adapters.StatusListAdapter;
import android.content.Context;
import android.os.AsyncTask;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

import java.util.ArrayList;


public class ConversationAsyncTask extends AsyncTask<Long, Void, ConversationAsyncTask.ConversationResult> {

	public interface ConversationAsyncTaskResponder {
		public void conversationLoading();
		public void conversationCancelled();
		public void conversationLoaded(ConversationAsyncTask.ConversationResult searchResult);
	}
	
	public class ConversationResult {
		public StatusListAdapter statusListAdapter;
		public boolean error = false;
		public ConversationResult() {
			super();
		}
	}

	private ConversationAsyncTaskResponder responder;
	private TweetTopicsCore mTweetTopicsCore;
	private Context mContext;

	public ConversationAsyncTask(ConversationAsyncTaskResponder responder, Context context) {
		this.responder = responder;
		this.mTweetTopicsCore = (TweetTopicsCore)responder;
		this.mContext = context;
	}

	@Override
	protected ConversationAsyncTask.ConversationResult doInBackground(Long... args) {
		ConversationResult cr = new ConversationResult();
		try {
			ConnectionManager.getInstance().open(mContext);
			ArrayList<twitter4j.Status> tweets = new ArrayList<twitter4j.Status>();
			twitter4j.Status st = ConnectionManager.getInstance().getTwitter().showStatus(args[0]);
	
		    tweets.add(st);
		    while (st.getInReplyToStatusId()>0) {
		    	st = ConnectionManager.getInstance().getTwitter().showStatus(st.getInReplyToStatusId());
	    	    tweets.add(st);
		    }
		    cr.statusListAdapter = new StatusListAdapter(mContext, mTweetTopicsCore, tweets);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return cr;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.conversationLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.conversationCancelled();
	}

	@Override
	protected void onPostExecute(ConversationAsyncTask.ConversationResult searchResult) {
		super.onPostExecute(searchResult);
		responder.conversationLoaded(searchResult);
	}

}
