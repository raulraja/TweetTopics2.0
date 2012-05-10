package infos;

import android.content.Context;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.Utils;

public class InfoSubMenuTweet {

	public static final String[] codesSubMenuTweets = {"reply", "retweet", "lastread", "readafter",
														"favorite", "share", "mention", "map",
														"clipboard", "send_dm", "delete_tweet", "delete_up_tweets"};
	public static final Integer[] stringsSubMenuTweets = {R.string.reply, R.string.retweet, R.string.last_read, R.string.create_read_after,
															R.string.favorite, R.string.share, R.string.mention, R.string.map,
															R.string.copy, R.string.dm, R.string.delete_tweet, R.string.delete_up_tweets};
	public static final Integer[] drawablesSubMenuTweets = {R.drawable.gd_action_bar_reply, R.drawable.gd_action_bar_retweet, R.drawable.gd_action_bar_last_read, R.drawable.gd_action_bar_read_after,
															R.drawable.gd_action_bar_favorite, R.drawable.gd_action_bar_share, R.drawable.gd_action_bar_talk, R.drawable.gd_action_bar_locate,
															R.drawable.gd_action_bar_clipboard, R.drawable.gd_action_bar_dm, R.drawable.gd_action_bar_trashcan, R.drawable.gd_action_bar_trashcan};
	
	private String code = "";
	private int resDrawable;
	private int resName;
	private boolean value = false;

	
	public InfoSubMenuTweet(Context cnt, String code) {
		this.code = code;
		value = Utils.getSubMenuTweet(cnt, code);
		int pos = 0;
		for (int i=0; i<codesSubMenuTweets.length; i++) {
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
