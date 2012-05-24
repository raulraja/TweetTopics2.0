package com.javielinux.fragments;

import adapters.TweetsAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.fragmentadapter.TweetTopicsFragmentAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.Utils;
import infos.InfoTweet;
import layouts.PullToRefreshListView;

import java.util.ArrayList;

public class TweetTopicsFragment extends Fragment {

    public static int TIMELINE = 0;
    public static int MENTIONS = 1;
    public static int FAVORITES = 2;
    public static int DIRECTMESSAGES = 3;
    public static int SENT_DIRECTMESSAGES = 4;

    private TweetsAdapter tweetsAdapter;
    private Entity column_entity;
    private View view;
    private PullToRefreshListView listView;

    public TweetTopicsFragment(long column_id) {

        super();

        try {
            DataFramework.getInstance().open(getActivity(), Utils.packageName);

            column_entity = new Entity("columns", column_id);

            DataFramework.getInstance().close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private ArrayList<InfoTweet> getTweets() {

        String whereType = "";

        switch (column_entity.getInt("type_id")) {
            case TweetTopicsFragmentAdapter.TIMELINE:
                whereType = " AND type_id = " + TIMELINE;
                break;
            case TweetTopicsFragmentAdapter.MENTIONS:
                whereType = " AND type_id = " + MENTIONS;
                break;
            case TweetTopicsFragmentAdapter.DIRECT_MESSAGES:
                whereType = " AND (type_id = " + DIRECTMESSAGES + " OR type_id = " + SENT_DIRECTMESSAGES + ")";
                break;
        }

        ArrayList<Entity> tweets;

        try {
            DataFramework.getInstance().open(getActivity(), Utils.packageName);

            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc");

            DataFramework.getInstance().close();
        } catch (Exception exception) {
            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
        }

        ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();

        for (int i = 0; i < tweets.size(); i++) {
            infoTweets.add(new InfoTweet(tweets.get(i)));
        }

        return infoTweets;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("TweetTopics 2.0", "Generating adapter");
        tweetsAdapter = new TweetsAdapter(getActivity(), getTweets(), -1);

        view = View.inflate(getActivity(), R.layout.tweettopics_fragment, null);
        listView = (PullToRefreshListView) view.findViewById(R.id.tweet_status_listview);

        listView.getAdapterView().setCacheColorHint(Color.TRANSPARENT);

        listView.getAdapterView().setAdapter(tweetsAdapter);

        tweetsAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
