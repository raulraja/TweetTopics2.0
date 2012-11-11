package com.javielinux.api.loaders;

import android.content.Context;
import android.util.Log;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadTypeStatusRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadTypeStatusResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.Utils;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Arrays;

public class LoadTypeStatusLoader extends AsynchronousLoader<BaseResponse> {

	public static int FAVORITES = 0;
	public static int SEARCH_USERS = 1;
	public static int RETWEETED_BYME = 2;
	public static int RETWEETED_TOME = 3;
	public static int RETWEETED_OFME = 4;
	public static int FOLLOWERS = 5;
	public static int FRIENDS = 6;
	public static int TIMELINE = 7;
	public static int LIST = 8;
    public static int USER_TIMELINE = 9;

    private LoadTypeStatusRequest request;

    public LoadTypeStatusLoader(Context context, LoadTypeStatusRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            LoadTypeStatusResponse response = new LoadTypeStatusResponse();
            ArrayList<InfoTweet> result = new ArrayList<InfoTweet>();

            ConnectionManager.getInstance().open(getContext());

            if (request.getType() == FAVORITES) {
                ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter(request.getUserId()).getFavorites();

                for (int i=0; i<statii.size(); i++) {
                    result.add(new InfoTweet(statii.get(i)));
                }
            } else if (request.getType() == SEARCH_USERS) {
                ResponseList<User> users = ConnectionManager.getInstance().getTwitter(request.getUserId()).searchUsers(request.getUserSearchText(), 0);

                for (User user : users) {
                    InfoTweet row = new InfoTweet(user);
                    result.add(row);
                }
            } else if (request.getType() == RETWEETED_BYME) {
				ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter(request.getUserId()).getRetweetedByMe();

				for (int i=0; i<statii.size(); i++) {
					result.add(new InfoTweet(statii.get(i)));
				}
			} else if (request.getType()==RETWEETED_TOME) {
				ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter(request.getUserId()).getRetweetedToMe();

				for (int i=0; i<statii.size(); i++) {
					result.add(new InfoTweet(statii.get(i)));
				}
			} else if (request.getType()==RETWEETED_OFME) {
				ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter(request.getUserId()).getRetweetsOfMe();

				for (int i=0; i<statii.size(); i++) {
					result.add(new InfoTweet(statii.get(i)));
				}
			} else if (request.getType()==FOLLOWERS) {
                ArrayList<Long> followers_id_arraylist = new ArrayList<Long>();
                IDs followers_ids_response;
                long cursor = -1;

                do {
                    followers_ids_response = ConnectionManager.getInstance().getTwitter(request.getUserId()).getFollowersIDs(request.getUser(), cursor);

                    for (long id :followers_ids_response.getIDs()) {
                        followers_id_arraylist.add(id);
                    }
                    cursor = followers_ids_response.getNextCursor();
                } while (followers_ids_response.hasNext());

                Log.d(Utils.TAG + ":Followers","Número de followers: " + followers_id_arraylist.size());

                long[] followers_ids = new long[followers_id_arraylist.size()];
                int index = 0;

                for (Long follower_id : followers_id_arraylist) {
                    followers_ids[index] = follower_id.longValue();
                    index++;
                }

                followers_id_arraylist.clear();

                ResponseList<User> users = null;

                if (followers_ids.length <= 100) {
                    users = ConnectionManager.getInstance().getTwitter(request.getUserId()).lookupUsers(followers_ids);
                } else {
                    int hundred_count = followers_ids.length / 100;
                    hundred_count = 5;

                    for (int i=0; i < hundred_count; i++) {
                        if (users == null)
                            users = ConnectionManager.getInstance().getTwitter(request.getUserId()).lookupUsers(Arrays.copyOfRange(followers_ids,i*100,(i+1)*100-1));
                        else
                            users.addAll(ConnectionManager.getInstance().getTwitter(request.getUserId()).lookupUsers(Arrays.copyOfRange(followers_ids,i*100,(i+1)*100-1)));

                        Log.d(Utils.TAG + ":Followers","Centena de followers número: " + (i + 1));
                    }

                    /*if (followers_ids.length % 100 > 0)
                        users.addAll(ConnectionManager.getInstance().getTwitter(request.getUserId()).lookupUsers(Arrays.copyOfRange(followers_ids,hundred_count*100 + 1,followers_ids.length-1)));*/
                }

                Log.d(Utils.TAG + ":Followers","Número de usuarios: " + users.size());

				for (User user : users) {
					InfoTweet row = new InfoTweet(user);
					result.add(row);
				}
			} else if (request.getType()==FRIENDS) {
                IDs friends_ids = ConnectionManager.getInstance().getTwitter(request.getUserId()).getFriendsIDs(request.getUser(), -1);
                ResponseList<User> users = null;

                if (friends_ids.getIDs().length <= 100) {
                    users = ConnectionManager.getInstance().getTwitter(request.getUserId()).lookupUsers(friends_ids.getIDs());
                } else {
                    int hundred_count = friends_ids.getIDs().length / 100;

                    for (int i=0; i < hundred_count; i++) {
                        if (users == null)
                            users = ConnectionManager.getInstance().getTwitter(request.getUserId()).lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(), i * 100, (i + 1) * 100 - 1));
                        else
                            users.addAll(ConnectionManager.getInstance().getTwitter(request.getUserId()).lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(),i*100,(i+1)*100-1)));
                    }

                    if (friends_ids.getIDs().length % 100 > 0)
                        users.addAll(ConnectionManager.getInstance().getTwitter(request.getUserId()).lookupUsers(Arrays.copyOfRange(friends_ids.getIDs(),hundred_count*100 + 1,friends_ids.getIDs().length-1)));
                }

				for (User user : users) {
					InfoTweet row = new InfoTweet(user);
					result.add(row);
				}
            } else if (request.getType()==TIMELINE) {
                ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter(request.getUserId()).getHomeTimeline();
                for (int i=0; i<statii.size(); i++) {
                    result.add(new InfoTweet(statii.get(i)));
                }
			} else if (request.getType()==LIST) {
				ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter(request.getUserId()).getUserListStatuses(request.getListId(), new Paging(1));
				for (int i=0; i<statii.size(); i++) {
					result.add(new InfoTweet(statii.get(i)));
				}
            } else if (request.getType()==USER_TIMELINE) {
                ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getAnonymousTwitter().getUserTimeline(request.getUser());
                for (int i=0; i<statii.size(); i++) {
                    result.add(new InfoTweet(statii.get(i)));
                }
			}

            response.setInfoTweets(result);
            return response;

		} catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e,e.getMessage());
            return response;
		} catch (OutOfMemoryError e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e,e.getMessage());
            return response;
        } catch (Exception e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e,e.getMessage());
            return response;
		}
    }
}
