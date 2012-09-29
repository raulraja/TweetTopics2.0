package com.javielinux.infos;

import android.content.Context;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.TweetActions;

public class InfoSubMenuTweet {

    public static final String[] codesSubMenuTweets = {TweetActions.TWEET_ACTION_REPLY, TweetActions.TWEET_ACTION_RETWEET,
            TweetActions.TWEET_ACTION_LAST_READ, TweetActions.TWEET_ACTION_READ_AFTER, TweetActions.TWEET_ACTION_FAVORITE,
            TweetActions.TWEET_ACTION_SHARE, TweetActions.TWEET_ACTION_MENTION, TweetActions.TWEET_ACTION_CLIPBOARD,
            TweetActions.TWEET_ACTION_SEND_DM, TweetActions.TWEET_ACTION_DELETE_TWEET, TweetActions.TWEET_ACTION_DELETE_UP_TWEET};
    public static final Integer[] stringsSubMenuTweets = {R.string.reply, R.string.retweet, R.string.last_read, R.string.create_read_after,
            R.string.favorite, R.string.share, R.string.mention,
            R.string.copy, R.string.dm, R.string.delete_tweet, R.string.delete_up_tweets};
    public static final Integer[] drawablesSubMenuTweets = {R.drawable.icon_social_reply_dark, R.drawable.icon_social_retweet_dark,
            R.drawable.gd_action_bar_last_read, R.drawable.gd_action_bar_read_after,
            R.drawable.icon_content_favorite_dark, R.drawable.gd_action_bar_share, R.drawable.icon_content_timeline_dark,
            R.drawable.gd_action_bar_clipboard, R.drawable.icon_content_direct_dark, R.drawable.icon_content_delete_dark,
            R.drawable.icon_content_delete_dark};

    private String code = "";
    private int resDrawable;
    private int resName;
    private boolean value = false;


    public InfoSubMenuTweet(Context cnt, String code) {
        this.code = code;
        value = PreferenceUtils.getSubMenuTweet(cnt, code);
        int pos = 0;
        for (int i = 0; i < codesSubMenuTweets.length; i++) {
            if (codesSubMenuTweets[i].equals(code)) pos = i;
        }
        this.resName = stringsSubMenuTweets[pos];
        this.resDrawable = drawablesSubMenuTweets[pos];
    }

    public String getCode() {
        return code;
    }

    public boolean isValue() {
        return value;
    }

    public int getResDrawable() {
        return resDrawable;
    }

    public int getResName() {
        return resName;
    }


}
