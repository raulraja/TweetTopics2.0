package adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import layouts.LoadMoreBreakListItem;
import layouts.TweetListItem;

import java.util.ArrayList;
import java.util.HashMap;

public class ResponseListAdapter extends ArrayAdapter<RowResponseList> {

	public static HashMap<Integer,LoadMoreBreakListItem> CacheLoadMoreListItem = new HashMap<Integer,LoadMoreBreakListItem>();
	
	public static class ViewHolder {
		public ImageView avatarView;
		public ImageView tagAvatar;
		public ImageView tagMap;
		public ImageView tagConversation;
		public TextView screenName;
		public TextView statusText;
		public TextView sourceText;
		public TextView dateText;
		public LinearLayout tweetPhotoLayout;
		public RelativeLayout lastReadLayout;
		public LinearLayout retweetLayout;
		public ImageView retweetAvatar;
		public TextView retweetUser;
		public TextView retweetText;
	}
	
	private Context mContext;
	
	ViewHolder viewHolder = new ViewHolder();

	private LinearLayout mLayoutAd; 
  
	private long mLastTweetId = -1;
	
	private long mSelectedId = -1;
	
	private int mHideMessages = 0;
	private int mPositionTweet;
	
	private int mLastReadPosition = -1;
	private boolean mUserLastItemLastRead = false;
	private Entity mCurrentUser;
	private int mColorLine = 0;
	private TweetTopicsCore mTweetTopicsCore;

	private ThemeManager mThemeManager;
	
	public ResponseListAdapter(Context context, TweetTopicsCore core, ArrayList<RowResponseList> statii, long last_tweet_id) {
		super(context, android.R.layout.simple_list_item_1, statii);
		this.mContext = context;
		mTweetTopicsCore = core;
		mThemeManager = core.getThemeManager();
		mColorLine = mThemeManager.getColor("color_tweet_no_read");
		this.mLastTweetId = last_tweet_id;
		mUserLastItemLastRead = false;
		mCurrentUser = DataFramework.getInstance().getTopEntity("users", "active=1", "");
		mPositionTweet = Integer.parseInt(core.getTweetTopics().getPreference().getString("prf_positions_links", "1"));

	}
  
	public boolean hasAd() {
		for (int i=0; i<getCount(); i++) {
			RowResponseList response = getItem(i);
			if (response.getType()==RowResponseList.TYPE_PUB) {
				return true;
			}
		}
		return false;
	}
	
	public void generateAd() {
		LayoutInflater inflater = LayoutInflater.from(mContext); 
		mLayoutAd = (LinearLayout) inflater.inflate(R.layout.ad, null, false);

		AdView adView = (AdView)mLayoutAd.findViewById(R.id.adView);
	    adView.loadAd(new AdRequest());
	}
	
	private LoadMoreBreakListItem getMoreTweetsView(int position) {
		/*View v = (LoadMoreListItem) mTweetTopicsCore.getTweetTopics().getLayoutInflater().inflate(R.layout.load_more, null);
		((TextView)v.findViewById(R.id.load_more_text)).setText(mContext.getString(R.string.load_more_header));
		v.setBackgroundDrawable(Utils.createStateListDrawable(mContext, new ThemeManager(mContext).getColor("list_background_row_color")));
		return v;*/
				
		LoadMoreBreakListItem v = (LoadMoreBreakListItem) mTweetTopicsCore.getTweetTopics().getLayoutInflater().inflate(R.layout.load_more_break, null);
		v.setBackgroundDrawable(ImageUtils.createStateListDrawable(mContext, mThemeManager.getColor("color_load_more_break")));
		v.showText();
		try {
			RowResponseList prev = getItem(position-1);
			RowResponseList next = getItem(position+1);
			if (prev != null && next != null) {
				v.showText(Utils.diffDate(prev.getDate(), next.getDate()));
			}
		} catch (Exception e) {
			
		}				
		
		/*BitmapDrawable bmp = new BitmapDrawable(BitmapFactory.decodeResource(mTweetTopicsCore.getTweetTopics().getResources(), R.drawable.bg_break_tweet));
		bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		((LinearLayout)v.findViewById(R.id.layout_bg)).setBackgroundDrawable(bmp);*/
		CacheLoadMoreListItem.put(position, v);
		return v;
	}
	
	public void showProgressMoreTweetsView(int position) {
		RowResponseList response = getItem(position);
		if (response.getType()==RowResponseList.TYPE_MORE_TWEETS) {
			if (CacheLoadMoreListItem.containsKey(position)) {
				CacheLoadMoreListItem.get(position).showProgress();
			}
		}
	}
	
