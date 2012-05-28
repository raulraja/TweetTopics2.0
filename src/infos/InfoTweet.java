package infos;

import adapters.RowResponseList;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.NewStatus;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.tweettopics2.Utils;
import com.javielinux.tweettopics2.Utils.URLContent;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Date;

public class InfoTweet implements Parcelable {

    public static int TYPE_ENTITY = 0;
    public static int TYPE_TWEET = 1;
    public static int TYPE_STATUS = 2;
    public static int TYPE_PUB = 3;
    public static int TYPE_DIRECTMESSAGE = 4;
    public static int TYPE_USER = 5;
    public static int TYPE_MORE_TWEETS = 6;
    public static final String START_URL_TWITTER = "http://twitter.com/#!/";
	public static final String PREFIX_URL_TWITTER = "/status/";
	
	public static final int OUT_TRUE = 0;
	public static final int OUT_FALSE = 1;
	public static final int OUT_ERROR = 2;
	
	public static final int FROM_TWEETS = 0;
	public static final int FROM_STATUS = 1;
	public static final int FROM_USER = 2;
	
	private int mTypeFrom = FROM_TWEETS;
	
	private long idDB = 0;
	private long id = 0;
	private String urlAvatar = "";
	private ArrayList<URLContent> urls = null;
	private String text = "";
	private String textURLs = "";
	private String textFinal = "";
	private String textHTMLFinal = "";
	private String username = "";
	private String fullname = "";
	private long userId = 0;
	private String source = "";
	private String toUsername = "";
	private long toUserId = 0;
	private Date createAt = null;
	private long toReplyId = 0;
	private double latitude = 0;
	private double longitude = 0;
	private boolean favorited = false;
	
	private boolean retweet = false;
    private boolean lastRead = false;
    private boolean read = true;
	
	private String urlAvatarRetweet = "";
	private String textRetweet = "";
	private String usernameRetweet = "";
	private String fullnameRetweet = "";
    private String sourceRetweet = "";
	
	private String urlTweet = "";
	
	public InfoTweet(Tweet tweet) {
		writeTweet(tweet);
	}
	
	private void writeTweet(Tweet tweet) {
		mTypeFrom = FROM_TWEETS;
		id = tweet.getId();
		urlAvatar = tweet.getProfileImageUrl();
		userId = tweet.getFromUserId();
		text = tweet.getText();
		username = tweet.getFromUser();
		fullname = tweet.getFromUser();
		source = tweet.getSource();
		toUsername = tweet.getToUser();
		toUserId = tweet.getToUserId();
		createAt = tweet.getCreatedAt();
		if (tweet.getGeoLocation()!=null) {
			latitude = tweet.getGeoLocation().getLatitude();
			longitude = tweet.getGeoLocation().getLongitude();
		}
		
		urlTweet = START_URL_TWITTER + username.toLowerCase() + "/status/" + id;
	}
	
	public InfoTweet(Status status) {
		writeStatus(status);
	}
	
	private void writeStatus(Status status) {
		mTypeFrom = FROM_STATUS;
		id = status.getId();
		urlAvatar = status.getUser().getProfileImageURL().toString();
		userId = status.getUser().getId();
		text = status.getText();
		username = status.getUser().getScreenName();
		fullname = status.getUser().getName();
		source = status.getSource();
		toUsername = status.getInReplyToScreenName();
		toUserId = status.getInReplyToUserId();
		createAt = status.getCreatedAt();
		toReplyId = status.getInReplyToStatusId();
		favorited = status.isFavorited();
		if (status.getGeoLocation()!=null) {
			latitude = status.getGeoLocation().getLatitude();
			longitude = status.getGeoLocation().getLongitude();
		}
		if (status.getRetweetedStatus()!=null) {
			retweet = true;
			urlAvatarRetweet = status.getRetweetedStatus().getUser().getProfileImageURL().toString();
			textRetweet = status.getRetweetedStatus().getText();
			usernameRetweet = status.getRetweetedStatus().getUser().getScreenName();
			fullnameRetweet = status.getRetweetedStatus().getUser().getName();
            sourceRetweet = status.getRetweetedStatus().getSource();
		}
		
		urlTweet = "http://twitter.com/#!/" + username.toLowerCase() + PREFIX_URL_TWITTER + id;
	}

