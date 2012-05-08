package task;

import java.util.ArrayList;

import task.UserListsAsyncTask.UserListsResult;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.UserList;
import android.os.AsyncTask;

import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;


public class UserListsAsyncTask extends AsyncTask<String, Void, UserListsResult> {

	public interface UserListsAsyncTaskResponder {
		public void userListsLoading();
		public void userListsCancelled();
		public void userListsLoaded(UserListsResult result);
	}

	public static int ADD_USER = 0;
	public static int SHOW_TWEETS = 1;
	public static int SHOW_TWEETS_FOLLOWINGLIST = 2;
	
	public class UserListsResult {
		public ResponseList<UserList> response;
		public int type = SHOW_TWEETS;
		public String userAdd;
		public UserListsResult() {
			super();
		}
	}
	
	private int type = SHOW_TWEETS;
	private UserListsAsyncTaskResponder responder;
	private String userAdd = "";
	private TweetTopicsCore mTweetTopicsCore;

	public UserListsAsyncTask(TweetTopicsCore responder, int type, String user) {
		mTweetTopicsCore = responder;
		this.responder = (UserListsAsyncTaskResponder)responder;
		this.type = type;
		this.userAdd = user;
	}

	@Override
	protected UserListsResult doInBackground(String... args) {
		try {
			ConnectionManager.getInstance().open(mTweetTopicsCore.getTweetTopics());
			UserListsResult ulr = new UserListsResult();
			ulr.type = type;
			ulr.userAdd = userAdd;
			if (type==SHOW_TWEETS) {
				ulr.response = ConnectionManager.getInstance().getTwitter().getAllUserLists(args[0]);
			} else if (type==SHOW_TWEETS_FOLLOWINGLIST) {
				ulr.response = ConnectionManager.getInstance().getTwitter().getUserListMemberships(args[0], -1);
			} else {
				ResponseList<UserList> r = ConnectionManager.getInstance().getTwitter().getAllUserLists(args[0]);
				ArrayList<UserList> deleteList = new ArrayList<UserList>(); 
				for (UserList ul : r) {
					if (ul.isFollowing()) deleteList.add(ul);
				}	
				r.removeAll(deleteList);
				ulr.response = r;
			}
			return ulr;
		} catch (TwitterException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.userListsLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.userListsCancelled();
	}

	@Override
	protected void onPostExecute(UserListsResult result) {
		super.onPostExecute(result);
		responder.userListsLoaded(result);
	}

}
