package api.api.loaders;

import adapters.RowResponseList;
import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Arrays;

public class LoadTypeStatusLoader extends AsynchronousLoader<APIResult> {

	public static int FAVORITES = 0;
	public static int SEARCH_USERS = 1;
	public static int RETWEETED_BYME = 2;
	public static int RETWEETED_TOME = 3;
	public static int RETWEETED_OFME = 4;
	public static int FOLLOWERS = 5;
	public static int FRIENDS = 6;
	public static int TIMELINE = 7;
	public static int LIST = 8;

    private int type = 0;
    private String user_search_text = "";
    private String user = "";
    private int userlist_id = 0;

    public LoadTypeStatusLoader(Context context, Bundle bundle) {
        super(context);

        this.type = bundle.getInt("type");
        this.user_search_text = bundle.getString("user_search_text");
        this.user = bundle.getString("user");
        this.userlist_id = bundle.getInt("userlist_id");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();

		try {
            ConnectionManager.getInstance().open(getContext());
            ArrayList<RowResponseList> result = new ArrayList<RowResponseList>();

            if (type == FAVORITES) {
                ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter().getFavorites();

                for (int i=0; i<statii.size(); i++) {
                    result.add(new RowResponseList(statii.get(i)));
                }
            } else if (type == SEARCH_USERS) {
                ResponseList<User> users = ConnectionManager.getInstance().getTwitter().searchUsers(user_search_text, 0);

                for (User user : users) {
                    RowResponseList row = new RowResponseList(user);
                    result.add(row);
                }
            } else if (type == RETWEETED_BYME) {
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
               IDs followers_ids = ConnectionManager.getInstance().getTwitter().getFollowersIDs(user, -1);

                ResponseList<User> users = null;

                if (followers_ids.getIDs().length <= 100) {
                    users = ConnectionManager.getInstance().getTwitter().lookupUsers(followers_ids.getIDs());
                } else {
                    int hundred_count = followers_ids.getIDs().length / 100;

                    for (int i=0; i < hundred_count; i++) {
                        if (users == null)
                            users = ConnectionManager.getInstance().getTwitter().lookupUsers(Arrays.copyOfRange(followers_ids.getIDs(),i*100,(i+1)*100-1));
                        else
                            users.addAll(ConnectionManager.getInstance().getTwitter().lookupUsers(Arrays.copyOfRange(followers_ids.getIDs(),i*100,(i+1)*100-1)));
                    }

                    if (followers_ids.getIDs().length % 100 > 0)
                        users.addAll(ConnectionManager.getInstance().getTwitter().lookupUsers(Arrays.copyOfRange(followers_ids.getIDs(),hundred_count*100 + 1,followers_ids.getIDs().length-1)));
                }

				for (User user : users) {
					RowResponseList row = new RowResponseList(user);
					result.add(row);
				}
			} else if (type==FRIENDS) {
                IDs friends_ids = ConnectionManager.getInstance().getTwitter().getFriendsIDs(user, -1);
                ResponseList<User> users = null;

                if (friends_ids.getIDs().length <= 100) {
                    users = ConnectionManager.getInstance().getTwitter().lookupUsers(friends_ids.getIDs());
                } else {
                    int hundred_count = friends_ids.getIDs().length / 100;

                    for (int i=0; i < hundred_count; i++) {
                        if (users == null)
                            users = ConnectionManager.getInstance().getTwitter().lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(), i * 100, (i + 1) * 100 - 1));
                        else
                            users.addAll(ConnectionManager.getInstance().getTwitter().lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(),i*100,(i+1)*100-1)));
                    }

                    if (friends_ids.getIDs().length % 100 > 0)
                        users.addAll(ConnectionManager.getInstance().getTwitter().lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(),hundred_count*100 + 1,friends_ids.getIDs().length-1)));
                }

				for (User user : users) {
					RowResponseList row = new RowResponseList(user);
					result.add(row);
				}
			} else if (type==TIMELINE) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getHomeTimeline();
				for (int i=0; i<statii.size(); i++) {
					result.add(new RowResponseList(statii.get(i)));
				}
			} else if (type==LIST) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getUserListStatuses(userlist_id, new Paging(1));
				for (int i=0; i<statii.size(); i++) {
					result.add(new RowResponseList(statii.get(i)));
				}
			}

            out.addParameter("result", result);
            return out;

		} catch (TwitterException e) {
            e.printStackTrace();
            out.setError(e,e.getMessage());
            return out;
		} catch (OutOfMemoryError e) {
            e.printStackTrace();
            out.setError(e,e.getMessage());
            return out;
        } catch (Exception e) {
			e.printStackTrace();
            out.setError(e,e.getMessage());
            return out;
		}
    }
}
