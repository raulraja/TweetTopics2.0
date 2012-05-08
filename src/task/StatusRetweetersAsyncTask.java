package task;

import android.content.Context;
import android.os.AsyncTask;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class StatusRetweetersAsyncTask extends AsyncTask<Long, Void, StatusRetweetersAsyncTask.StatusRetweetersResult> {

    public interface StatusRetweetersAsyncTaskResponder {
        public void statusRetweetersLoading();
        public void statusRetweetersCancelled();
        public void statusRetweetersLoaded(StatusRetweetersResult result);
    }

    public class StatusRetweetersResult {
        public ResponseList<User> retweeters_list;
        public boolean error = false;
        public StatusRetweetersResult() {
            super();
        }
    }

    private StatusRetweetersAsyncTaskResponder responder;
    private Context mContext;

	public StatusRetweetersAsyncTask(Context context, StatusRetweetersAsyncTaskResponder responder) {
		this.responder = responder;
		this.mContext = context;
	}

    @Override
    protected StatusRetweetersResult doInBackground(Long... args) {
		StatusRetweetersResult result = new StatusRetweetersResult();

		try {
			ConnectionManager.getInstance().open(mContext);
            ResponseList<User> retweeters_list = ConnectionManager.getInstance().getTwitter().getRetweetedBy(args[0], new Paging(1, 100));
            result.retweeters_list = retweeters_list;

            return result;
		} catch (TwitterException e) {
			e.printStackTrace();
            result.error = true;
            return result;
		}
    }

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.statusRetweetersLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.statusRetweetersCancelled();
	}

	@Override
	protected void onPostExecute(StatusRetweetersResult result) {
		super.onPostExecute(result);
		responder.statusRetweetersLoaded(result);
	}
}
