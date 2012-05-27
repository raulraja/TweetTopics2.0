package com.javielinux.fragments;

import adapters.TweetsAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.tweettopics2.TweetTopicsConstants;
import com.javielinux.tweettopics2.Utils;
import infos.InfoTweet;

import java.util.ArrayList;

public class TweetTopicsFragment extends Fragment {

    private TweetsAdapter tweetsAdapter;
    private Entity column_entity;
    private Entity user_entity;
    private View view;
    private PullToRefreshListView listView;

    public TweetTopicsFragment(long column_id) {

        super();

        try {
            DataFramework.getInstance().open(getActivity(), Utils.packageName);

            column_entity = new Entity("columns", column_id);
            user_entity = new Entity("users", column_entity.getLong("user_id"));

            DataFramework.getInstance().close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private ArrayList<InfoTweet> getTweets() {

        String whereType = "";

        switch (column_entity.getInt("type_id")) {
            case TweetTopicsConstants.COLUMN_TIMELINE:
                whereType = " AND type_id = " + TweetTopicsConstants.TWEET_TYPE_TIMELINE;
                break;
            case TweetTopicsConstants.COLUMN_MENTIONS:
                whereType = " AND type_id = " + TweetTopicsConstants.TWEET_TYPE_MENTIONS;
                break;
            case TweetTopicsConstants.COLUMN_DIRECT_MESSAGES:
                whereType = " AND (type_id = " + TweetTopicsConstants.TWEET_TYPE_DIRECTMESSAGES + " OR type_id = " + TweetTopicsConstants.TWEET_TYPE_SENT_DIRECTMESSAGES + ")";
                break;
        }

        ArrayList<Entity> tweets;

        try {
            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc");
        } catch (OutOfMemoryError exception) {
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

        //view = View.inflate(getActivity(), R.layout.tweettopics_fragment, null);

        view = inflater.inflate(R.layout.tweettopics_fragment, container, false);

        listView = (PullToRefreshListView) view.findViewById(R.id.tweet_status_listview);

        listView.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);

        listView.getRefreshableView().setAdapter(tweetsAdapter);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                onListItemClick(v, position, id);
            }

        });

        tweetsAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void refresh() {
        listView.onRefreshComplete();
    }


    private void onListItemClick(View v, int position, long id) {

        Intent intent = new Intent(getActivity(), TweetActivity.class);
        getActivity().startActivity(intent);

    }

}
