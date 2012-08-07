package com.javielinux.infos;


import com.javielinux.twitter.ConnectionManager;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.Date;
import java.util.HashMap;

public class InfoUsers {

    public static String SIZE_ORIGINAL = "original";
    public static String SIZE_MINI = "mini";
    public static String SIZE_NORMAL = "normal";
    public static String SIZE_BIGGER = "bigger";

	private String name = "";
	private String fullname = "";
	private String location = "";
	private String url = "";
	private Date created = null;
	private String urlAvatar = "";
	private int tweets = 0;
	private int followers = 0;
	private int following = 0;
	private String tweet = "";
	private String tweetTranslate = "";
	private Date dateTweet = null;
	private String bio = "";

    private static HashMap<Long,Boolean> friend = new HashMap<Long,Boolean>();
    private static HashMap<Long,Boolean> follower = new HashMap<Long,Boolean>();
	
	public InfoUsers() {
	}

    public InfoUsers(User user) {
        setName(user.getScreenName());
        setFullname(user.getName());
        setCreated(user.getCreatedAt());
        setLocation(user.getLocation());
        if (user.getURL()!=null) setUrl(user.getURL().toString());
        setFollowers(user.getFollowersCount());
        setFollowing(user.getFriendsCount());
        setTweets(user.getStatusesCount());
        setBio(user.getDescription());
        if (user.getStatus()!=null) setTextTweet(user.getStatus().getText());
        setUrlAvatar(user.getProfileImageURL().toString());

        //infoUsers.setFollower(ConnectionManager.getInstance().getTwitter(request.getUserId()).existsFriendship(request.getUser(), screenName));
        //infoUsers.setFriend(ConnectionManager.getInstance().getTwitter(request.getUserId()).existsFriendship(screenName, request.getUser()));

    }

    public String getURLAvatar(String size) {
        return String.format("https://api.twitter.com/1/users/profile_image?screen_name=%s&size=%s", name, size);
    }

	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public void setTweets(int tweets) {
		this.tweets = tweets;
	}


	public int getTweets() {
		return tweets;
	}


	public void setFollowers(int followers) {
		this.followers = followers;
	}


	public int getFollowers() {
		return followers;
	}


	public void setTextTweet(String tweet) {
		this.tweet = tweet;
	}


	public String getTextTweet() {
		return tweet;
	}


	public void setFollowing(int following) {
		this.following = following;
	}


	public int getFollowing() {
		return following;
	}


	public void setTextTweetTranslate(String tweetTranslate) {
		this.tweetTranslate = tweetTranslate;
	}


	public String getTextTweetTranslate() {
		return tweetTranslate;
	}


	public void setDateTweet(Date dateTweet) {
		this.dateTweet = dateTweet;
	}


	public Date getDateTweet() {
		return dateTweet;
	}


	public void setBio(String bio) {
		this.bio = bio;
	}


	public String getBio() {
		return bio;
	}


	public void setUrlAvatar(String urlAvatar) {
		this.urlAvatar = urlAvatar;
	}


	public String getUrlAvatar() {
		return urlAvatar;
	}
	
	public String getBigUrlAvatar() {
		return urlAvatar.replace("_normal", "").replace("JPG", "jpg");
	}


	public void addFriend(long id, boolean isFriend) {
        friend.put(id, isFriend);
	}

    public boolean hasFriend(long id) {
        return friend.containsKey(id);
    }

    public void checkFriend(long id) {
        if (!hasFriend(id)) {
            try {
                Twitter twitter = ConnectionManager.getInstance().getTwitter(id);
                addFriend(id, twitter.existsFriendship(getName(), twitter.getScreenName()));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

	public boolean isFriend(long id) {
        return (friend.containsKey(id) && friend.get(id));
	}


	public void setFullname(String fullname) {
		this.fullname = fullname;
	}


	public String getFullname() {
		return fullname;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getUrl() {
		return url;
	}


	public void setCreated(Date created) {
		this.created = created;
	}


	public Date getCreated() {
		return created;
	}

    public void addFollower(long id, boolean isFollower) {
        follower.put(id, isFollower);
    }

    public boolean hasFollower(long id) {
        return follower.containsKey(id);
    }

    public void checkFollower(long id) {
        if (!hasFollower(id)) {
            try {
                Twitter twitter = ConnectionManager.getInstance().getTwitter(id);
                addFollower(id, twitter.existsFriendship(twitter.getScreenName(), getName()));
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isFollower(long id) {
        return (follower.containsKey(id) && follower.get(id));
    }

	public void setLocation(String location) {
		this.location = location;
	}


	public String getLocation() {
		return location;
	}


}
