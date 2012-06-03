package adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;
import infos.InfoTweet;
import layouts.TweetListViewItem;

import java.util.ArrayList;

public class TweetsAdapter extends ArrayAdapter<InfoTweet> {

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

    private ArrayList<InfoTweet> infoTweetArrayList;
    private long last_tweet_id;
    private int position_tweet;
    private long selected_id = -1;
    private int hide_messages = 0;
    private boolean user_last_item_last_read = false;
    private int last_read_position = -1;

    private ThemeManager themeManager;
    private int color_line;

    public TweetsAdapter(Context context, ArrayList<InfoTweet> infoTweetArrayList, long last_tweet_id) {

        super(context, android.R.layout.simple_list_item_1, infoTweetArrayList);

        Log.d(Utils.TAG, "Numero de elementos: " + infoTweetArrayList.size());
        this.infoTweetArrayList = infoTweetArrayList;
        this.last_tweet_id = last_tweet_id;
        this.position_tweet = Integer.parseInt(Utils.getPreference(context).getString("prf_positions_links", "1"));
        themeManager = new ThemeManager(context);
        color_line = themeManager.getColor("color_tweet_no_read");

        notifyDataSetChanged();
    }

    public static ViewHolder generateViewHolder(View v) {

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

        InfoTweet infoTweet = getItem(position) ;


        TweetListViewItem view;

        if (null == convertView) {
            //view = (TweetListViewItem) View.inflate(getContext(), (position_tweet==1)?R.layout.tweet_list_item_1:R.layout.tweet_list_item_2, null);
            view = (TweetListViewItem) View.inflate(getContext(), R.layout.tweet_list_view_item, null);
            view.setTag(generateViewHolder(view));
        } else {
            if(convertView instanceof TweetListViewItem) {
                view = (TweetListViewItem) convertView;
            } else {
                view = (TweetListViewItem) View.inflate(getContext(), R.layout.tweet_list_view_item, null);
                view.setTag(generateViewHolder(view));
            }
        }

        if (selected_id == position) {
            view.setBackgroundDrawable(ImageUtils.createGradientDrawableSelected(getContext(), infoTweet.isRead() ? 0 : color_line));
        /*} else if (TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.TIMELINE && infoTweet.getText().toLowerCase().contains("@"+current_user.getString("name").toLowerCase())) {
            view.setBackgroundDrawable(Utils.createGradientDrawableMention(getContext(), infoTweet.isRead()?0:color_line));
        } else if ((TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.MENTIONS || TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.TIMELINE) &&infoTweet.isFavorited()) {
            view.setBackgroundDrawable(Utils.createGradientDrawableFavorite(getContext(), infoTweet.isRead() ? 0 : color_line));
        */} else {
            Entity color = DataFramework.getInstance().getTopEntity("colors", "type_id=2 and word=\""+infoTweet.getUsername()+"\"", "");
            if (color!=null) {
                try {
                    int c = Color.parseColor(themeManager.getColors().get(color.getEntity("type_color_id").getInt("pos")));
                    view.setBackgroundDrawable(ImageUtils.createStateListDrawable(getContext(), c, infoTweet.isRead() ? 0 : color_line));
                } catch (Exception e) {
                    view.setBackgroundDrawable(ImageUtils.createStateListDrawable(getContext(), themeManager.getColor("list_background_row_color"), infoTweet.isRead() ? 0 : color_line));
                }
            } else {
                view.setBackgroundDrawable(ImageUtils.createStateListDrawable(getContext(), themeManager.getColor("list_background_row_color"), infoTweet.isRead() ? 0 : color_line));
            }
        }

        view.setRow(infoTweet, last_tweet_id, getContext(), position);

        return view;

    }

    public void addElements(ArrayList<InfoTweet> infoTweetArrayList)
    {
        if (infoTweetArrayList != null) {
            this.infoTweetArrayList.clear();
            this.infoTweetArrayList.addAll(infoTweetArrayList);

            notifyDataSetChanged();
        }
    }

    public int appendNewer(ArrayList<Entity> entityList, int column) {

        setNotifyOnChange(false);

        boolean isFirst = (getCount()<=0);

        int count = 0;
        int countHide = 0;

        for (int i=entityList.size()-1; i>=0; i--) {

            boolean delete = false;

            if (i>0) {
                if (entityList.get(i).getLong("tweet_id") == entityList.get(i-1).getLong("tweet_id")) {
                    delete = true;
                }
            }

            if (delete) {
                try {
                    entityList.get(i).delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (column == TweetTopicsConstants.COLUMN_TIMELINE && Utils.hideUser.contains(entityList.get(i).getString("username").toLowerCase())) {
                    countHide++;
                } else if (column == TweetTopicsConstants.COLUMN_TIMELINE && Utils.isHideWordInText(entityList.get(i).getString("text").toLowerCase())) {
                    countHide++;
                } else if (column == TweetTopicsConstants.COLUMN_TIMELINE && Utils.isHideSourceInText(entityList.get(i).getString("source").toLowerCase())) { // fuente
                    countHide++;
                } else {
                    InfoTweet infoTweet = new InfoTweet(entityList.get(i));

                    /*if (infoTweet.hasMoreTweetDown()) {
                        insert(new RowResponseList(RowResponseList.TYPE_MORE_TWEETS), 0);
                    }*/

                    if (isFirst) {
                        infoTweet.setLastRead(true);
                        isFirst = false;
                    }
                    infoTweet.setRead(false);
                    insert(infoTweet, 0);
                    count++;
                }
            }
        }

        this.addHideMessages(countHide);

        Log.d(Utils.TAG, count + " mensajes insertados");

        notifyDataSetChanged();

        return count;
    }

    public void selectedRow(int pos) {
        setNotifyOnChange(false);
        selected_id = pos;
        notifyDataSetChanged();
    }

    public void unSelectedRow() {
        setNotifyOnChange(false);
        selected_id = -1;
        notifyDataSetChanged();
    }

    public boolean hasSelectedRow() {
        if (selected_id < 0) {
            return false;
        }
        return true;
    }

    public void firtsItemIsLastRead() {
        setNotifyOnChange(false);
        try {
            if (getCount()>0 && getLastReadPosition()>=0) {
                for (int i=0; i<getCount(); i++) {
                    ((InfoTweet)getItem(i)).setRead(true);
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
        user_last_item_last_read = true;
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

    public void addHideMessages(int hide_messages) {
        this.hide_messages += hide_messages;
    }

    public int getHideMessages() {
        return this.hide_messages;
    }

    public void setHideMessages(int hide_messages) {
        this.hide_messages = hide_messages;
    }

    public int getLastReadPosition() {
        return this.last_read_position;
    }

    public void setLastReadPosition(int last_read_position) {
        this.last_read_position = last_read_position;
    }

    public boolean isUserLastItemLastRead() {
        return this.user_last_item_last_read;
    }
}
