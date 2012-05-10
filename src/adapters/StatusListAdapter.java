/**
 * 
 */
package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.tweettopics2.Utils;
import layouts.TweetListItem;
import twitter4j.ResponseList;
import twitter4j.Status;

import java.util.ArrayList;

public class StatusListAdapter extends ArrayAdapter<Status> {

	private Context mContext;
	
	private TweetTopicsCore mTweetTopicsCore;

	private long mSelectedId = -1;
	
	public StatusListAdapter(Context context, TweetTopicsCore core,  ResponseList<Status> statii) {
		super(context, android.R.layout.simple_list_item_1, statii);
		this.mContext = context;
		mTweetTopicsCore = core;
	}
  
	public StatusListAdapter(Context context, TweetTopicsCore core,  ArrayList<Status> statii) {
		super(context, android.R.layout.simple_list_item_1, statii);
		this.mContext = context;
		mTweetTopicsCore = core;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TweetListItem v;
		int positionTweet = Integer.parseInt(mTweetTopicsCore.getTweetTopics().getPreference().getString("prf_positions_links", "1"));
		if (null == convertView) {
			v = (TweetListItem) View.inflate(mContext, (positionTweet==1)?R.layout.tweet_list_item_1:R.layout.tweet_list_item_2, null);
			v.setTag(ResponseListAdapter.generateViewHolder(v));
		} else {
			v = (TweetListItem) convertView;
		}
		if (mSelectedId==position) {
			v.setBackgroundDrawable(Utils.createGradientDrawableSelected(mContext, 0));
		} else {
			v.setBackgroundDrawable(Utils.createStateListDrawable(mContext, new ThemeManager(mContext).getColor("list_background_row_color")));
		}
		v.setStatus((Status) getItem(position), mTweetTopicsCore, position);
		return v;
	}
	
	public void selectedRow(int pos) {
		setNotifyOnChange(false);
		mSelectedId = pos;
		notifyDataSetChanged();
	}
	
	public void unSelectedRow() {
		setNotifyOnChange(false);
		mSelectedId = -1;
		notifyDataSetChanged();
	}
	
	public boolean hasSelectedRow() {
		if (mSelectedId < 0) {
			return false;
		}
		return true;
	}

	public long getFirstId() {
		Status firstStatus = getItem(0);
		if (null == firstStatus) {
			return 0;
		} else {
			return firstStatus.getId();
		}
	}

	public long getLastId() {
		Status lastStatus = getItem(getCount()-1);
		if (null == lastStatus) {
			return 0;
		} else {
			return lastStatus.getId();
		}
	}

	public void appendNewer(ArrayList<Status> statii) {
		setNotifyOnChange(false);
		for (Status status : statii) {
			insert(status, 0);
		}
		notifyDataSetChanged();
	}
  
	public void appendOlder(ArrayList<Status> statii) {
		setNotifyOnChange(false);
		for (Status status : statii) {
			add(status);
		}
		notifyDataSetChanged();
	}
}