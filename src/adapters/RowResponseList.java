package adapters;

import android.content.Context;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics.Utils;
import com.javielinux.tweettopics.Utils.URLContent;
import infos.InfoTweet;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Date;

public class RowResponseList {
	
	static public int TYPE_ENTITY = 0;
	static public int TYPE_TWEET = 1;
	static public int TYPE_STATUS = 2;
	static public int TYPE_PUB = 3;
	static public int TYPE_DIRECTMESSAGE = 4;
	static public int TYPE_USER = 5;
	static public int TYPE_MORE_TWEETS = 6;
	
	private int type = TYPE_PUB;
	
	private Entity mEntity = null;
	private Tweet mTweet =  null;
	private Status mStatus=  null;
	private DirectMessage mDirect = null;
	private User mUser = null;
	private boolean mForceNoRetweet = false;
	
	private String mHTMLTextFinal = "";
	private String mTextFinal = "";
	
	private ArrayList<URLContent> urls = null;
	
	private boolean read = true;
	
	private boolean lastRead = false;
	
	private boolean favorited = false;
	
	public long getTweetId() {
        try {
            if (type == TYPE_ENTITY) {
                return mEntity.getLong("tweet_id");
            }
            if (type == TYPE_TWEET) {
                return mTweet.getId();
            }
            if (type == TYPE_STATUS) {
                return mStatus.getId();
            }
            if (type == TYPE_USER) {
                return mUser.getStatus().getId();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
		return 0;
	}
	
	public RowResponseList(Entity ent) {
		type = TYPE_ENTITY;
		this.mEntity = ent;
		favorited = false;
		if (mEntity.isAttribute("is_favorite")) favorited = mEntity.getInt("is_favorite")==1?true:false;
	}

	public RowResponseList(Tweet tweet) {
		type = TYPE_TWEET;
		this.mTweet = tweet;
		favorited = false;
	}
	
	public RowResponseList(Status status) {
		type = TYPE_STATUS;
		this.mStatus = status;
		favorited = mStatus.isFavorited();
	}
	
	public RowResponseList(DirectMessage direct) {
		type = TYPE_DIRECTMESSAGE;
		this.mDirect = direct;
		favorited = false;
	}
	
	public RowResponseList(User user) {
		type = TYPE_USER;
		this.mUser = user;
		favorited = false;
	}
	
	public RowResponseList(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
	public int getTypeColumn() {
		if (type == TYPE_ENTITY) {
			return mEntity.getInt("type_id");
		}
		return -1;
	}

	public Tweet getTweet() {
		return mTweet;
	}


	public Entity getEntity() {
		return mEntity;
	}
	
	public Status getStatus() {
		return mStatus;
	}
	
	public User getUser() {
		return mUser;
	}
	
	public String getTwitterURL() {
		return InfoTweet.START_URL_TWITTER + getUsername().toLowerCase() + "/status/" + getTweetId();
	}

	public void setLastRead(boolean lastRead) {
		this.lastRead = lastRead;
	}

	public boolean isLastRead() {
		return lastRead;
	}
	
	public boolean hasMoreTweetDown() {
		if (type == TYPE_ENTITY) {
			if (mEntity.isAttribute("has_more_tweets_down")) {
				if (mEntity.getInt("has_more_tweets_down")==1) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getToUsername() {
		if (type == TYPE_ENTITY) {
			return mEntity.getString("to_username");
		} else if (type == TYPE_TWEET) {
			return mTweet.getToUser();
		} else if (type == TYPE_STATUS) {
			return mStatus.getInReplyToScreenName();
		} else if (type == TYPE_DIRECTMESSAGE) {
			return mDirect.getRecipientScreenName();
		}
		return "";
	}
	
	public long getInReplyToUserId() {
		try {
			if (type == TYPE_ENTITY) {
				if (mEntity.isAttribute("to_user_id")) {
					return mEntity.getLong("to_user_id");
				}
			} else if (type == TYPE_STATUS) {
				return mStatus.getInReplyToStatusId();
			} else if (type == TYPE_TWEET) {
				return mTweet.getToUserId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getUsername() {
		if (type == TYPE_ENTITY) {
			return mEntity.getString("username");
		} else if (type == TYPE_TWEET) {
			return mTweet.getFromUser();
		} else if (type == TYPE_STATUS) {
			return mStatus.getUser().getScreenName();
		} else if (type == TYPE_DIRECTMESSAGE) {
			return mDirect.getSenderScreenName();
		} else if (type == TYPE_USER) {
			return mUser.getScreenName();
		}
		return "";
	}
	
	public String getFullname() {
		if (type == TYPE_ENTITY) {
			return mEntity.getString("fullname");
		} else if (type == TYPE_TWEET) {
			return mTweet.getFromUser();
		} else if (type == TYPE_STATUS) {
			return mStatus.getUser().getName();
		} else if (type == TYPE_DIRECTMESSAGE) {
			return mDirect.getSender().getName();
		} else if (type == TYPE_USER) {
			return mUser.getName();
		}
		return "";
	}
	
	public String getText() {
		if (type == TYPE_ENTITY) {
			return mEntity.getString("text");
		} else if (type == TYPE_TWEET) {
			return mTweet.getText();
		} else if (type == TYPE_STATUS) {
			return mStatus.getText();
		} else if (type == TYPE_DIRECTMESSAGE) {
			return mDirect.getText();
		} else if (type == TYPE_USER) {
			try {
				return mUser.getStatus().getText();
			} catch (Exception e) {
			}
		}
		return "";
	}
	
	public String getTextURLs() {
		if (type == TYPE_ENTITY) {
			if (mEntity.isAttribute("text_urls")) {
				return mEntity.getString("text_urls");
			}
		} else if (type == TYPE_TWEET) {
			return mTweet.getText();
		} else if (type == TYPE_STATUS) {
			return Utils.getTextURLs(mStatus);
		} else if (type == TYPE_DIRECTMESSAGE) {
			return mDirect.getText();
		} else if (type == TYPE_USER) {
			try {
				return Utils.getTextURLs(mUser.getStatus());
			} catch (Exception e) {
			}
		}
		return "";
	}
	
	public ArrayList<URLContent> getContentURLs() {
		if (urls==null) {
			urls = Utils.urls2content(getTextURLs());
		}
		return urls;
	}
	
	
	public String getSource() {
		if (type == TYPE_ENTITY) {
			return mEntity.getString("source");
		} else if (type == TYPE_TWEET) {
			return mTweet.getSource();
		} else if (type == TYPE_STATUS) {
			return mStatus.getSource();
		} else if (type == TYPE_USER) {
			try {
				return mUser.getStatus().getSource();
			} catch (Exception e) {
			}
		}
		return "";
	}
	
	public String getTime(Context cnt) {
		try {
			if (type == TYPE_ENTITY) {
				Date d = new Date();
				d.setTime(mEntity.getLong("date"));
				return Utils.timeFromTweet(cnt, d);
			} else if (type == TYPE_TWEET) {
				return Utils.timeFromTweet(cnt, mTweet.getCreatedAt());
			} else if (type == TYPE_STATUS) {
				return Utils.timeFromTweet(cnt, mStatus.getCreatedAt());
			} else if (type == TYPE_DIRECTMESSAGE) {
				return Utils.timeFromTweet(cnt, mDirect.getCreatedAt());
			} else if (type == TYPE_USER) {
				return Utils.timeFromTweet(cnt, mUser.getStatus().getCreatedAt());
			}
		} catch (Exception e) {
			
		}
		return "";
	}
	
	public Date getDate() {
		try {
			if (type == TYPE_ENTITY) {
				Date d = new Date();
				d.setTime(mEntity.getLong("date"));
				return d;
			} else if (type == TYPE_TWEET) {
				return mTweet.getCreatedAt();
			} else if (type == TYPE_STATUS) {
				return mStatus.getCreatedAt();
			} else if (type == TYPE_DIRECTMESSAGE) {
				return mDirect.getCreatedAt();
			} else if (type == TYPE_USER) {
				return mUser.getStatus().getCreatedAt();
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	public String getUrlAvatar() {
		if (type == TYPE_ENTITY) {
			return mEntity.getString("url_avatar");
		} else if (type == TYPE_TWEET) {
			return mTweet.getProfileImageUrl();
		} else if (type == TYPE_STATUS) {
			if (mStatus.getUser().getProfileImageURL()!=null) {
				return mStatus.getUser().getProfileImageURL().toString();
			} else {
				return "";
			}
		} else if (type == TYPE_DIRECTMESSAGE) {
			return mDirect.getSender().getProfileImageURL().toString();
		} else if (type == TYPE_USER) {
			return mUser.getProfileImageURL().toString();
		}
		return "";
	}
	
	public boolean hasConversation() {
		try {
			if (type == TYPE_ENTITY) {
				if (mEntity.isAttribute("reply_tweet_id")) {
					if (mEntity.getLong("reply_tweet_id")>0) {
						return true;
					}
				}
			} else if (type == TYPE_STATUS) {
				if (mStatus.getInReplyToStatusId()>0) {
					return true;
				}
			} else if (type == TYPE_USER) {
				if (mUser.getStatus().getInReplyToStatusId()>0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean hasGeoLocation() {
		try {
			if (type == TYPE_ENTITY) {
				if (mEntity.getDouble("latitude")!=0) {
					return true;
				}
			} else if (type == TYPE_STATUS) {
				if (mStatus.getGeoLocation()!=null) {
					return true;
				}
			} else if (type == TYPE_TWEET) {
				if (mTweet.getLocation()!=null) {
					try {
						GeoQuery gq = new GeoQuery(mTweet.getLocation());
						if (gq.getLocation()!=null) return true;
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} else if (type == TYPE_USER) {
				if (mUser.getStatus().getGeoLocation()!=null) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public double getLatitude() {
		if (type == TYPE_ENTITY) {
			return mEntity.getDouble("latitude");
		} else if (type == TYPE_STATUS) {
			if (mStatus.getGeoLocation()!=null) {
				return mStatus.getGeoLocation().getLatitude();
			}
		} else if (type == TYPE_TWEET) {
			if (mTweet.getLocation()!=null) {
				try {
					GeoQuery gq = new GeoQuery(mTweet.getLocation());
					if (gq.getLocation()!=null) {
						return gq.getLocation().getLatitude();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}				
			}
		} else if (type == TYPE_USER) {
			if (mUser.getStatus().getGeoLocation()!=null) {
				return mUser.getStatus().getGeoLocation().getLatitude();
			}
		}
		return 0;
	}
	
	public double getLongitude() {
		if (type == TYPE_ENTITY) {
			return mEntity.getDouble("longitude");
		} else if (type == TYPE_STATUS) {
			if (mStatus.getGeoLocation()!=null) {
				return mStatus.getGeoLocation().getLongitude();
			}
		} else if (type == TYPE_TWEET) {
			if (mTweet.getGeoLocation()!=null) {
				try {
					GeoQuery gq = new GeoQuery(mTweet.getLocation());
					if (gq.getLocation()!=null) {
						return gq.getLocation().getLongitude();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} else if (type == TYPE_USER) {
			if (mUser.getStatus().getGeoLocation()!=null) {
				return mUser.getStatus().getGeoLocation().getLongitude();
			}
		}
		return 0;
	}
	
	public boolean isRetweet() {
		if (mForceNoRetweet) return false;
		if (type == TYPE_ENTITY) {
			if (mEntity.isAttribute("is_retweet")) {
				if (mEntity.getInt("is_retweet")==1) {
					return true;
				}
			}
		} else if (type == TYPE_STATUS) {
			if (mStatus.getRetweetedStatus()!=null) {
				return true;
			}
		}
		return false;
	}
	
	public String getRetweetUrlAvatar() {
		if (!mForceNoRetweet) {
			if (type == TYPE_ENTITY) {
				if (mEntity.isAttribute("retweet_url_avatar")) {
					return mEntity.getString("retweet_url_avatar");
				}
			} else if (type == TYPE_STATUS) {
				if (mStatus.getRetweetedStatus()!=null) {
					if (mStatus.getRetweetedStatus().getUser().getProfileImageURL()!=null) {
						return mStatus.getRetweetedStatus().getUser().getProfileImageURL().toString();
					} else {
						return "";
					}
				}
			}
		}
		return "";
	}
	
	public String getRetweetUsername() {
		if (!mForceNoRetweet) {
			if (type == TYPE_ENTITY) {
				if (mEntity.isAttribute("retweet_username")) {
					return mEntity.getString("retweet_username");
				}
			} else if (type == TYPE_STATUS) {
				if (mStatus.getRetweetedStatus()!=null) {
					return mStatus.getRetweetedStatus().getUser().getScreenName();
				}
			}
		}
		return "";
	}
	
	public String getRetweetSource() {
		if (!mForceNoRetweet) {
			if (type == TYPE_ENTITY) {
				if (mEntity.isAttribute("retweet_source")) {
					return mEntity.getString("retweet_source");
				}
			} else if (type == TYPE_STATUS) {
				if (mStatus.getRetweetedStatus()!=null) {
					return mStatus.getRetweetedStatus().getSource();
				}
			}
		}
		return "";
	}
	
	public long getRetweetCount() {
		if (type == TYPE_STATUS) {
			return mStatus.getRetweetCount();
/*			try {
				return TweetTopicsCore.twitter.getRetweets(mStatus.getId()).size();
			} catch (TwitterException e) {
				e.printStackTrace();
			}*/
		}
		return 0;
	}

	public void setForceNoRetweet(boolean mForceNoRetweet) {
		this.mForceNoRetweet = mForceNoRetweet;
	}

	public boolean isForceNoRetweet() {
		return mForceNoRetweet;
	}
	
	public void setFavorited(boolean b) {
		favorited = b;
	}
	
	public boolean isFavorited() {
		return favorited;
	}

	public void setHTMLTextFinal(String mHTMLText) {
		this.mHTMLTextFinal = mHTMLText;
	}

	public String getHTMLTextFinal() {
		return mHTMLTextFinal;
	}
	
	public void setTextFinal(String mText) {
		this.mTextFinal = mText;
	}

	public String getTextFinal() {
		return mTextFinal;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isRead() {
		return read;
	}

	
}