	public InfoTweet(Entity entity) {
		writeEntity(entity);
	}
	
	private void writeEntity(Entity entity) {
		if (entity.getTable().equals("tweets")) {
			mTypeFrom = FROM_TWEETS;
		} else {
			mTypeFrom = FROM_STATUS;
			toReplyId = entity.getLong("reply_tweet_id");
			favorited = entity.getInt("is_favorite")==1?true:false;
		}
		idDB = entity.getId();
		id = entity.getLong("tweet_id");
		urlAvatar = entity.getString("url_avatar");
		userId = entity.getLong("user_id");
		text = entity.getString("text");
		if (entity.isAttribute("text_urls")) {
			textURLs = entity.getString("text_urls");
			if (urls==null) urls = Utils.urls2content(entity.getString("text_urls"));
		}
		username = entity.getString("username");
		fullname = entity.getString("fullname");
		source = entity.getString("source");
		toUsername = entity.getString("to_username");
		toUserId = entity.getLong("to_user_id");
		createAt = new Date();
		createAt.setTime(entity.getLong("date"));
		latitude = entity.getDouble("latitude");
		longitude = entity.getDouble("longitude");
		if (entity.getTable().equals("tweets_user")) {
			retweet = entity.getInt("is_retweet")==1?true:false;
			if (retweet) {
				urlAvatarRetweet = entity.getString("retweet_url_avatar");
				textRetweet = entity.getString("text");
				usernameRetweet = entity.getString("retweet_username");
			}
		}
		
		urlTweet = "http://twitter.com/#!/" + username.toLowerCase() + "/status/" + id;
	}
	
	public InfoTweet(User user) {
		writeUser(user);
	}
	
	private void writeUser(User user) {
		mTypeFrom = FROM_USER;

		urlAvatar = user.getProfileImageURL().toString();
		userId = user.getId();
		username = user.getScreenName();
		fullname = user.getName();
		try {
			id = user.getStatus().getId();
			text = user.getStatus().getText();
			source = user.getStatus().getSource();
			toUsername = user.getStatus().getInReplyToScreenName();
			toUserId = user.getStatus().getInReplyToUserId();
			createAt = user.getStatus().getCreatedAt();
			toReplyId = user.getStatus().getInReplyToStatusId();
			if (user.getStatus().getGeoLocation()!=null) {
				latitude = user.getStatus().getGeoLocation().getLatitude();
				longitude = user.getStatus().getGeoLocation().getLongitude();
			}
		} catch (Exception e) {
		}
	}
	
	public InfoTweet(RowResponseList row) {
		
		if (row.getContentURLs()!=null) {
			urls = row.getContentURLs();
		}
		
		if (row.getType()==RowResponseList.TYPE_ENTITY) {
			writeEntity(row.getEntity());
		}
		if (row.getType()==RowResponseList.TYPE_TWEET) {
			writeTweet(row.getTweet());
		}
		if (row.getType()==RowResponseList.TYPE_STATUS) {
			writeStatus(row.getStatus());
		}
		if (row.getType()==RowResponseList.TYPE_USER) {
			writeUser(row.getUser());
		}
		
		if (!row.getTextFinal().equals("")) {
			textFinal = row.getTextFinal();
		}
		if (!row.getHTMLTextFinal().equals("")) {
			textHTMLFinal = row.getHTMLTextFinal();
		}
	}

	public long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getText() {
		return text;
	}

	public long getUserId() {
		return userId;
	}

	public String getSource() {
		return source;
	}

	public String getToUsername() {
		return toUsername;
	}

	public long getToUserId() {
		return toUserId;
	}

	public Date getDate() {
		return createAt;
	}

	public String getUrlAvatar() {
		return urlAvatar;
	}

	public long getIdDB() {
		return idDB;
	}

