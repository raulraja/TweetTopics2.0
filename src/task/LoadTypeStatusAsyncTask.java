package task;

import adapters.RowResponseList;
import android.os.AsyncTask;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;


public class LoadTypeStatusAsyncTask extends AsyncTask<String, Void, ArrayList<RowResponseList>> {
  
	public static int FAVORITES = 0;
	public static int SEARCH_USERS = 1;
	public static int RETWEETED_BYME = 2;
	public static int RETWEETED_TOME = 3;
	public static int RETWEETED_OFME = 4;
	public static int FOLLOWERS = 5;
	public static int FRIENDS = 6;
	public static int TIMELINE = 7;
	public static int LIST = 8;
	
	private int type;
	private LoadTypeStatusResponder responder;
	private TweetTopicsCore mTweetTopicsCore;
	  
	public interface LoadTypeStatusResponder {
		public void loadingTypeStatus();
		public void typeStatusLoaded(ArrayList<RowResponseList> result);
	}
  
  
	public LoadTypeStatusAsyncTask(TweetTopicsCore responder, int type) {
		super();
		mTweetTopicsCore = responder;
		this.responder = (LoadTypeStatusResponder)responder;
		this.type = type;
	}

	@Override
	protected ArrayList<RowResponseList> doInBackground(String...args) {
		
		ArrayList<RowResponseList> result = new ArrayList<RowResponseList>();
		try {
			
			ConnectionManager.getInstance().open(mTweetTopicsCore.getTweetTopics());
			
			if (type==FAVORITES) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getFavorites();
				for (int i=0; i<statii.size(); i++) {
					result.add(new RowResponseList(statii.get(i)));
				}
			} else if (type==SEARCH_USERS) {
				ResponseList<User> users = ConnectionManager.getInstance().getTwitter().searchUsers(args[0], 0);
				for (User user : users) {
					RowResponseList row = new RowResponseList(user);
					result.add(row);
				}
			} else if (type==RETWEETED_BYME) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getRetweetedByMe();
				for (int i=0; i<statii.size(); i++) {
					result.add(new RowResponseList(statii.get(i)));
				}
			} else if (type==RETWEETED_TOME) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getRetweetedToMe();
				for (int i=0; i<statii.size(); i++) {
					result.add(new RowResponseList(statii.get(i)));
				}
			} else if (type==RETWEETED_OFME) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getRetweetsOfMe();
				for (int i=0; i<statii.size(); i++) {
					result.add(new RowResponseList(statii.get(i)));
				}
			} else if (type==FOLLOWERS) {
				/*IDs ids = TweetTopicsCore.twitter.getFollowersIDs(args[0], -1);
				for (long i : ids.getIDs()) {
					RowResponseList row = new RowResponseList(TweetTopicsCore.twitter.showUser(i));
					result.add(row);
				}*/
                // TODO
				/*PagableResponseList<User> users = ConnectionManager.getInstance().getTwitter().getFollowersStatuses(args[0], -1);
				for (User user : users) {
					RowResponseList row = new RowResponseList(user);
					result.add(row);
				}   */
			} else if (type==FRIENDS) {
                // TODO
				/*ResponseList<User> users = ConnectionManager.getInstance().getTwitter().getFriendsStatuses(args[0], -1);
				for (User user : users) {
					RowResponseList row = new RowResponseList(user);
					result.add(row);
				}  */
			} else if (type==TIMELINE) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getHomeTimeline();
				for (int i=0; i<statii.size(); i++) {
					result.add(new RowResponseList(statii.get(i)));
				}
			} else if (type==LIST) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getUserListStatuses(Integer.parseInt(args[1]), new Paging(1));
				for (int i=0; i<statii.size(); i++) {
					result.add(new RowResponseList(statii.get(i)));
				}
			}
			
		} catch (TwitterException e) {
			//throw new RuntimeException("Unable to load timeline", e);
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
            //throw new RuntimeException("Unable to load timeline", e);
            e.printStackTrace();
        } catch (Exception e) {
			//throw new RuntimeException("Unable to load timeline", e);
			e.printStackTrace();
		}
    
		return result;
	}

	@Override
	public void onPreExecute() {
		responder.loadingTypeStatus();
	}
  
	@Override
	public void onPostExecute(ArrayList<RowResponseList> result) {
		responder.typeStatusLoaded(result);
	}


}
