package com.javielinux.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.infos.InfoSaveTweets;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import twitter4j.*;

public class EntityTweetUser extends Entity {

	private String mErrorLastQuery = "";
	
	private int tweet_type = 0;
	private long mLastIdNotification = 0;

	public EntityTweetUser(Long id, int type) {
		super("users", id);
		this.tweet_type = type;
	}
	
	public int getType() {
		return tweet_type;
	}
	
	public String getErrorLastQuery() {
		return mErrorLastQuery;
	}
	
	public String getFieldLastId() {

        switch (tweet_type) {
            case TweetTopicsUtils.TWEET_TYPE_TIMELINE:
                return "last_timeline_id";
            case TweetTopicsUtils.TWEET_TYPE_MENTIONS:
                return "last_mention_id";
            case TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES:
                return "last_direct_id";
            case TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES:
                return "last_sent_direct_id";
        }

        return "";

	}

    public void saveLastId(long id) {
        if (tweet_type!=TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES) {
            try {
                ContentValues args = new ContentValues();
                args.put(getFieldLastId(), id + "");
                DataFramework.getInstance().getDB().update(getTable(), args, DataFramework.KEY_ID + "=" + getId(), null);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }
	
	
	public int getValueNewCount() {
		return DataFramework.getInstance().getEntityListCount("tweets_user", "type_id=" + tweet_type 
   				+ " AND user_tt_id=" + getId() + " AND tweet_id >'" + Utils.fillZeros(""+getString(getFieldLastId()))+"'");
	}


    private int getUnreadTweetsCount(int column_type, Entity user, Entity search) {
        int tweetsCount = 0;

        switch (column_type) {
            case TweetTopicsUtils.COLUMN_TIMELINE:
                tweetsCount = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_TIMELINE + " AND user_tt_id=" + user.getId() + " AND tweet_id >'" + Utils.fillZeros("" + user.getString("last_timeline_id")) + "'");
                break;
            case TweetTopicsUtils.COLUMN_MENTIONS:
                tweetsCount = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_MENTIONS + " AND user_tt_id=" + user.getId() + " AND tweet_id >'" + Utils.fillZeros("" + user.getString("last_mention_id")) + "'");
                break;
            case TweetTopicsUtils.COLUMN_DIRECT_MESSAGES:
                tweetsCount = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES + " AND user_tt_id=" + user.getId() + " AND tweet_id >'" + Utils.fillZeros("" + user.getString("last_direct_id")) + "'");
                break;
            case TweetTopicsUtils.COLUMN_SEARCH:
                EntitySearch ent = new EntitySearch(search.getId());
                tweetsCount = ent.getValueNewCount();
                //tweetsCount = DataFramework.getInstance().getEntityListCount("search", "tweet_id >'" + Utils.fillZeros("" + user.getString("last_direct_id")) + "'");
                //if (search.getLong("last_tweet_id") < search.getLong("last_tweet_id_notifications"))
                //    tweetsCount = search.getInt("new_tweets_count");
                break;
        }

