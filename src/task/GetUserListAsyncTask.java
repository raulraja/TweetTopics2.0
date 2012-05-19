package task;

import android.os.AsyncTask;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;
import task.GetUserListAsyncTask.GetUserListAsyncResult;
import twitter4j.ResponseList;
import twitter4j.UserList;

public class GetUserListAsyncTask extends AsyncTask<Entity, Void, GetUserListAsyncResult> {

	public static int SHOW_TWEETS = 1;
	public static int SHOW_TWEETS_FOLLOWINGLIST = 2;

	public interface GetUserListAsyncTaskResponder {
		public void getUserListLoading();
		public void getUserListCancelled();
		public void getUserListLoaded(GetUserListAsyncResult result);
	}

    public class GetUserListAsyncResult {
		public boolean result;
        public String error_message = "";
		public GetUserListAsyncResult() {
			super();
		}
    }

	private GetUserListAsyncTaskResponder responder;
	private TweetTopicsCore mTweetTopicsCore;

	public GetUserListAsyncTask(TweetTopicsCore responder) {
		mTweetTopicsCore = responder;
		this.responder = (GetUserListAsyncTaskResponder)responder;
	}

	@Override
	protected GetUserListAsyncResult doInBackground(Entity... args) {
        GetUserListAsyncResult result = new GetUserListAsyncResult();

		try {
			ConnectionManager.getInstance().open(mTweetTopicsCore.getTweetTopics());

            // Delete user list from database for updating the info
            String sql_delete_user_list = "DELETE FROM user_lists WHERE user_id="+ args[0].getInt("user_id") + " AND type_id=1";
            DataFramework.getInstance().getDB().execSQL(sql_delete_user_list);

            ResponseList<UserList> user_list = ConnectionManager.getInstance().getTwitter().getAllUserLists(args[0].getString("name"));

            for (int i = 0; i < user_list.size(); i++) {
                Entity user_list_entity = new Entity("user_lists");

                user_list_entity.setValue("user_id", args[0].getInt("user_id"));
                user_list_entity.setValue("user_screenname", user_list.get(i).getUser().getScreenName());
                user_list_entity.setValue("url_avatar", user_list.get(i).getUser().getProfileImageURL());
                user_list_entity.setValue("userlist_id", user_list.get(i).getId());
                user_list_entity.setValue("type_id", 1);
                user_list_entity.setValue("name", user_list.get(i).getName());
                user_list_entity.setValue("full_name", user_list.get(i).getFullName());

                if (!user_list_entity.save())
                    throw new Exception();
            }

            // Delete user list from database for updating the info
            String sql_delete_user_following_list = "DELETE FROM user_lists WHERE user_id="+ args[0].getInt("user_id") + " AND type_id=2";
            DataFramework.getInstance().getDB().execSQL(sql_delete_user_following_list);

            ResponseList<UserList> user_following_list = ConnectionManager.getInstance().getTwitter().getUserListMemberships(args[0].getString("name"), -1);

            for (int i = 0; i < user_following_list.size(); i++) {
                Entity user_list_entity = new Entity("user_lists");
                user_list_entity.setValue("user_id", args[0].getInt("user_id"));
                user_list_entity.setValue("user_screenname", user_following_list.get(i).getUser().getScreenName());
                user_list_entity.setValue("url_avatar", user_following_list.get(i).getUser().getProfileImageURL());
                user_list_entity.setValue("userlist_id", user_following_list.get(i).getId());
                user_list_entity.setValue("type_id", 2);
                user_list_entity.setValue("name", user_following_list.get(i).getName());
                user_list_entity.setValue("full_name", user_following_list.get(i).getFullName());

                if (!user_list_entity.save())
                    throw new Exception();
            }

            result.result = true;
		} catch (Exception e) {
			e.printStackTrace();
            result.result = false;
		}

        return result;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.getUserListLoading();
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		responder.getUserListCancelled();
	}

	@Override
	protected void onPostExecute(GetUserListAsyncResult result) {
		super.onPostExecute(result);
		responder.getUserListLoaded(result);
	}
}
