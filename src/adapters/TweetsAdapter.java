package adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.*;
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

    private Context context;
    private ArrayList<InfoTweet> infoTweetArrayList;
    private long last_tweet_id;
    private int position_tweet;
    private long selected_id = -1;

    private Entity current_user;
    private ThemeManager themeManager;
    private int color_line;

    public TweetsAdapter(Context context, ArrayList<InfoTweet> infoTweetArrayList, long last_tweet_id) {

        super(context, android.R.layout.simple_list_item_1, infoTweetArrayList);

        Log.d("TweetTopics 2.0", "Numero de elementos:" + infoTweetArrayList.size());
        this.context = context;
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

        Log.d("TweetTopics 2.0", "Getting element " + position);
        InfoTweet infoTweet = getItem(position) ;

        try {

            TweetListViewItem view;

            if (null == convertView) {
                view = (TweetListViewItem) View.inflate(context, (position_tweet==1)?R.layout.tweet_list_item_1:R.layout.tweet_list_item_2, null);
                view.setTag(generateViewHolder(view));
            } else {
                if(convertView instanceof TweetListViewItem) {
                    view = (TweetListViewItem) convertView;
                } else {
                    view = (TweetListViewItem) View.inflate(context, (position_tweet==1)?R.layout.tweet_list_item_1:R.layout.tweet_list_item_2, null);
                    view.setTag(generateViewHolder(view));
                }
            }

            if (selected_id == position) {
                view.setBackgroundDrawable(Utils.createGradientDrawableSelected(context, infoTweet.isRead() ? 0 : color_line));
            } else if (TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.TIMELINE && infoTweet.getText().toLowerCase().contains("@"+current_user.getString("name").toLowerCase())) {
                view.setBackgroundDrawable(Utils.createGradientDrawableMention(context, infoTweet.isRead()?0:color_line));
            } else if ((TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.MENTIONS || TweetTopicsCore.mTypeLastColumn == TweetTopicsCore.TIMELINE) &&infoTweet.isFavorited()) {
                view.setBackgroundDrawable(Utils.createGradientDrawableFavorite(context, infoTweet.isRead() ? 0 : color_line));
            } else {
                Entity color = DataFramework.getInstance().getTopEntity("colors", "type_id=2 and word=\""+infoTweet.getUsername()+"\"", "");
                if (color!=null) {
                    try {
                        int c = Color.parseColor(themeManager.getColors().get(color.getEntity("type_color_id").getInt("pos")));
                        view.setBackgroundDrawable(Utils.createStateListDrawable(context, c, infoTweet.isRead() ? 0 : color_line));
                    } catch (Exception e) {
                        view.setBackgroundDrawable(Utils.createStateListDrawable(context, themeManager.getColor("list_background_row_color"), infoTweet.isRead() ? 0 : color_line));
                    }
                } else {
                    view.setBackgroundDrawable(Utils.createStateListDrawable(context, themeManager.getColor("list_background_row_color"), infoTweet.isRead() ? 0 : color_line));
                }
            }

            view.setRow(infoTweet, last_tweet_id, context, position, current_user.getString("name"));

            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addElements(ArrayList<InfoTweet> infoTweetArrayList)
    {
        if (infoTweetArrayList != null) {
            this.infoTweetArrayList.clear();
            this.infoTweetArrayList.addAll(infoTweetArrayList);

            notifyDataSetChanged();
        }
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
}
