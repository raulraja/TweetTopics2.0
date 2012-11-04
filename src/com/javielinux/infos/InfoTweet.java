package com.javielinux.infos;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.dataframework.Entity;
import com.javielinux.utils.LinksUtils;
import com.javielinux.utils.Utils;
import com.javielinux.utils.Utils.URLContent;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Date;

public class InfoTweet implements Parcelable {

    public static final String START_URL_TWITTER = "http://twitter.com/#!/";
	public static final String PREFIX_URL_TWITTER = "/status/";
	
	public static final int FROM_TWEETS = 0;
	public static final int FROM_STATUS = 1;
	public static final int FROM_USER = 2;
    public static final int FROM_SAVED_TWEET = 3;
	
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

    private int typeTweet = -1;
	
	private boolean retweet = false;
    private boolean lastRead = false;
    private boolean read = true;
	
	private String urlAvatarRetweet = "";
	private String textRetweet = "";
	private String usernameRetweet = "";
	private String fullnameRetweet = "";
    private String sourceRetweet = "";
	
	private String urlTweet = "";

    private String bestLink = "";
    private int linksCount = 0;
	
	public InfoTweet(Tweet tweet) {
        urls = new ArrayList<URLContent>();
//        if (tweet.getURLEntities()!=null) {
//            for (URLEntity urlEntity : tweet.getURLEntities()) {
//                URLContent urlContent = new URLContent();
//                urlContent.normal = urlEntity.getURL().toString();
//                urlContent.display = urlEntity.getDisplayURL();
//                urlContent.expanded = urlEntity.getExpandedURL().toString();
//            }
//        }
//        if (tweet.getMediaEntities()!=null) {
//            for (MediaEntity mediaEntity : tweet.getMediaEntities()) {
//                URLContent urlContent = new URLContent();
//                urlContent.normal = mediaEntity.getURL().toString();
//                urlContent.display = mediaEntity.getDisplayURL();
//                urlContent.expanded = mediaEntity.getExpandedURL().toString();
//                urlContent.linkMediaThumb = mediaEntity.getMediaURL().toString() + ":thumb";
//                urlContent.linkMediaLarge = mediaEntity.getMediaURL().toString() + ":medium";
//            }
//        }
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
        calculateLinks();
	}
	
	public InfoTweet(Status status) {
        urls = new ArrayList<URLContent>();
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

        calculateLinks();
	}

	public InfoTweet(Entity entity) {
		if (entity.getTable().equals("tweets")) {
			mTypeFrom = FROM_TWEETS;
        } else if (entity.getTable().equals("saved_tweets")) {
            mTypeFrom = FROM_SAVED_TWEET;
		} else {
			mTypeFrom = FROM_STATUS;
			toReplyId = entity.getLong("reply_tweet_id");
			favorited = entity.getInt("is_favorite")==1?true:false;
            typeTweet = entity.getInt("type_id");
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

        calculateLinks();
	}
	
	public InfoTweet(User user) {
        urls = new ArrayList<URLContent>();
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

        calculateLinks();
	}

    private void calculateLinks() {
        ArrayList<String> links = LinksUtils.pullLinks(getText(), getContentURLs());
        linksCount = links.size();
        bestLink = searchLink(links);
    }

    private String searchLink(ArrayList<String> links) {
        if (links.size()>0) {
            // primero buscamos un link con imagen

            for (String link : links) {
                if (!link.startsWith("@") || !link.startsWith("#")) {
                    if (LinksUtils.isLinkImage(link)){
                        return link;
                    }
                }
            }

            // si no un link normal

            for (String link : links) {
                if (!link.startsWith("@") || !link.startsWith("#")) {
                    return link;
                }
            }


            // si no un usuario

            for (String link : links) {
                if (link.startsWith("@")) {
                    return link;
                }
            }

            // si no un hashtag

            for (String link : links) {
                if (link.startsWith("#")) {
                    return link;
                }
            }

        }
        return "";
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

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
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

    public boolean isDm() {
        return (typeTweet ==3 || typeTweet ==4);
    }

    public boolean isTimeline() {
        return (typeTweet ==1);
    }

    public boolean isSavedTweet() {
        return (mTypeFrom == FROM_SAVED_TWEET);
    }

    public String getBestLink() {
        return bestLink;
    }

    public int getLinksCount() {
        return linksCount;
    }

    @Override
    public boolean equals(Object other) {
        return  (id == ((InfoTweet)other).getId());
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
        parcel.writeInt(favorited ? 1 : 0);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeInt(retweet ? 1 : 0);
        parcel.writeString(urlAvatarRetweet);
        parcel.writeString(textRetweet);
        parcel.writeString(usernameRetweet);
        parcel.writeString(fullnameRetweet);
        parcel.writeString(sourceRetweet);
        parcel.writeString(urlTweet);
        parcel.writeString(textHTMLFinal);
        parcel.writeInt(typeTweet);
        parcel.writeInt(linksCount);
        parcel.writeString(bestLink);

        if (urls!=null) {
            parcel.writeInt(urls.size());
            for (URLContent url : urls) {
                parcel.writeString(url.normal);
                parcel.writeString(url.display);
                parcel.writeString(url.expanded);
                parcel.writeString(url.linkMediaThumb);
                parcel.writeString(url.linkMediaLarge);
            }
        }
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
        textHTMLFinal = in.readString();
        typeTweet = in.readInt();
        linksCount = in.readInt();
        bestLink = in.readString();

        int sizeUrls = in.readInt();
        urls = new ArrayList<URLContent>();
        for (int i=0; i<sizeUrls; i++) {
            URLContent url = new URLContent();
            url.normal = in.readString();
            url.display = in.readString();
            url.expanded = in.readString();
            url.linkMediaThumb = in.readString();
            url.linkMediaLarge = in.readString();
            urls.add(url);
        }

        calculateLinks();

    }

}