package com.javielinux.tweettopics2;

import adapters.RowSearchAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.utils.Utils;
import database.EntitySearch;

import java.util.ArrayList;
import java.util.List;

public class TweetTopicsPortrait extends TweetTopicsCore {

	protected SlidingDrawer mSlidingDrawer;
	
	protected ImageButton mMore;
	protected ImageButton mTimeline;
	protected ImageButton mMentions;
	protected ImageButton mDirectMessages;
	
	protected ImageView mImageHadleSlide;
	protected LinearLayout mSlide;
	protected ImageView mImgBorderTopSearch;
	
	protected GridView mGridSearch;
	
	public TweetTopicsPortrait(TweetTopics cnt) {
		super(cnt);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mTweetTopics.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mTweetTopics.setContentView(R.layout.tweet_list);

		
		mSlidingDrawer = (SlidingDrawer) mTweetTopics.findViewById(R.id.drawer);
		
        mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener () {
			@Override
			public void onDrawerOpened() {
				mLayoutBlack.setVisibility(View.VISIBLE);
			}        	
        });
        
        mSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener () {
			@Override
			public void onDrawerClosed() {
				mLayoutBlack.setVisibility(View.GONE);

				reloadNewMsgInSlide();
				if (isToDoSearch) {
					isToDoSearch = false;
					if (mTypeList==TYPE_LIST_READAFTER) {
						readAfter();
					}
					if ( (mTypeList==TYPE_LIST_SEARCH) || (mTypeList==TYPE_LIST_SEARCH_NOTIFICATIONS) ) {
						search();
					}
				}
			}        	
        });
        
        mImageHadleSlide = (ImageView) mTweetTopics.findViewById(R.id.icon_handle);
        mImgBorderTopSearch = (ImageView) mTweetTopics.findViewById(R.id.img_border_top_search);
        mSlide = (LinearLayout) mTweetTopics.findViewById(R.id.slide);
        
        mGridSearch = (GridView) mTweetTopics.findViewById(R.id.grid_search);
        
        mTimeline = (ImageButton) mTweetTopics.findViewById(R.id.timeline);
        
        mTimeline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTypeList == TYPE_LIST_COLUMNUSER && mTypeLastColumn == TIMELINE) {
					reloadColumnUser(true);
				} else {
					columnUser(TIMELINE);
				}
			}
        	
        });
        
        mMentions = (ImageButton) mTweetTopics.findViewById(R.id.mentions);
        
        mMentions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTypeList == TYPE_LIST_COLUMNUSER && mTypeLastColumn == MENTIONS) {
					reloadColumnUser(true);
				} else {
					columnUser(MENTIONS);
				}
			}
        	
        });
                
        mDirectMessages = (ImageButton) mTweetTopics.findViewById(R.id.direct_messages);
        
        mDirectMessages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToDirectMessages();
			}
        	
        });
        
        mMore = (ImageButton) mTweetTopics.findViewById(R.id.more);
        
        mMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                Intent prueba = new Intent(mTweetTopics, TweetTopicsActivity.class);
                mTweetTopics.startActivity(prueba);
			}
        	
        });

        
        super.onCreate(savedInstanceState);
        
        LinearLayout mContent = (LinearLayout) mTweetTopics.findViewById(R.id.content);
		BitmapDrawable bmp = (BitmapDrawable)mTweetTopics.getResources().getDrawable(mThemeManager.getResource("search_tile"));
		bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		mContent.setBackgroundDrawable(bmp);

		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (mSlidingDrawer.isOpened()) {
			mSlidingDrawer.animateClose();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
    protected void refreshButtonsColumns() {
		super.refreshButtonsColumns();
    	int typeTimeline = ThemeManager.TYPE_NORMAL;
    	int typeMentions = ThemeManager.TYPE_NORMAL;
    	int typeDirectMessages = ThemeManager.TYPE_NORMAL;
    	
    	if (mTypeList == TYPE_LIST_COLUMNUSER) {
    		if (mTypeLastColumn == TIMELINE) {
    			typeTimeline = ThemeManager.TYPE_SELECTED;
    		} else if (mTypeLastColumn == MENTIONS) {
    	    	typeMentions = ThemeManager.TYPE_SELECTED;
			} else 	if (mTypeLastColumn == DIRECTMESSAGES) {
    	    	typeDirectMessages = ThemeManager.TYPE_SELECTED;
			}
    	}
    	
		mTimeline.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.action_bar_timeline, typeTimeline));
		mMentions.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.action_bar_mentions, typeMentions));
		mDirectMessages.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.action_bar_dm, typeDirectMessages));
		mMore.setImageDrawable(mThemeManager.getDrawableMainButton(R.drawable.action_bar_more, ThemeManager.TYPE_NORMAL));

    }

	@Override
	protected void refreshColorsBars() {
		mLayoutBottomBar.setBackgroundColor(Color.parseColor("#"+mThemeManager.getStringColor("color_bottom_bar")));
		mSidebarBackground.setBackgroundColor(Color.parseColor("#"+mThemeManager.getStringColor("list_background_row_color")));
		
		Utils.SlideDrawable slide = new Utils.SlideDrawable(new RectShape(), Color.parseColor("#"+mThemeManager.getStringColor("color_top_bar")));
		mSlide.setBackgroundDrawable(slide);
		mImgBorderTopSearch.setBackgroundColor(slide.getColorStroke());
		super.refreshColorsBars();
	}
	
	@Override
    protected void reloadNewMsgInSlide() {
		List<Entity> searchs = DataFramework.getInstance().getEntityList("search");
		int totalNotification = 0;
		for (int i=0; i<searchs.size(); i++) {
			if (searchs.get(i).getInt("notifications")==1) {
				EntitySearch es = new EntitySearch(searchs.get(i).getId());
				totalNotification += es.getInt("new_tweets_count");
			}
		}
		if (totalNotification>0) {
			mImageHadleSlide.setImageBitmap(Utils.getBitmapNumber(mTweetTopics, totalNotification, Color.GREEN, Utils.TYPE_CIRCLE));
		} else {
			mImageHadleSlide.setImageResource(R.drawable.lens);
		}
    }
	
	@Override
	protected void toDoSearch(long id) {
    	
    	super.toDoSearch(id);
    	
    	if (mSlidingDrawer.isOpened()) {
    		mSlidingDrawer.animateClose();
    	} else {
    		search();
    	}
    	
    }
	
	@Override
    public void toDoReadAfter() {

		super.toDoReadAfter();
		
    	if (mSlidingDrawer.isOpened()) {
    		mSlidingDrawer.animateClose();
    	} else {
    		readAfter();
    	}

    }
	
	@Override
    public void loadGridSearch() {
    	
    	int typeOrder = Integer.parseInt(mTweetTopics.getPreference().getString("prf_order_list", "1"));
    	
    	String order = "";
    	
    	if (typeOrder==1) {
    		order = "date_create asc";
    	} else if (typeOrder==2) {
    		order = "date_create desc";
    	} else if (typeOrder==3) {
    		order = "use_count desc";
    	} else if (typeOrder==4) {
    		order = "last_modified desc";
    	}
    	
    	ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("search", "is_temp=0", order);
    	
    	ents.addAll(DataFramework.getInstance().getEntityList("search", "is_temp=1", "date_create desc"));
    	
    	mAdapterSearch = new RowSearchAdapter(mTweetTopics, ents);
    	
    	if (mAdapterSearch.getCount()>0) {
    		mGridSearch.setVisibility(View.VISIBLE);
	    	mLayoutSamplesSearch.setVisibility(View.GONE);
	    	mGridSearch.setAdapter(mAdapterSearch);
	    	mGridSearch.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> av, View v, int pos, long id) {				
					toDoSearch(mAdapterSearch.getItem(pos).getId());
				}
	        });
	    	mGridSearch.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	            @Override
	            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
	            	Entity ent = mAdapterSearch.getItem(pos);
	            	if (ent.getInt("is_temp")==1) {
	            		mPositionSelectedSearch = pos;
	            		mTweetTopics.showDialog(DIALOG_SUBMENU_SEARCH_TEMP);
	            	} else {
	            		mPositionSelectedSearch = pos;
	            		mTweetTopics.showDialog(DIALOG_SUBMENU_SEARCH);
	            	}
	                return true;
	            }
	        });
    	} else {
    		mGridSearch.setVisibility(View.GONE);
    		mLayoutSamplesSearch.setVisibility(View.VISIBLE);
    	}
    	
    	reloadNewMsgInSlide();
    }
	
}
