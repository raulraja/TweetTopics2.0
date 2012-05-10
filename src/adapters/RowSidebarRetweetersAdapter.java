package adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetTopicsCore;
import layouts.RetweeterItem;
import layouts.TweetConversationItem;
import twitter4j.ResponseList;
import twitter4j.User;

import java.util.ArrayList;

public class RowSidebarRetweetersAdapter extends ArrayAdapter<User> {

    public static class ViewHolder {
        public ImageView avatar_sidebar;
        public TextView username_sidebar;
        public TextView date_sidebar;
        public TextView text_tweet_sidebar;
    }

	private Context mContext;
    private TweetTopicsCore mTweetTopicsCore;
    private long mSelectedId = -1;

    static public ViewHolder generateViewHolder(View v) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.avatar_sidebar = (ImageView)v.findViewById(R.id.user_avatar_sidebar);
        viewHolder.username_sidebar = (TextView)v.findViewById(R.id.tweet_user_name_text_sidebar);
        viewHolder.date_sidebar = (TextView)v.findViewById(R.id.tweet_date_sidebar);
        viewHolder.text_tweet_sidebar = (TextView)v.findViewById(R.id.tweet_text_sidebar);
        return viewHolder;
    }

    public RowSidebarRetweetersAdapter(Context context, TweetTopicsCore core, ResponseList<User> statii) {
        super(context, android.R.layout.simple_list_item_1, statii);
        this.mContext = context;
        mTweetTopicsCore = core;
        TweetConversationItem.cacheBitmaps.clear();
    }

	public RowSidebarRetweetersAdapter(Context context, TweetTopicsCore core, ArrayList<User> statii) {
        super(context, android.R.layout.simple_list_item_1, statii);
        this.mContext = context;
        mTweetTopicsCore = core;
        TweetConversationItem.cacheBitmaps.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		RetweeterItem view;

        if (null == convertView) {
            view = (RetweeterItem) View.inflate(mContext, R.layout.sidebar_retweeters_row, null);
            view.setTag(generateViewHolder(view));
        } else {
            view = (RetweeterItem) convertView;
        }

        User retweeter_user = getItem(position);

        if (retweeter_user != null) {
             view.setRow(retweeter_user);
        }

		return view;
	}
}
