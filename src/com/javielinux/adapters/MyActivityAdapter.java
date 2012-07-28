package com.javielinux.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.components.AlphaTextView;
import com.javielinux.fragments.MyActivityFragment;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class MyActivityAdapter extends BaseAdapter {

    public static final int MAX_SEARCHES_IN_ROW = 3;

    public static final int MY_ACTIVITY_USER = 0;
    public static final int MY_ACTIVITY_TITLE_SEARCH = 1;
    public static final int MY_ACTIVITY_SEARCH = 2;
    public static final int MY_ACTIVITY_NO_USERS = 3;
    public static final int MY_ACTIVITY_NO_SEARCHES = 4;

    private MyActivityFragment myActivityFragment;

    private ThemeManager themeManager;

    public static class MyActivityItem {
        public int type = 0;
        public Entity entityUser;
        public ArrayList<Entity> entitiesSearch;

        public MyActivityItem(int type) {
            this.type = type;
        }

    }

    private ArrayList<MyActivityItem> elements = new ArrayList<MyActivityItem>();

    private Context context;

    public MyActivityAdapter(Context context, MyActivityFragment myActivityFragment)
    {
        this.context = context;
        this.myActivityFragment = myActivityFragment;
        themeManager = new ThemeManager(context);
        refresh();
    }

    public void refresh() {
        elements.clear();
        ArrayList<Entity> users = DataFramework.getInstance().getEntityList("users");

        if (users.size()>0) {
            for (Entity entity : users) {
                MyActivityItem item =  new MyActivityItem(MY_ACTIVITY_USER);
                item.entityUser = entity;
                elements.add(item);
            }
        } else {
            elements.add(new MyActivityItem(MY_ACTIVITY_NO_USERS));
        }

        elements.add(new MyActivityItem(MY_ACTIVITY_TITLE_SEARCH));

        ArrayList<Entity> searches = DataFramework.getInstance().getEntityList("search", "", "is_temp asc");

        if (searches.size()>0) {
            ArrayList<Entity> auxEntitiesSearch = new ArrayList<Entity>();
            for (Entity entity : searches) {
                auxEntitiesSearch.add(entity);
                if (auxEntitiesSearch.size()>=MAX_SEARCHES_IN_ROW) {
                    MyActivityItem item =  new MyActivityItem(MY_ACTIVITY_SEARCH);
                    item.entitiesSearch = auxEntitiesSearch;
                    elements.add(item);
                    auxEntitiesSearch = new ArrayList<Entity>();
                }
            }
        } else {
            elements.add(new MyActivityItem(MY_ACTIVITY_NO_SEARCHES));
        }
    }
    
	@Override
	public int getCount() {
		return elements.size();
	}
	
	public int getPositionById(long id) {
        for (int i=0; i<getCount(); i++) {
        	if ( ((Entity)getItem(i)).getId() == id ) {
        		return i;
        	}
        }
        return -1;
	}
	

	@Override
	public Object getItem(int position) {
        return elements.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

    private View inflateView(MyActivityItem element) {
        View v = null;
        if (element.type==MY_ACTIVITY_USER) {
            v = View.inflate(context, R.layout.my_activity_users_row, null);
        } else if (element.type==MY_ACTIVITY_SEARCH) {
            v = View.inflate(context, R.layout.my_activity_search_row, null);
        } else if (element.type==MY_ACTIVITY_NO_USERS) {
            v = View.inflate(context, R.layout.my_activity_no_users_row, null);
        } else if (element.type==MY_ACTIVITY_NO_SEARCHES) {
            v = View.inflate(context, R.layout.my_activity_no_searches_row, null);
        } else if (element.type==MY_ACTIVITY_TITLE_SEARCH) {
            v = View.inflate(context, R.layout.my_activity_title_search_row, null);
        }
        v.setTag(element.type);
        return v;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        MyActivityItem element = elements.get(position);

        View v = null;

        if (null == convertView) {
            v = inflateView(element);
        } else {
            if (element.type == Integer.valueOf(convertView.getTag().toString())) {
                v = convertView;
            } else {
                v = inflateView(element);
            }
        }

        // crear vista

        if (element.type==MY_ACTIVITY_USER) {

            Entity item = element.entityUser;

            long id = item.getId();

            //v.setBackgroundDrawable(ImageUtils.createStateListDrawable(context, themeManager.getColor("list_background_row_color")));

            ImageView img = (ImageView)v.findViewById(R.id.my_activity_user_icon);
            try {
                img.setImageBitmap(Utils.getBitmapAvatar(id, Utils.AVATAR_LARGE));
            } catch (Exception e) {
                e.printStackTrace();
                img.setImageResource(R.drawable.avatar);
            }
            img.setTag(item);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myActivityFragment.clickUser((Entity)view.getTag());
                }
            });

            ImageView tag_network = (ImageView)v.findViewById(R.id.my_activity_user_tag_network);

            if (item.getString("service").equals("facebook")) {
                tag_network.setImageResource(R.drawable.icon_facebook);
            } else {
                tag_network.setImageResource(R.drawable.icon_twitter);
            }

            TextView fullname = (TextView)v.findViewById(R.id.my_activity_user_fullname);
            if (item.getString("fullname").equals("")) {
                fullname.setText(item.getString("name"));
            } else {
                fullname.setText(item.getString("fullname"));
            }

            TextView name = (TextView)v.findViewById(R.id.my_activity_user_name);
            name.setText(item.getString("name"));

            ImageButton imgButtonTimeline = (ImageButton) v.findViewById(R.id.my_activity_timeline);
            imgButtonTimeline.setTag(id);
            imgButtonTimeline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myActivityFragment.createUserColumn(Integer.valueOf(view.getTag().toString()), TweetTopicsUtils.COLUMN_TIMELINE);
                }
            });

            ImageView imgCounterTimeline = (ImageView)v.findViewById(R.id.my_activity_counter_timeline);
            int totalTimeline = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_TIMELINE
                    + " AND user_tt_id="+item.getId() + " AND tweet_id >'" + Utils.fillZeros(""+item.getString("last_timeline_id"))+"'");
            if (totalTimeline>0) {
                imgCounterTimeline.setVisibility(View.VISIBLE);
                imgCounterTimeline.setImageBitmap(Utils.getBitmapNumber(context, totalTimeline, Color.RED, Utils.TYPE_RECTANGLE, 11));
            } else {
                imgCounterTimeline.setVisibility(View.GONE);
            }

            ImageButton imgButtonMentions = (ImageButton) v.findViewById(R.id.my_activity_mentions);
            imgButtonMentions.setTag(id);
            imgButtonMentions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myActivityFragment.createUserColumn(Integer.valueOf(view.getTag().toString()), TweetTopicsUtils.COLUMN_MENTIONS);
                }
            });

            ImageView imgCounterMentions = (ImageView)v.findViewById(R.id.my_activity_counter_mentions);
            int totalMentions = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_MENTIONS
                    + " AND user_tt_id="+item.getId() + " AND tweet_id >'" + Utils.fillZeros(""+item.getString("last_mention_id"))+"'");
            if (totalMentions>0) {
                imgCounterMentions.setVisibility(View.VISIBLE);
                imgCounterMentions.setImageBitmap(Utils.getBitmapNumber(context, totalMentions, Color.RED, Utils.TYPE_RECTANGLE, 11));
            } else {
                imgCounterMentions.setVisibility(View.GONE);
            }

            ImageButton imgButtonDMs = (ImageButton) v.findViewById(R.id.my_activity_directs);
            imgButtonDMs.setTag(id);
            imgButtonDMs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myActivityFragment.createUserColumn(Integer.valueOf(view.getTag().toString()), TweetTopicsUtils.COLUMN_DIRECT_MESSAGES);
                }
            });

            ImageView imgCounterDirectMessages = (ImageView)v.findViewById(R.id.my_activity_counter_directs);
            int totalDirectMessages = DataFramework.getInstance().getEntityListCount("tweets_user", "type_id = " + TweetTopicsUtils.TWEET_TYPE_DIRECTMESSAGES
                    + " AND user_tt_id="+item.getId() + " AND tweet_id >'" + Utils.fillZeros(""+item.getString("last_direct_id"))+"'");
            if (totalDirectMessages>0) {
                imgCounterDirectMessages.setVisibility(View.VISIBLE);
                imgCounterDirectMessages.setImageBitmap(Utils.getBitmapNumber(context, totalDirectMessages, Color.RED, Utils.TYPE_RECTANGLE, 11));
            } else {
                imgCounterDirectMessages.setVisibility(View.GONE);
            }

        } else if (element.type==MY_ACTIVITY_SEARCH) {

            ((LinearLayout)v).removeAllViews();

            ArrayList<Entity> items = element.entitiesSearch;

            for (Entity item : items) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.weight = 1;
                ((LinearLayout)v).addView(drawSearch(item), params);
            }
        }

        return v;

	}

    private View drawSearch(Entity item) {

        View v = View.inflate(context, R.layout.my_activity_search_item_row, null);

        v.setTag(item);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myActivityFragment.clickSearch((Entity) view.getTag());
            }
        });

        ImageView img = (ImageView)v.findViewById(R.id.my_activity_search_img);

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

        ImageView tagNew = (ImageView)v.findViewById(R.id.my_activity_search_tag_new);
        ImageView tagLang = (ImageView)v.findViewById(R.id.my_activity_search_tag_lang);

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
                    tagNew.setImageBitmap(Utils.getBitmapNumber(context, item.getInt("new_tweets_count"), Color.GREEN, Utils.TYPE_CIRCLE));
                } else {
                    tagNew.setImageResource(R.drawable.tag_notification);
                }
            } catch (Exception e) {
                tagNew.setImageResource(R.drawable.tag_notification);
            }

        } else {
            tagNew.setVisibility(View.GONE);
        }


        AlphaTextView lTitle = (AlphaTextView)v.findViewById(R.id.my_activity_search_title);
        lTitle.setText(name);

        if (item.getInt("is_temp")==1) {
            img.setAlpha(80);
            lTitle.onSetAlpha(80);
        } else {
            img.setAlpha(255);
            lTitle.onSetAlpha(255);
        }

        return v;

    }

}
