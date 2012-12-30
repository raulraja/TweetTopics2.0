/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.infos;


import android.content.Context;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class InfoUsers {

    public static class Friend {
        public Friend(String user) {
            this.user = user;
        }

        public String user;
        public boolean checked;
        public boolean friend;
        public boolean follower;
    }

    public static String SIZE_ORIGINAL = "original";
    public static String SIZE_MINI = "mini";
    public static String SIZE_NORMAL = "normal";
    public static String SIZE_BIGGER = "bigger";

    private long id = -1;
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

    private HashMap<String, Friend> friendly = new HashMap<String, Friend> ();

    public InfoUsers() {
	}

    public InfoUsers(User user) {
        setId(user.getId());
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

        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        for (Entity ent : ents) {
            friendly.put(ent.getString("name"), new Friend(ent.getString("name")));
        }

    }

    public String getURLAvatar(String size) {
        return String.format("https://api.twitter.com/1/users/profile_image?screen_name=%s&size=%s", name, size);
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getId() {
        return id;
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

    public HashMap<String, Friend> getFriendly() {
        return friendly;
    }

	public void addFriendly(String name) {
        friendly.put(name, new Friend(name));
	}

    public void removeFriendly(String name) {
        friendly.remove(name);
    }

    public void replaceFriendly(String name, Friend friend) {
        friendly.remove(name);
        friendly.put(name, friend);
    }

    public boolean hasFriendly(String name) {
        return friendly.containsKey(name);
    }

    public boolean isCheckFriendly(String name) {
        if (hasFriendly(name)) {
           return friendly.get(name).checked;
        }
        return false;
    }

    public void checkFriend(Context context, String name) {
        if (!hasFriendly(name)) {
            addFriendly(name);
        }
        if (!isCheckFriendly(name)) {
            Friend friend = friendly.get(name);
            try {
                //Entity ent = DataFramework.getInstance().getTopEntity("users", "name = '"+getName()+"'", "");
                ConnectionManager.getInstance().open(context);
                Twitter twitter = ConnectionManager.getInstance().getUserForSearchesTwitter();
                Relationship relationship = twitter.showFriendship(getName(), name);
                friend.friend = relationship.isSourceFollowingTarget();
                friend.follower = relationship.isSourceFollowedByTarget();
                friend.checked = true;
                friendly.remove(name);
                friendly.put(name, friend);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

	public boolean isFriend(String name) {
        return (friendly.containsKey(name) && friendly.get(name).friend);
	}

    public boolean isFollower(String name) {
        return (friendly.containsKey(name) && friendly.get(name).follower);
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

	public void setLocation(String location) {
		this.location = location;
	}


	public String getLocation() {
		return location;
	}


}
