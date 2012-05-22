package com.javielinux.fragments;

import adapters.TweetsAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.fragmentadapter.TweetFragmentAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.Utils;
import infos.InfoTweet;

import java.util.ArrayList;

public class TweetStatusFragment extends Fragment {

    public static int TIMELINE = 0;
    public static int MENTIONS = 1;
    public static int FAVORITES = 2;
    public static int DIRECTMESSAGES = 3;
    public static int SENT_DIRECTMESSAGES = 4;

    private TweetsAdapter tweetsAdapter;
    private Entity column_entity;

    public TweetStatusFragment(long column_id) {

        super();

        try {
            DataFramework.getInstance().open(getActivity(), Utils.packageName);

            column_entity = new Entity("columns", column_id);
            tweetsAdapter = new TweetsAdapter(getActivity());
            getTweets();

            DataFramework.getInstance().close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void getTweets() {

        String whereType = "";

        switch (column_entity.getInt("type_id")) {
            case TweetFragmentAdapter.TIMELINE:
                whereType = " AND type_id = " + TIMELINE;
                break;
            case TweetFragmentAdapter.MENTIONS:
                whereType = " AND type_id = " + MENTIONS;
                break;
            case TweetFragmentAdapter.DIRECT_MESSAGES:
                whereType = " AND (type_id = " + DIRECTMESSAGES + " OR type_id = " + SENT_DIRECTMESSAGES + ")";
                break;
        }

        ArrayList<Entity> tweets;

        try {
            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc");
        } catch (OutOfMemoryError er) {
            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
        }

        ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();

        for (int i = 0; i < tweets.size(); i++) {
            infoTweets.add(new InfoTweet(tweets.get(i)));
        }

        tweetsAdapter.addAll(infoTweets);
        tweetsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.tweet_status_fragment, null);
        ListView listView = (ListView) view.findViewById(R.id.tweet_status_listview);

        listView.setAdapter(tweetsAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
