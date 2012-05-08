package database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics.R;
import com.javielinux.tweettopics.TweetTopicsCore;
import com.javielinux.tweettopics.Utils;
import infos.InfoSaveTweets;
import twitter4j.*;

public class EntityTweetUser extends Entity {
	/*
	public static int TYPE_SICELASTID_NOUSE = 0;
	public static int TYPE_SICELASTID_NORMAL = 1;
	public static int TYPE_SICELASTID_NOTIFICATIONS = 2;
	*/

	private String mErrorLastQuery = "";
	
	private int mType = 0;
	private long mLastIdNotification = 0;

	public EntityTweetUser(Long id, int type) {
		super("users", id);
		mType = type;
	}
	
	public int getType() {
		return mType;
	}
	
	public String getErrorLastQuery() {
		return mErrorLastQuery;
	}
	
	public String getFieldLastId() {
		String out = "";
		if (mType==TweetTopicsCore.TIMELINE) {
			out = "last_timeline_id";
		} else if (mType==TweetTopicsCore.MENTIONS) {
			out = "last_mention_id";
		} else if (mType==TweetTopicsCore.DIRECTMESSAGES) {
			out = "last_direct_id";
		} else if (mType==TweetTopicsCore.SENT_DIRECTMESSAGES) {
			out = "last_sent_direct_id";
		}
		return out;
	}
	
	
	public int getValueNewCount() {
		return DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + mType 
   				+ " AND user_tt_id="+getId() + " AND tweet_id >'" + Utils.fillZeros(""+getString(getFieldLastId()))+"'");
	}
	
	public int getValueCountFromId(long id) {
		return DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + mType 
   				+ " AND user_tt_id="+getId() + " AND tweet_id >'" + Utils.fillZeros(""+id)+"'");
	}
	
	public void saveValueLastIdFromDB() {
		/*
		if (mType==TweetTopicsCore.TIMELINE) {
			String where = "type_id = " + TweetTopicsCore.TIMELINE + " AND user_tt_id="+getId();
			Entity ent = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc");
			if (ent!=null) {
				long id = ent.getLong("tweet_id");
				setValue("last_timeline_id", id+"");
			}
		} else if (mType==TweetTopicsCore.MENTIONS) {
			String where = "type_id = " + TweetTopicsCore.MENTIONS + " AND user_tt_id="+getId();
			Entity ent = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc");
			if (ent!=null) {
				long id = ent.getLong("tweet_id");
				setValue("last_mention_id", id+"");
			}
		} else if (mType==TweetTopicsCore.DIRECTMESSAGES) {
			String where = "type_id = " + TweetTopicsCore.DIRECTMESSAGES + " AND user_tt_id="+getId();
			Entity ent = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc");
			if (ent!=null) {
				long id = ent.getLong("tweet_id");
				setValue("last_direct_id", id+"");
			}
			where = "type_id = " + TweetTopicsCore.SENT_DIRECTMESSAGES + " AND user_tt_id="+getId();
			ent = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc");
			if (ent!=null) {
				long id = ent.getLong("tweet_id");
				setValue("last_sent_direct_id", id+"");
			}
		}
		save();*/
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
		String out = "";
		if (mType==TweetTopicsCore.TIMELINE) {
			out = "timeline";
		} else if (mType==TweetTopicsCore.MENTIONS) {
			out = "menciones";
		} else if (mType==TweetTopicsCore.DIRECTMESSAGES) {
			out = "directos";
		} else if (mType==TweetTopicsCore.SENT_DIRECTMESSAGES) {
			out = "directos enviados";
		}
		return out;
	}
	
	public InfoSaveTweets saveTweets(Context cnt, Twitter twitter, boolean saveNotifications) {
		InfoSaveTweets out = new InfoSaveTweets();
		try {
			
			//ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("tweets_user", "type_id = " + mType + " AND user_tt_id="+getId(), "date desc");
			String where = "type_id = " + mType + " AND user_tt_id="+getId();
			
			int nResult = DataFramework.getInstance().getEntityListCount("tweets_user", where);
			if (nResult>0) mLastIdNotification = DataFramework.getInstance().getTopEntity("tweets_user", where, "date desc").getLong("tweet_id");
			
			boolean breakTimeline = false;

            PreferenceManager.setDefaultValues(cnt, R.xml.preferences, false);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(cnt);

            int maxDownloadTweet = Integer.parseInt(pref.getString("prf_n_max_download", "60"));
			
			ResponseList<twitter4j.Status> statii = null;
			ResponseList<twitter4j.DirectMessage> directs = null;
			
			if (mLastIdNotification>0) {
				if (mType==TweetTopicsCore.TIMELINE) {	

                    if (maxDownloadTweet<=0) { // se descargan todos los tweets

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
					/*
					Paging p = new Paging(1, MAX_DOWNLOAD_TWEETS);
					p.setSinceId(mLastIdNotification);

					try {
						statii = twitter.getHomeTimeline(p);
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					
					// comprobar si hay más tweets
					if (statii.size()>=MAX_DOWNLOAD_TWEETS-6) {
						Paging pa = new Paging(1,10);
						pa.setMaxId(statii.get(statii.size()-1).getId());
						p.setSinceId(mLastIdNotification);
						if (twitter.getHomeTimeline().size()>0) {
							breakTimeline = true;
						}
					}

		            */
				} else if (mType==TweetTopicsCore.MENTIONS) {
					
					int page = 1;
					ResponseList<twitter4j.Status> statuses = twitter.getMentions(new Paging(page, mLastIdNotification));
					
		            while (statuses.size()>0) {
		            	if (statii==null) {
		            		statii = statuses;
		            	} else {
		            		statii.addAll(statuses);
		            	}
		            	page++;
		            	statuses = twitter.getMentions(new Paging(page, mLastIdNotification));
		            }
					
				} else if (mType==TweetTopicsCore.DIRECTMESSAGES) {
					
					int page = 1;
					ResponseList<twitter4j.DirectMessage> directses = twitter.getDirectMessages(new Paging(page, mLastIdNotification));
					
		            while (directses.size()>0) {
		            	if (directs==null) {
		            		directs = directses;
		            	} else {
		            		directs.addAll(directses);
		            	}
		            	page++;
		            	directses = twitter.getDirectMessages(new Paging(page, mLastIdNotification));
		            }
					
				} else if (mType==TweetTopicsCore.SENT_DIRECTMESSAGES) {
					
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
					if (mType==TweetTopicsCore.TIMELINE) {
						statii = twitter.getHomeTimeline(new Paging(1, 40));
					} else if (mType==TweetTopicsCore.MENTIONS) {
						statii = twitter.getMentions(new Paging(1, 40));
					} else if (mType==TweetTopicsCore.DIRECTMESSAGES) {
						directs = twitter.getDirectMessages();
					} else if (mType==TweetTopicsCore.SENT_DIRECTMESSAGES) {
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
								
					DataFramework.getInstance().getDB().beginTransaction();
					
					try {
						boolean isFirst = true;
						for (int i=statii.size()-1; i>=0; i--) {
							User u = statii.get(i).getUser();
							if (u!=null) {
								ContentValues args = new ContentValues();
								args.put(DataFramework.KEY_ID, "" + fisrtId);
								args.put("type_id", mType);
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
								fisrtId++;
								
								if (isFirst) isFirst = false;
							}
		
						}
						
						// finalizar
						
						int total = nResult+statii.size();
		
						if (total>Utils.MAX_ROW_BYSEARCH && getValueNewCount()<Utils.MAX_ROW_BYSEARCH) {
							Log.d(Utils.TAG,"Limpiando base de datos de " + getTypeText() + " actualmente " + total + " registros");
							String date = DataFramework.getInstance().getEntityList("tweets_user", "type_id=" + mType + " and user_tt_id="+getId(), "date desc").get(Utils.MAX_ROW_BYSEARCH).getString("date");
							String sqldelete = "DELETE FROM tweets_user WHERE type_id=" + mType + " and user_tt_id="+getId() + " AND date  < '" + date + "'";
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
			
			// guardar directs
			
			if (directs!=null) {
				if (directs.size()>0) {
					out.setNewMessages(directs.size());
					out.setNewerId(directs.get(0).getId());
					out.setOlderId(directs.get(directs.size()-1).getId());					
					
					Log.d(Utils.TAG,directs.size()+" mensajes directos a "+getString("name"));
					
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
					
					DataFramework.getInstance().getDB().beginTransaction();
					
					try {
						for (int i=directs.size()-1; i>=0; i--) {
							User u = directs.get(i).getSender();
							if (u!=null) {
								ContentValues args = new ContentValues();
								args.put(DataFramework.KEY_ID, "" + fisrtId);
								args.put("type_id", mType);
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
								
								Log.d(Utils.TAG, "getRecipientScreenName: "+directs.get(i).getRecipientScreenName());
								
								fisrtId++;
							}
			
						}
						
						// finalizar
											
						int total = nResult+directs.size();
		
						if (total>Utils.MAX_ROW_BYSEARCH && getValueNewCount()<Utils.MAX_ROW_BYSEARCH) {
							Log.d(Utils.TAG,"Limpiando base de datos de " + getTypeText() + " actualmente " + total + " registros");
							String date = DataFramework.getInstance().getEntityList("tweets_user", "type_id=" + mType + " and user_tt_id="+getId(), "date desc").get(Utils.MAX_ROW_BYSEARCH).getString("date");
							String sqldelete = "DELETE FROM tweets_user WHERE type_id=" + mType + " and user_tt_id="+getId() + " AND date  < '" + date + "'";
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