	public int getTypeFrom() {
		return mTypeFrom;
	}


	public long getToReplyId() {
		return toReplyId;
	}

	public String getFullname() {
		return fullname;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public boolean hasLocation() {
		if (latitude!=0) {
			return true;
		}
		return false;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public boolean isRetweet() {
		return retweet;
	}

	public String getUrlAvatarRetweet() {
		return urlAvatarRetweet;
	}

	public String getTextRetweet() {
		return textRetweet;
	}

	public String getFullnameRetweet() {
		return fullnameRetweet;
	}

	public String getUsernameRetweet() {
		return usernameRetweet;
	}

    public String getSourceRetweet() {
        return sourceRetweet;
    }

	public String getUrlTweet() {
		return urlTweet;
	}
	
	public void goToMention(TweetTopicsCore mTweetTopicsCore) {
		mTweetTopicsCore.updateStatus(NewStatus.TYPE_NORMAL, "@"+getUsername(), this);
	}
	
	public void goToReply(TweetTopicsCore mTweetTopicsCore) {
		if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER) && TweetTopicsCore.isTypeLastColumn(TweetTopicsCore.DIRECTMESSAGES)) {
			mTweetTopicsCore.directMessage(getUsername());
		} else {
			ArrayList<String> users = Utils.pullLinksUsers(getText());
			int count = users.size();
			if (!users.contains("@"+getUsername())) count++;

			Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
	    	if (e!=null) {
	    		if (users.contains("@"+e.getString("name"))) count--;	
	    	}
			
			if (count>1) {
				mTweetTopicsCore.getTweetTopics().showDialog(TweetTopicsCore.DIALOG_REPLY);
			} else {
				mTweetTopicsCore.updateStatus(NewStatus.TYPE_REPLY, "", this);
			}
		}
		
		mTweetTopicsCore.closeSidebar();
	}
	
	public void goToRetweet(TweetTopicsCore mTweetTopicsCore) {
		mTweetTopicsCore.showDialogRetweet();
	}
	
