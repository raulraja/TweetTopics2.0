package api.loaders;


import adapters.RowResponseList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import api.AsynchronousLoader;
import api.request.LoadMoreTweetDownRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.LoadMoreTweetDownResponse;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.*;

import java.util.ArrayList;

public class LoadMoreTweetDownLoader extends AsynchronousLoader<BaseResponse> {

    private LoadMoreTweetDownRequest request;

    public LoadMoreTweetDownLoader(Context context, LoadMoreTweetDownRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        LoadMoreTweetDownResponse response = new LoadMoreTweetDownResponse();

        response.setPos(request.getPos());

        ArrayList<RowResponseList> tweets = new ArrayList<RowResponseList>();
        try {

            ConnectionManager.getInstance().open(getContext());

            ResponseList<Status> statii = null;

            if (request.getCount()<0) { // se lo descarga to do

                Paging p = new Paging(1, 60);
                p.setMaxId(request.getMaxId());
                if (request.getSinceId()>0) p.setSinceId(request.getSinceId());

                try {
                    statii = ConnectionManager.getInstance().getTwitter().getHomeTimeline(p);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                if (statii!=null) {
                    while (statii.size()%60>=50 || statii.size()%60==0) {
                        p = new Paging(1, 60);
                        if (request.getSinceId()>0) p.setSinceId(request.getSinceId());
                        p.setMaxId(statii.get(statii.size()-1).getId());
                        statii.addAll(ConnectionManager.getInstance().getTwitter().getHomeTimeline(p));
                    }
                }

                /*
				int page = 1;
				Paging p = new Paging(page,60);
				p.setMaxId(maxId);
				if (sinceId>0) p.setSinceId(sinceId);

				ResponseList<twitter4j.Status> statuses = ConnectionManager.getInstance().getTwitter().getHomeTimeline(p);
				statii = statuses;

				while (statuses.size()>0) {
					statuses.clear();
					page++;
					p = new Paging(page,60);
					p.setMaxId(maxId);
					if (sinceId>0) p.setSinceId(sinceId);
					statuses = ConnectionManager.getInstance().getTwitter().getHomeTimeline(p);
					statii.addAll(statuses);
				}
				 */
                /*
                    Paging p = new Paging(1,60);
                    p.setMaxId(maxId);
                    if (sinceId>0) p.setSinceId(sinceId);

                    ResponseList<twitter4j.Status> statuses = ConnectionManager.getInstance().getTwitter().getHomeTimeline(p);

                    while (statuses.size()>1) {
                        //Log.d(Utils.TAG, "tam statuses: " + statuses.size());
                        if (statii==null) {
                            statii = statuses;
                        } else {
                            statuses.remove(0);
                            statii.addAll(statuses);
                        }
                        Paging ps = new Paging(1,60);
                        ps.setMaxId(statii.get(statii.size()-1).getId());
                        if (sinceId>0) ps.setSinceId(sinceId);
                        statuses = ConnectionManager.getInstance().getTwitter().getHomeTimeline(ps);
                    }
                    */
            } else {

                Paging p = new Paging(1,request.getCount());
                p.setMaxId(request.getMaxId());
                if (request.getSinceId()>0) p.setSinceId(request.getSinceId());

                statii = ConnectionManager.getInstance().getTwitter().getHomeTimeline(p);

                if (statii.size()>=request.getCount()-10) {
                    p = new Paging(1, 10);
                    if (request.getSinceId()>0) p.setSinceId(request.getSinceId());
                    p.setMaxId(statii.get(statii.size()-1).getId());
                    if (ConnectionManager.getInstance().getTwitter().getHomeTimeline(p).size()>0) {
                        response.setHasMoreTweets(true);
                    }
                }

                /*
                    Paging p = new Paging(1, count);
                    p.setMaxId(maxId);
                    if (sinceId>0) p.setSinceId(sinceId);

                    statii = ConnectionManager.getInstance().getTwitter().getHomeTimeline(p);

                    if (statii.size()>=count-6) {
                        Paging pa = new Paging(1,10);
                        pa.setMaxId(statii.get(statii.size()-1).getId());
                        if (sinceId>0) p.setSinceId(sinceId);
                        if (ConnectionManager.getInstance().getTwitter().getHomeTimeline().size()>0) {
                            result.hasMoreTweets = true;
                        }
                    }
                    */
            }

            if (statii!=null) {

                if (statii.size()>1) {
                    for (int i=1; i<statii.size(); i++) {
                        tweets.add(new RowResponseList(statii.get(i)));
                    }
                }

                try {
                    DataFramework.getInstance().open(getContext(), Utils.packageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // quitamos la marca
                Entity ent = DataFramework.getInstance().getTopEntity("tweets_user", "tweet_id = '" + Utils.fillZeros("" + request.getMaxId()) + "'", "");
                if (ent!=null) {
                    ent.setValue("has_more_tweets_down", 0);
                    ent.save();
                }


                long fisrtId = 1;
                Cursor c = DataFramework.getInstance().getCursor("tweets_user", new String[]{DataFramework.KEY_ID},
                        null, null, null, null, DataFramework.KEY_ID + " desc", "1");
                if (!c.moveToFirst()) {
                    c.close();
                    fisrtId = 1;
                } else {
                    long Id = c.getInt(0) + 1;
                    c.close();
                    fisrtId = Id;
                }

                //DataFramework.getInstance().getDB().beginTransaction();

                try {
                    boolean isFirst = true;
                    for (int i=statii.size()-1; i>=0; i--) {
                        User u = statii.get(i).getUser();
                        if (u!=null) {
                            ContentValues args = new ContentValues();
                            args.put(DataFramework.KEY_ID, "" + fisrtId);
                            args.put("type_id", TweetTopicsCore.TIMELINE); // solo lo hace en el TL
                            args.put("user_tt_id", "" + request.getUserId());
                            if (u.getProfileImageURL()!=null) {
                                args.put("url_avatar", u.getProfileImageURL().toString());
                            } else {
                                args.put("url_avatar", "");
                            }
                            args.put("username", u.getScreenName());
                            args.put("fullname", u.getName());
                            args.put("user_id", "" + u.getId());
                            args.put("tweet_id", Utils.fillZeros("" + statii.get(i).getId()));
                            args.put("source", statii.get(i).getSource());
                            args.put("to_username", statii.get(i).getInReplyToScreenName());
                            args.put("to_user_id", "" + statii.get(i).getInReplyToUserId());
                            args.put("date", String.valueOf(statii.get(i).getCreatedAt().getTime()));
                            if (statii.get(i).getRetweetedStatus()!=null) {
                                args.put("is_retweet", 1);
                                args.put("retweet_url_avatar", statii.get(i).getRetweetedStatus().getUser().getProfileImageURL().toString());
                                args.put("retweet_username", statii.get(i).getRetweetedStatus().getUser().getScreenName());
                                args.put("retweet_source", statii.get(i).getRetweetedStatus().getSource());
                                String t = Utils.getTwitLoger(statii.get(i).getRetweetedStatus());
                                args.put("text", t.equals("")?statii.get(i).getRetweetedStatus().getText():t);
                                args.put("is_favorite", 0);
                            } else {
                                String t = Utils.getTwitLoger(statii.get(i));
                                args.put("text", t.equals("")?statii.get(i).getText():t);

                                if (statii.get(i).isFavorited()) {
                                    args.put("is_favorite", 1);
                                }
                            }

                            if (statii.get(i).getGeoLocation()!=null) {
                                args.put("latitude", statii.get(i).getGeoLocation().getLatitude());
                                args.put("longitude", statii.get(i).getGeoLocation().getLongitude());
                            }
                            args.put("reply_tweet_id", statii.get(i).getInReplyToStatusId());

                            if (response.isHasMoreTweets() && isFirst) args.put("has_more_tweets_down", 1);

                            DataFramework.getInstance().getDB().insert("tweets_user", null, args);
                            fisrtId++;

                            if (isFirst) isFirst = false;
                        }

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //DataFramework.getInstance().getDB().endTransaction();
                }

                DataFramework.getInstance().close();

                response.setTweets(tweets);

            }

            return response;

        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        }



    }

}