	public void hideProgressMoreTweetsView(int position) {
		RowResponseList response = getItem(position);
		if (response.getType()==RowResponseList.TYPE_MORE_TWEETS) {
			if (CacheLoadMoreListItem.containsKey(position)) {
				CacheLoadMoreListItem.get(position).hideProgress();
			}
		}
	}
	
	public void setHideMessages(int m) {
		mHideMessages = m;
	}
	
	public void addHideMessages(int m) {
		mHideMessages += m;
	}
	
	public int getHideMessages() {
		return mHideMessages;
	}
	
	static public ViewHolder generateViewHolder(View v) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.avatarView = (ImageView)v.findViewById(R.id.user_avatar);
		viewHolder.tagMap = (ImageView)v.findViewById(R.id.tag_map);
		viewHolder.tagConversation = (ImageView)v.findViewById(R.id.tag_conversation);
		viewHolder.tagAvatar = (ImageView)v.findViewById(R.id.tag_avatar);
		viewHolder.screenName = (TextView)v.findViewById(R.id.tweet_user_name_text);
		viewHolder.statusText = (TextView)v.findViewById(R.id.tweet_text);
		viewHolder.dateText = (TextView)v.findViewById(R.id.tweet_date);
		viewHolder.sourceText = (TextView)v.findViewById(R.id.tweet_source);
		viewHolder.tweetPhotoLayout = (LinearLayout)v.findViewById(R.id.tweet_photo_layout);
		viewHolder.lastReadLayout = (RelativeLayout)v.findViewById(R.id.lastread_layout);
		
		viewHolder.retweetLayout = (LinearLayout)v.findViewById(R.id.retweet_layout);
		viewHolder.retweetAvatar = (ImageView)v.findViewById(R.id.retweet_avatar);
		viewHolder.retweetUser = (TextView)v.findViewById(R.id.retweet_user);
		viewHolder.retweetText = (TextView)v.findViewById(R.id.retweet_text);
		return viewHolder;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
				
