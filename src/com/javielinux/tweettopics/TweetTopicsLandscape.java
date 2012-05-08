package com.javielinux.tweettopics;

import greendroid.widget.ActionBarItem.Type;

import java.util.ArrayList;
import java.util.List;

import layouts.AlphaTextView;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.cyrilmottier.android.greendroid.R;

import database.EntitySearch;

public class TweetTopicsLandscape extends TweetTopicsCore {

	protected LinearLayout mMore;
	protected LinearLayout mTimeline;
	protected LinearLayout mMentions;
	protected LinearLayout mDirectMessages;
	
	protected LinearLayout mLayoutTopBar;
	protected ScrollView  mLayoutScroll;
	protected LinearLayout mLayoutBackgroundAppLeft;
	
	public TweetTopicsLandscape(TweetTopics cnt) {
		super(cnt);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		mTweetTopics.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mTweetTopics.setActionBarContentView(R.layout.tweet_list);
		
		mActionBar = mTweetTopics.getGreenDroidActionBar();
		mActionBar.addItem(Type.Refresh);
		mActionBar.setVisibility(View.GONE);
		
		mLayoutTopBar  = (LinearLayout) mTweetTopics.findViewById(R.id.layout_top_bar);
		
		mLayoutBackgroundAppLeft = (LinearLayout) mTweetTopics.findViewById(R.id.layout_background_app_left);
		
        mTimeline = (LinearLayout) mTweetTopics.findViewById(R.id.timeline);
        
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
        
        mMentions = (LinearLayout) mTweetTopics.findViewById(R.id.mentions);
        
        mMentions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTypeList == TYPE_LIST_COLUMNUSER && mTypeLastColumn == MENTIONS) {
					reloadColumnUser(true);
				} else {
					columnUser(TweetTopicsCore.MENTIONS);
				}
			}
        	
        });
                
        mDirectMessages = (LinearLayout) mTweetTopics.findViewById(R.id.direct_messages);
        
        mDirectMessages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goToDirectMessages();
			}
        	
        });
        
        mMore = (LinearLayout) mTweetTopics.findViewById(R.id.more);
        
        mMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
        	
        });
        
        mIconActivity = (ImageView) mTweetTopics.findViewById(R.id.icon_activity);
        
        mIconActivity.setImageResource(R.drawable.icon_tt);
        
        int px = Utils.dip2px(mTweetTopics, 32);
        
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(px, px);
        ll.gravity = Gravity.CENTER;
        ll.setMargins(5, 5, 5, 5);
        mIconActivity.setLayoutParams(ll);
        
        mIconActivity.setAdjustViewBounds(true);
        
        mIconActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickIconActivity();
			}
        	
        });        
        
        super.onCreate(savedInstanceState);
        
        DisplayMetrics dm = mTweetTopics.getResources().getDisplayMetrics();
        mLayoutList.setLayoutParams(new LinearLayout.LayoutParams(dm.heightPixels, LayoutParams.FILL_PARENT));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dm.heightPixels, LayoutParams.FILL_PARENT);
        params.leftMargin = dm.widthPixels-dm.heightPixels;
        
        mLayoutBackgroundApp.setLayoutParams(params);

        mLayoutScroll = (ScrollView )mTweetTopics.findViewById(R.id.layout_scroll);
        mLayoutScroll.setFadingEdgeLength(0);
        //mLayoutScroll.setBackgroundColor(mThemeManager.getColor("color_shadow_listview"));
        
        loadGridSearch();
		
	}
	
	@Override
    public void loadGridSearch() {
		TableLayout tableLayout = createTableSearch();
		if (tableLayout!=null) {
			LinearLayout data_grid = (LinearLayout) mTweetTopics.findViewById(R.id.data_grid);
			data_grid.removeAllViews();
			data_grid.addView(tableLayout);
		}
	}
	
	private TableLayout createTableSearch() {
		//TableRow.LayoutParams params = new TableRow.LayoutParams(60, 60);
        TableLayout table = new TableLayout(mTweetTopics);
        table.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        
        table.setStretchAllColumns(true);
        
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
    	
    	if (ents.size()<=0) {
    		mLayoutSamplesSearch.setVisibility(View.VISIBLE);
    		return null;
    	}
        
    	int count = 1;
    	TableRow row = new TableRow(mTweetTopics);
    	row.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	
        for (Entity item : ents) {
            
        	View v = View.inflate(mTweetTopics, R.layout.row_search, null);	
        	v.setTag(item.getId());
	    	v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toDoSearch(Long.parseLong(v.getTag().toString()));
				}

	        });
	    	v.setOnLongClickListener(new OnLongClickListener() {
	            @Override
	            public boolean onLongClick(View v) {
	            	/*
	            	Entity ent = mAdapterSearch.getItem(pos);
	            	if (ent.getInt("is_temp")==1) {
	            		mPositionSelectedSearch = pos;
	            		mTweetTopics.showDialog(DIALOG_SUBMENU_SEARCH_TEMP);
	            	} else {
	            		mPositionSelectedSearch = pos;
	            		mTweetTopics.showDialog(DIALOG_SUBMENU_SEARCH);
	            	}*/
	                return true;
	            }

	        });
        	
    		ImageView img = (ImageView)v.findViewById(R.id.img_search);
    		
    		try {
    			Drawable d = Utils.getDrawable(v.getContext(), item.getString("icon_big"));
    			if (d==null) {
    				img.setImageResource(R.drawable.letter_az);	
    			} else {
    				img.setImageDrawable( d );
    			}
    		} catch (Exception e) {
    			img.setImageResource(R.drawable.letter_az);
    			e.printStackTrace();
    		}
    					
    		ImageView tagNew = (ImageView)v.findViewById(R.id.tag_new);
    		ImageView tagLang = (ImageView)v.findViewById(R.id.tag_lang);
    		
    		String name = item.getString("name");
    				
    		if (item.getString("lang").equals("")) {
    			tagLang.setVisibility(View.GONE);
    		} else {
    			tagLang.setVisibility(View.VISIBLE);
    			int i = v.getResources().getIdentifier(Utils.packageName+":drawable/tag_flag_"+item.getString("lang"), null, null);
    			tagLang.setImageResource(i);
    		}
    		
    		
    		if (item.getInt("notifications")==1) {
    						
    			tagNew.setVisibility(View.VISIBLE);
    			
    			try {
    				if (item.getLong("last_tweet_id")<item.getLong("last_tweet_id_notifications")) {							
    					tagNew.setImageBitmap(Utils.getBitmapNumber(mTweetTopics, item.getInt("new_tweets_count"), Color.GREEN, Utils.TYPE_CIRCLE));
    				} else {
    					tagNew.setImageResource(R.drawable.tag_notification);
    				}
    			} catch (Exception e) {
    				tagNew.setImageResource(R.drawable.tag_notification);
    			}
    			
    		} else {				
    			tagNew.setVisibility(View.GONE);
    		}
    		
    	
    		AlphaTextView lTitle = (AlphaTextView)v.findViewById(R.id.title);
    		lTitle.setText(name);
    		
    		if (item.getInt("is_temp")==1) {
    			img.setAlpha(80);
    			lTitle.onSetAlpha(80);
    		} else {				
    			img.setAlpha(255);
    			lTitle.onSetAlpha(255);
    		}
            
            row.addView(v);
            
            if (count%3==0) {
            	table.addView(row);
            	row = new TableRow(mTweetTopics);
            	row.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            }
            
            count++;
        }
        
        if ((count-1)%3>0) {
        	table.addView(row);
        }
        
        return table;

	}
	
	@Override
	protected void refreshColorsBars() {
		mLayoutTopBar.setBackgroundColor(Color.parseColor("#"+mThemeManager.getStringColor("color_top_bar")));
		mLayoutBackgroundAppLeft.setBackgroundColor(Color.parseColor("#"+mThemeManager.getStringColor("color_bottom_bar")));
		mSidebarBackground.setBackgroundColor(Color.parseColor("#"+mThemeManager.getStringColor("list_background_row_color")));
		
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
		
		// ponerlo en la barra

    }

	@Override
	protected void toDoSearch(long id) {
    	
    	super.toDoSearch(id);
   		search();
    	
    }
	
	@Override
    public void toDoReadAfter() {

		super.toDoReadAfter();
		readAfter();

    }
	
}
