package infos;


import java.util.Date;

import android.graphics.Bitmap;

public class InfoUsers {

	private String name = "";
	private String fullname = "";
	private String location = "";
	private String url = "";
	private Date created = null;
	private Bitmap avatar = null;
	private String urlAvatar = "";
	private int tweets = 0;
	private int followers = 0;
	private int following = 0;
	private String tweet = "";
	private String tweetTranslate = "";
	private Date dateTweet = null;
	private String bio = "";
	private boolean friend = false;
	private boolean follower = false;
	
	public InfoUsers() {
	}

	
	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}

	public Bitmap getAvatar() {
		return avatar;
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


	public void setFriend(boolean friend) {
		this.friend = friend;
	}


	public boolean isFriend() {
		return friend;
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


	public void setFollower(boolean follower) {
		this.follower = follower;
	}


	public boolean isFollower() {
		return follower;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getLocation() {
		return location;
	}


}