		RowResponseList response = getItem(position);
		if (response.getType()==RowResponseList.TYPE_PUB) {
			return mLayoutAd;
		}
		if (response.getType()==RowResponseList.TYPE_MORE_TWEETS) {
			return getMoreTweetsView(position);
		}
		try {
			TweetListItem v;
			
			if (null == convertView) {
				//Log.d(Utils.TAG, "inflate");
				v = (TweetListItem) View.inflate(mContext, (mPositionTweet==1)?R.layout.tweet_list_item_1:R.layout.tweet_list_item_2, null);
				v.setTag(generateViewHolder(v));
			} else {
				//if (convertView.getClass().getName().equals("layouts.TweetListItem")) {
				if(convertView instanceof TweetListItem) {
					//Log.d(Utils.TAG, "position sin inflate: " + position + " -- " +  response.getText());
					v = (TweetListItem) convertView;
				} else {
					//Log.d(Utils.TAG, "inflate");
					v = (TweetListItem) View.inflate(mContext, (mPositionTweet==1)?R.layout.tweet_list_item_1:R.layout.tweet_list_item_2, null);
					v.setTag(generateViewHolder(v));
				}
			}
			
			if (mSelectedId==position) {
				v.setBackgroundDrawable(ImageUtils.createGradientDrawableSelected(mContext, response.isRead()?0:mColorLine));
			} else if (TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.TIMELINE && response.getText().toLowerCase().contains("@"+mCurrentUser.getString("name").toLowerCase())) {
				v.setBackgroundDrawable(ImageUtils.createGradientDrawableMention(mContext, response.isRead()?0:mColorLine));
			} else if ((TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.MENTIONS || TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.TIMELINE) &&response.isFavorited()) {
				v.setBackgroundDrawable(ImageUtils.createGradientDrawableFavorite(mContext, response.isRead()?0:mColorLine));
			} else {
				Entity color = DataFramework.getInstance().getTopEntity("colors", "type_id=2 and word=\""+response.getUsername()+"\"", "");
				if (color!=null) {
					try {
						int c = Color.parseColor( mThemeManager.getColors().get( color.getEntity("type_color_id").getInt("pos") ) );
						v.setBackgroundDrawable(ImageUtils.createStateListDrawable(mContext, c, response.isRead()?0:mColorLine));
					} catch (Exception e) {
						v.setBackgroundDrawable(ImageUtils.createStateListDrawable(mContext, mThemeManager.getColor("list_background_row_color"), response.isRead()?0:mColorLine));
					}
				} else {
					v.setBackgroundDrawable(ImageUtils.createStateListDrawable(mContext, mThemeManager.getColor("list_background_row_color"), response.isRead()?0:mColorLine));
				}
			}	
			
			v.setRow(response, mLastTweetId, mContext, mTweetTopicsCore, position, mCurrentUser.getString("name"));
			
			//response.setRead(true);
			return v;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
  
	public int appendNewer(ArrayList<Entity> ents, int column) {
	  	  
		setNotifyOnChange(false);
		
		boolean isFirst = (getCount()<=0)?true:false;
	
		int count = 0;
		int countHide = 0;
				
		for (int i=ents.size()-1; i>=0; i--) {
    		boolean delete = false;
    		if (i>0) {
    			if (ents.get(i).getLong("tweet_id") == ents.get(i-1).getLong("tweet_id")) {
    				delete = true;
    			}
    		}
    		if (delete) {
    			try {
    				ents.get(i).delete();
    			} catch (Exception e) {
    			}
    		} else {
    			if (column==TweetTopicsCore.TIMELINE && Utils.hideUser.contains(ents.get(i).getString("username").toLowerCase())) {
    				countHide++;
    			} else if (column==TweetTopicsCore.TIMELINE && Utils.isHideWordInText(ents.get(i).getString("text").toLowerCase())) {
    				countHide++;
    			} else if (column==TweetTopicsCore.TIMELINE && Utils.isHideSourceInText(ents.get(i).getString("source").toLowerCase())) { // fuente
    				countHide++;
    			} else {
					RowResponseList r = new RowResponseList(ents.get(i));
					if (r.hasMoreTweetDown()) {
						insert(new RowResponseList(RowResponseList.TYPE_MORE_TWEETS), 0);	
					}
					if (isFirst) {
						r.setLastRead(true);
						isFirst = false;
					}
					r.setRead(false);
					insert(r, 0);
					count++;
    			}
    		}
		}
		
		this.addHideMessages(countHide);
		
		Log.d(Utils.TAG, count + " mensajes insertados");
	
		notifyDataSetChanged();
		
		return count;
	}
	
	public void appendOlder(ArrayList<RowResponseList> rows) {
	  	  
		setNotifyOnChange(false);
		
		for (RowResponseList row : rows) {
			add(row);
		}
		
		Log.d(Utils.TAG, rows.size() + " mensajes viejos insertados");
	
		notifyDataSetChanged();

	}
	
	public void appendPosition(ArrayList<RowResponseList> rows, int pos, boolean hasMoreTweets) {
	  	
		if (rows!=null && rows.size()>0) {
			setNotifyOnChange(false);
			
			if (!hasMoreTweets) {
				remove(getItem(pos));
			}
			
			for (int i=rows.size()-1; i>=0; i--) {
				rows.get(i).setRead(false);
				insert(rows.get(i), pos);
			}
			
			Log.d(Utils.TAG, rows.size() + " mensajes en la posición " + pos);
		
			notifyDataSetChanged();
		}

	}
	
	public void firtsItemIsLastRead() {
		setNotifyOnChange(false);
		try {
			if (getCount()>0 && getLastReadPosition()>=0) {
				for (int i=0; i<getCount(); i++) {
					((RowResponseList)getItem(i)).setRead(true);	
				}
				if (getLastReadPosition()<getCount()) {
					getItem(getLastReadPosition()).setLastRead(false);
				}
				getItem(0).setLastRead(true);
				setLastReadPosition(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		notifyDataSetChanged();
	}
	
	public void itemIsLastRead(int pos) {
		mUserLastItemLastRead = true;
		setNotifyOnChange(false);
		if (getLastReadPosition()<getCount()) {
			getItem(getLastReadPosition()).setLastRead(false);
		}
		if (getCount()>pos) { 
			getItem(pos).setLastRead(true);
			setLastReadPosition(pos);
		}
		notifyDataSetChanged();
	}
	
	public long getFirstId() {
		if (getCount()<=0) return -1;
		RowResponseList first = getItem(0);
		if (null == first) {
			return -1;
		} else {
			return first.getTweetId();
		}
	}
	
	public long getLastId() {
		if (getCount()<=0) return -1; 
		RowResponseList last = getItem(getCount()-1);
		if (null == last) {
			return -1;
		} else {
			return last.getTweetId();
		}
	}

	public void setLastReadPosition(int mLastReadPosition) {
		this.mLastReadPosition = mLastReadPosition;
	}

	public int getLastReadPosition() {
		return mLastReadPosition;
	}


	public boolean isUserLastItemLastRead() {
		return mUserLastItemLastRead;
	}

}