	public int goToFavorite(TweetTopicsCore mTweetTopicsCore) {
		try {

			int out = OUT_ERROR; 
			
			ConnectionManager.getInstance().open(mTweetTopicsCore.getTweetTopics());
			
			if (mTweetTopicsCore.isFavoritedSelected()) {
				if (getTypeFrom()==InfoTweet.FROM_STATUS && getIdDB()>0) {
					Entity ent = new Entity("tweets_user", getIdDB());
					ent.setValue("is_favorite", 0);
					ent.save();
				}
				ConnectionManager.getInstance().getTwitter().destroyFavorite(getId());

				out = OUT_FALSE; 
				//mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_OFF), null, null);
				Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.favorite_delete));
			} else {
				if (getTypeFrom()==InfoTweet.FROM_STATUS && getIdDB()>0) {
                    try {
					    Entity ent = new Entity("tweets_user", getIdDB());
					    ent.setValue("is_favorite", 1);
					    ent.save();
                    } catch (CursorIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
				}
				ConnectionManager.getInstance().getTwitter().createFavorite(getId());
				out = OUT_TRUE;
				//mBtSidebar3.setCompoundDrawablesWithIntrinsicBounds(null, mTweetTopicsCore.getThemeManager().getDrawableTweetButton(R.drawable.icon_favorite, ThemeManager.TYPE_NORMAL), null, null);
				Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.favorite_save));
			}
			mTweetTopicsCore.setFavoritedSelected(!mTweetTopicsCore.isFavoritedSelected());
			mTweetTopicsCore.refreshAdapters();
			
			return out;
			
		} catch (TwitterException e) {
			e.printStackTrace();
			Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.favorite_no_save));
			return OUT_ERROR;
		}
	}
	
	public void goToReadAfter(TweetTopicsCore mTweetTopicsCore) {
		try {
			if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_READAFTER)) {
				Entity ent = new Entity("saved_tweets", getIdDB());
				ent.delete();
				mTweetTopicsCore.toDoReadAfter();
				Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.favorite_delete));
			} else {
				Entity ent = new Entity("saved_tweets");
				ent.setValue("url_avatar", getUrlAvatar());
				ent.setValue("username", getUsername());
				ent.setValue("user_id", getUserId());
				ent.setValue("tweet_id", getId()+"");
				ent.setValue("text", getText());
				ent.setValue("text_urls", textURLs);
				ent.setValue("source", getSource());
				ent.setValue("to_username", getToUsername());
				ent.setValue("to_user_id", getToUserId());
				ent.setValue("date", getDate().getTime()+"");
				ent.setValue("latitude", getLatitude());
				ent.setValue("longitude", getLongitude());
				ent.save();
				Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.favorite_save));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.favorite_no_save));
		}
	}
	
	public void goToSendDM(TweetTopicsCore mTweetTopicsCore) {
		mTweetTopicsCore.directMessage(getUsername());
	}
	
	public void goToDeleteTweet(TweetTopicsCore mTweetTopicsCore) {
		if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER)) {
			if (getUsername().equals(mTweetTopicsCore.getTweetTopics().getActiveUser().getString("name"))) {
				ConnectionManager.getInstance().open(mTweetTopicsCore.getTweetTopics());
				try {
					if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER) && TweetTopicsCore.isTypeLastColumn(TweetTopicsCore.DIRECTMESSAGES)) {
						ConnectionManager.getInstance().getTwitter().destroyDirectMessage(getId());
					} else {
						ConnectionManager.getInstance().getTwitter().destroyStatus(getId());
					}
					if (getIdDB()>0) {
						Entity ent = new Entity ("tweets_user", getIdDB());
						ent.delete();
					}
					Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.delete_tweet_correct));
					mTweetTopicsCore.refreshAdapters();
				} catch (TwitterException e) {
					e.printStackTrace();
					Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.delete_tweet_problem));
				} catch (Exception e) {
					e.printStackTrace();
					Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.delete_tweet_problem));
				}
			} else {
				Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.option_no_available));
			}
		} else {
			Utils.showMessage(mTweetTopicsCore.getTweetTopics(), mTweetTopicsCore.getTweetTopics().getString(R.string.option_no_available));
		}
	}
	
	public void goToMap(TweetTopicsCore mTweetTopicsCore) {
		mTweetTopicsCore.showMap(this);
	}
	
	public void goToClipboard(TweetTopicsCore mTweetTopicsCore) {
		mTweetTopicsCore.copyToClipboard(getText());
	}
	
	public void goToShare(TweetTopicsCore mTweetTopicsCore) {
		Intent msg=new Intent(Intent.ACTION_SEND);
        msg.putExtra(Intent.EXTRA_TEXT, getUsername() + ": " + getText());
        msg.setType("text/plain");
        mTweetTopicsCore.getTweetTopics().startActivity(msg);
	}
	
	public boolean goToMarkLastReadId(TweetTopicsCore mTweetTopicsCore, int pos) {
		return mTweetTopicsCore.markLastReadId(pos, getId());
	}
	
	public boolean execByCode(String code, TweetTopicsCore mTweetTopicsCore, int pos) {
		/*
		"reply", "retweet", "lastread", "readafter",
		"favorite", "share", "mention", "map",
		"clipboard", "send_dm", "delete_tweet"};
		*/
		if (code.equals("reply")) {
			this.goToReply(mTweetTopicsCore);
		} else if (code.equals("retweet")) {
			this.goToRetweet(mTweetTopicsCore);
		} else if (code.equals("lastread")) {
			return this.goToMarkLastReadId(mTweetTopicsCore, pos);
		} else if (code.equals("readafter")) {
			this.goToReadAfter(mTweetTopicsCore);
		} else if (code.equals("favorite")) {
			this.goToFavorite(mTweetTopicsCore);
		} else if (code.equals("share")) {
			this.goToShare(mTweetTopicsCore);
		} else if (code.equals("mention")) {
			this.goToMention(mTweetTopicsCore);
		} else if (code.equals("map")) {
			this.goToMap(mTweetTopicsCore);
		} else if (code.equals("clipboard")) {
			this.goToClipboard(mTweetTopicsCore);
		} else if (code.equals("send_dm")) {
			this.goToSendDM(mTweetTopicsCore);
		} else if (code.equals("delete_tweet")) {
			this.goToDeleteTweet(mTweetTopicsCore);
		} else if (code.equals("delete_up_tweets")) {
			this.goToDeleteTop(mTweetTopicsCore);
		}
		return false;
	}
	
	public void goToDeleteTop(TweetTopicsCore mTweetTopicsCore) {
		Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
		if (e!=null) {
			Entity ent = new Entity("tweets_user", getIdDB());
			String date = ent.getString("date");
			String sqldelete = "DELETE FROM tweets_user WHERE type_id= 0 and user_tt_id="+e.getId() + " AND date > '" + date + "'";
			DataFramework.getInstance().getDB().execSQL(sqldelete);
		}
	}

	public String getTextHTMLFinal() {
		return textHTMLFinal;
	}

    public void setTextHTMLFinal(String textHTMLFinal) {
        this.textHTMLFinal = textHTMLFinal;
    }

	public String getTextFinal() {
		return textFinal;
	}

    public void setTextFinal(String textFinal) {
        this.textFinal = textFinal;
    }

    public String getTextURLs() {
        return textURLs;
    }


    public ArrayList<URLContent> getContentURLs() {
		return urls;
	}

    public String getTime(Context context) {
        try {
            return Utils.timeFromTweet(context, createAt);

            /*if (type == TYPE_ENTITY) {
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
            }*/
        } catch (Exception e) {

        }
        return "";
    }

    public boolean hasConversation() {

        if (toReplyId > 0)
            return true;

        return false;
    }

    public boolean hasGeoLocation() {
        if (latitude != 0) {
            return true;
        }

        return false;
    }

    public long getRetweetCount() {
        //TODO: Revisar getRetweetCount
        return 0;
    }

    public void setLastRead(boolean lastRead) {
        this.lastRead = lastRead;
    }

    public boolean isLastRead() {
        return lastRead;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    /*
    Parcelable implement
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(mTypeFrom);
        parcel.writeLong(id);
        parcel.writeString(urlAvatar);
        parcel.writeLong(userId);
        parcel.writeString(text);
        parcel.writeString(username);
        parcel.writeString(fullname);
        parcel.writeString(source);
        parcel.writeString(toUsername);
        parcel.writeLong(toUserId);
        parcel.writeLong(createAt.getTime());
        parcel.writeLong(toReplyId);
        parcel.writeInt(favorited?1:0);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeInt(retweet?1:0);
        parcel.writeString(urlAvatarRetweet);
        parcel.writeString(textRetweet);
        parcel.writeString(usernameRetweet);
        parcel.writeString(fullnameRetweet);
        parcel.writeString(sourceRetweet);
        parcel.writeString(urlTweet);
    }

    public static final Parcelable.Creator<InfoTweet> CREATOR
            = new Parcelable.Creator<InfoTweet>() {
        public InfoTweet createFromParcel(Parcel in) {
            return new InfoTweet(in);
        }

        public InfoTweet[] newArray(int size) {
            return new InfoTweet[size];
        }
    };

    private InfoTweet(Parcel in) {
        mTypeFrom = in.readInt();
        id = in.readLong();
        urlAvatar = in.readString();
        userId = in.readLong();
        text = in.readString();
        username = in.readString();
        fullname = in.readString();
        source = in.readString();
        toUsername = in.readString();
        toUserId = in.readLong();
        createAt = new Date(in.readLong());
        toReplyId = in.readLong();
        favorited = (in.readInt()==1);
        latitude = in.readDouble();
        longitude = in.readDouble();
        retweet = (in.readInt()==1);
        urlAvatarRetweet = in.readString();
        textRetweet = in.readString();
        usernameRetweet = in.readString();
        fullnameRetweet = in.readString();
        sourceRetweet = in.readString();
        urlTweet = in.readString();

    }

}