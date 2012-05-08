package task;

import android.content.Context;
import android.os.AsyncTask;
import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;


public class GetConversationAsyncTask extends AsyncTask<Long, GetConversationAsyncTask.GetConversationResult, Boolean> {

    public interface GetConversationAsyncTaskResponder {
        public void getFullConversationLoading();
        public void getFullConversationCancelled();
        public void getFullConversationProgressUpdate(GetConversationAsyncTask.GetConversationResult searchResult);
        public void getFullConversationLoaded(Boolean result);
    }

    public class GetConversationResult {
        public twitter4j.Status conversation_status;
        public boolean error = false;
        public GetConversationResult() {
            super();
        }
    }

	private GetConversationAsyncTaskResponder responder;
	private TweetTopicsCore mTweetTopicsCore;
	private Context mContext;

	public GetConversationAsyncTask(GetConversationAsyncTaskResponder responder, Context context) {
		this.responder = responder;
		this.mTweetTopicsCore = (TweetTopicsCore)responder;
		this.mContext = context;
	}

	@Override
	protected Boolean doInBackground(Long... args) {
		GetConversationResult result = new GetConversationResult();

		try {
			ConnectionManager.getInstance().open(mContext);
			twitter4j.Status status = ConnectionManager.getInstance().getTwitter().showStatus(args[0]);

            result.conversation_status = status;
		    publishProgress(result);

		    while (status.getInReplyToStatusId() > 0) {
		    	status = ConnectionManager.getInstance().getTwitter().showStatus(status.getInReplyToStatusId());

                result.conversation_status = status;
                publishProgress(result);
		    }

            return true;
		} catch (TwitterException e) {
			e.printStackTrace();
            return false;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.getFullConversationLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.getFullConversationCancelled();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		responder.getFullConversationLoaded(result);
	}

	@Override
	protected void onProgressUpdate(GetConversationAsyncTask.GetConversationResult ... searchResult) {
		super.onProgressUpdate(searchResult);
		responder.getFullConversationProgressUpdate(searchResult[0]);
	}
}