        return tweetsCount;
    }
	
	public int getValueCountFromId(long id) {
		return DataFramework.getInstance().getEntityListCount("tweets_user", "type_id=" + tweet_type 
   				+ " AND user_tt_id=" + getId() + " AND tweet_id >'" + Utils.fillZeros(""+id)+"'");
	}
	
	public void saveValueLastIdFromDB() {

        String where ="";
        Entity entity;
        
        switch (tweet_type) {
            case TweetTopicsUtils.TWEET_TYPE_TIMELINE:
                where = "type_id = " + TweetTopicsUtils.TWEET_TYPE_TIMELINE + " AND user_tt_id=" + getId();
                entity = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc");

                if (entity!=null) {
                    long id = entity.getLong("tweet_id");
                    setValue("last_timeline_id", id+"");
                }
                break;
            case TweetTopicsUtils.TWEET_TYPE_MENTIONS:
                where = "type_id = " + TweetTopicsUtils.TWEET_TYPE_MENTIONS + " AND user_tt_id=" + getId();
                entity = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc");

                if (entity!=null) {
                    long id = entity.getLong("tweet_id");
                    setValue("last_mention_id", id+"");
                }
                break;
            case TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES:
			    where = "type_id = " + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES + " AND user_tt_id=" + getId();
			    entity = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc");

			    if (entity!=null) {
				    long id = entity.getLong("tweet_id");
				    setValue("last_direct_id", id+"");
			    }
                break;
            case TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES:
			    where = "type_id = " + TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES + " AND user_tt_id="+getId();
			    entity = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc");

			    if (entity!=null) {
				    long id = entity.getLong("tweet_id");
				    setValue("last_sent_direct_id", id+"");
			    }
                break;
        }
        
        save();
	}
	
	public void setValueLastId(String id) {
		setValue(getFieldLastId(), id);
	}
		
	public long getValueLastId() {
		return getLong(getFieldLastId());
	}
		
	public long getValueLastIdNotification() {
		return mLastIdNotification;//getLong(getFieldLastId(true));
	}
		
	public String getTypeText() {

        switch (tweet_type) {
		    case TweetTopicsUtils.TWEET_TYPE_TIMELINE:
                return "timeline";
            case TweetTopicsUtils.TWEET_TYPE_MENTIONS:
			    return "menciones";
            case TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES:
			    return "directos";
            case TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES:
			    return "directos enviados";
		}

        return "";
	}
	
	public InfoSaveTweets saveTweets(Context context, Twitter twitter) {

		InfoSaveTweets out = new InfoSaveTweets();

		try {
			String where = "type_id = " + tweet_type + " AND user_tt_id="+getId();
			
			int nResult = DataFramework.getInstance().getEntityListCount("tweets_user", where);
			if (nResult > 0) mLastIdNotification = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc").getLong("tweet_id");
			
			boolean breakTimeline = false;

            PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

            int maxDownloadTweet = Integer.parseInt(pref.getString("prf_n_max_download", "60"));
			
			ResponseList<twitter4j.Status> statii = null;
			ResponseList<twitter4j.DirectMessage> directs = null;
			
			if (mLastIdNotification>0) {
				if (tweet_type == TweetTopicsUtils.TWEET_TYPE_TIMELINE) {

                    if (maxDownloadTweet <= 0) { // se descargan todos los tweets

                        Paging p = new Paging(1, 60);
                        p.setSinceId(mLastIdNotification);

                        try {
                            statii = twitter.getHomeTimeline(p);
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }

                        while (statii.size()%60>=50 || statii.size()%60==0) {
                            p = new Paging(1, 60);
                            p.setSinceId(mLastIdNotification);
                            p.setMaxId(statii.get(statii.size()-1).getId());
                            statii.addAll(twitter.getHomeTimeline(p));
                        }

                    } else {
                        Paging p = new Paging(1, maxDownloadTweet);
                        p.setSinceId(mLastIdNotification);

                        try {
                            statii = twitter.getHomeTimeline(p);
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }

                        if (statii.size()>=maxDownloadTweet-10) {
                            p = new Paging(1, 10);
                            p.setSinceId(mLastIdNotification);
                            p.setMaxId(statii.get(statii.size()-1).getId());
                            if (twitter.getHomeTimeline().size()>0) {
                                breakTimeline = true;
                            }
                        }
                    }

				} else if (tweet_type == TweetTopicsUtils.TWEET_TYPE_MENTIONS) {
					
					ResponseList<twitter4j.Status> statuses = twitter.getMentions(new Paging(mLastIdNotification));
					
		            while (statuses.size()>0) {
		            	if (statii==null) {
		            		statii = statuses;
		            	} else {
		            		statii.addAll(statuses);
		            	}
                        mLastIdNotification = statuses.get(0).getId();
		            	statuses = twitter.getMentions(new Paging(mLastIdNotification));
		            }
					
				} else if (tweet_type == TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES) {
					
					ResponseList<twitter4j.DirectMessage> directses = twitter.getDirectMessages(new Paging(mLastIdNotification));
					
		            while (directses.size()>0) {
		            	if (directs==null) {
		            		directs = directses;
		            	} else {
		            		directs.addAll(directses);
		            	}
                        mLastIdNotification = directses.get(0).getId();
		            	directses = twitter.getDirectMessages(new Paging(mLastIdNotification));
		            }
					
				} else if (tweet_type == TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES) {
					
					int page = 1;
					ResponseList<twitter4j.DirectMessage> directses = twitter.getSentDirectMessages(new Paging(page, mLastIdNotification));
					
		            while (directses.size()>0) {
		            	if (directs==null) {
		            		directs = directses;
		            	} else {
		            		directs.addAll(directses);
		            	}
		            	page++;
		            	directses = twitter.getSentDirectMessages(new Paging(page, mLastIdNotification));
		            }

				}
			} else {
				try {
					Log.d(Utils.TAG, "Primera carga de " + getTypeText());
					if (tweet_type == TweetTopicsUtils.TWEET_TYPE_TIMELINE) {
						statii = twitter.getHomeTimeline(new Paging(1, 40));
					} else if (tweet_type == TweetTopicsUtils.TWEET_TYPE_MENTIONS) {
						statii = twitter.getMentions(new Paging(1, 40));
					} else if (tweet_type == TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES) {
						directs = twitter.getDirectMessages();
					} else if (tweet_type == TweetTopicsUtils.TWEET_TYPE_SENT_DIRECTMESSAGES) {
						directs = twitter.getSentDirectMessages();
					}
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
			

			// guardar statii
			
			if (statii!=null) {
				
				if (statii.size()>0) {
					out.setNewMessages(statii.size());
					out.setNewerId(statii.get(0).getId());
					out.setOlderId(statii.get(statii.size()-1).getId());
									
					Log.d(Utils.TAG,statii.size()+" mensajes nuevos en " +  getTypeText() + " de "+getString("name"));

					long nextId = 1;
					Cursor c = DataFramework.getInstance().getCursor("tweets_user", new String[]{DataFramework.KEY_ID},
							null, null, null, null, DataFramework.KEY_ID + " desc", "1");
					if (!c.moveToFirst()) {
						c.close();
						nextId = 1;
					} else {
						long Id = c.getInt(0) + 1;
						c.close();
						nextId = Id;
					}

					DataFramework.getInstance().getDB().beginTransaction();
					
					try {
						boolean isFirst = true;
						for (int i=statii.size()-1; i>=0; i--) {
							User u = statii.get(i).getUser();
							if (u!=null) {
								ContentValues args = new ContentValues();
								args.put(DataFramework.KEY_ID, "" + nextId);
								args.put("type_id", tweet_type);
								args.put("user_tt_id", "" + getId());
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
									if (t.equals("")) {
										args.put("text", statii.get(i).getRetweetedStatus().getText());
										args.put("text_urls", Utils.getTextURLs(statii.get(i).getRetweetedStatus()));
									} else {
										args.put("text", t);
									}
									args.put("is_favorite", 0);
								} else {
									String t = Utils.getTwitLoger(statii.get(i));
									if (t.equals("")) {
										args.put("text", statii.get(i).getText());
										args.put("text_urls", Utils.getTextURLs(statii.get(i)));
									} else {
										args.put("text", t);
									}
									
									if (statii.get(i).isFavorited()) {
										args.put("is_favorite", 1);
									}
								}
								
								if (statii.get(i).getGeoLocation()!=null) {
									args.put("latitude", statii.get(i).getGeoLocation().getLatitude());
									args.put("longitude", statii.get(i).getGeoLocation().getLongitude());
								}
								args.put("reply_tweet_id", statii.get(i).getInReplyToStatusId());
								
								if (breakTimeline && isFirst) args.put("has_more_tweets_down", 1);
		
								DataFramework.getInstance().getDB().insert("tweets_user", null, args);

                                out.addId(nextId);

								nextId++;
								
								if (isFirst) isFirst = false;
							}
		
						}
						
						// finalizar
						
						int total = nResult+statii.size();

                        if (total>Utils.MAX_ROW_BYSEARCH && getValueNewCount()<Utils.MAX_ROW_BYSEARCH || total>Utils.MAX_ROW_BYSEARCH_FORCE) {
                            try {
                                Log.d(Utils.TAG,"Limpiando base de datos de " + getTypeText() + " actualmente " + total + " registros");
                                String date = DataFramework.getInstance().getEntityList("tweets_user", "type_id=" + tweet_type + " and user_tt_id="+getId(), "date desc").get(Utils.MAX_ROW_BYSEARCH).getString("date");
                                String sqldelete = "DELETE FROM tweets_user WHERE type_id=" + tweet_type + " and user_tt_id="+getId() + " AND date  < '" + date + "'";
                                DataFramework.getInstance().getDB().execSQL(sqldelete);
                            } catch (OutOfMemoryError e) { }
                        }
						
						DataFramework.getInstance().getDB().setTransactionSuccessful();
						
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						DataFramework.getInstance().getDB().endTransaction();
					}

				}
				
			}
			
			// guardar directs
			
			if (directs!=null) {
				if (directs.size()>0) {
					out.setNewMessages(directs.size());
					out.setNewerId(directs.get(0).getId());
					out.setOlderId(directs.get(directs.size()-1).getId());					
					
					Log.d(Utils.TAG,directs.size()+" mensajes directos a "+getString("name"));
					
					long nextId = 1;
					Cursor c = DataFramework.getInstance().getCursor("tweets_user", new String[]{DataFramework.KEY_ID}, 
							null, null, null, null, DataFramework.KEY_ID + " desc", "1");
					if (!c.moveToFirst()) {
						c.close();
						nextId = 1;
					} else {
						long Id = c.getInt(0) + 1;
						c.close();
						nextId = Id;
					}
					
					DataFramework.getInstance().getDB().beginTransaction();
					
					try {
						for (int i=directs.size()-1; i>=0; i--) {
							User u = directs.get(i).getSender();
							if (u!=null) {
								ContentValues args = new ContentValues();
								args.put(DataFramework.KEY_ID, "" + nextId);
								args.put("type_id", tweet_type);
								args.put("user_tt_id", "" + getId());
								if (u.getProfileImageURL()!=null) {
									args.put("url_avatar", u.getProfileImageURL().toString());
								} else {
									args.put("url_avatar", "");
								}
								args.put("username", u.getScreenName());
								args.put("fullname", u.getName());
								args.put("user_id", "" + u.getId());
								args.put("tweet_id", Utils.fillZeros("" + directs.get(i).getId()));
								args.put("source", "");
								args.put("to_username", directs.get(i).getRecipientScreenName());
								args.put("to_user_id", "" + directs.get(i).getRecipientId());
								args.put("date", String.valueOf(directs.get(i).getCreatedAt().getTime()));
								args.put("text", directs.get(i).getText());
								
								DataFramework.getInstance().getDB().insert("tweets_user", null, args);

                                out.addId(nextId);

								Log.d(Utils.TAG, "getRecipientScreenName: "+directs.get(i).getRecipientScreenName());
								
								nextId++;
							}
			
						}
						
						// finalizar
											
						int total = nResult+directs.size();
		
						if (total>Utils.MAX_ROW_BYSEARCH && getValueNewCount()<Utils.MAX_ROW_BYSEARCH) {
							Log.d(Utils.TAG,"Limpiando base de datos de " + getTypeText() + " actualmente " + total + " registros");
							String date = DataFramework.getInstance().getEntityList("tweets_user", "type_id=" + tweet_type + " and user_tt_id="+getId(), "date desc").get(Utils.MAX_ROW_BYSEARCH).getString("date");
							String sqldelete = "DELETE FROM tweets_user WHERE type_id=" + tweet_type + " and user_tt_id="+getId() + " AND date  < '" + date + "'";
							DataFramework.getInstance().getDB().execSQL(sqldelete);
						}
						
						DataFramework.getInstance().getDB().setTransactionSuccessful();
						
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						DataFramework.getInstance().getDB().endTransaction();
					}
					
				}	
				
			}
		} catch (TwitterException e) {
			e.printStackTrace();
    		RateLimitStatus rate = e.getRateLimitStatus();
    		if (rate!=null) {
    			out.setError(Utils.LIMIT_ERROR);
    			out.setRate(rate);
    		} else {
    			out.setError(Utils.UNKNOWN_ERROR);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			out.setError(Utils.UNKNOWN_ERROR);
		}
		return out;
	}
	

}
