package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadTypeStatusRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadTypeStatusResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.*;

import java.util.ArrayList;

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
                ResponseList<User> users = ConnectionManager.getInstance().getAnonymousTwitter().lookupUsers(request.getUserIdList());

				for (User user : users) {
					InfoTweet row = new InfoTweet(user);
					result.add(row);
				}
			} else if (request.getType()==FRIENDS) {
                ResponseList<User> users = ConnectionManager.getInstance().getAnonymousTwitter().lookupUsers(request.getUserIdList());

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
				ResponseList<twitter4j.Status> statii;

                if (request.getUserId() < 0) {
                    statii = ConnectionManager.getInstance().getAnonymousTwitter().getUserListStatuses(request.getListId(), new Paging(1));
                } else {
                    statii = ConnectionManager.getInstance().getTwitter(request.getUserId()).getUserListStatuses(request.getListId(), new Paging(1));
                }

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
