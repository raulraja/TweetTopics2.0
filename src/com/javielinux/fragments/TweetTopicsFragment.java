package com.javielinux.fragments;

import adapters.TweetsAdapter;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import api.APIDelegate;
import api.APITweetTopics;
import api.request.LoadTypeStatusRequest;
import api.response.ErrorResponse;
import api.response.LoadTypeStatusResponse;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.tweettopics2.TweetTopicsConstants;
import com.javielinux.tweettopics2.Utils;
import database.EntityTweetUser;
import infos.InfoTweet;

import java.util.ArrayList;
import java.util.Date;

public class TweetTopicsFragment extends Fragment {

    private Context context;
    private LoaderManager loaderManager;
    private TweetsAdapter tweetsAdapter;
    private Entity column_entity;
    private Entity user_entity;
    private View view;
    private PullToRefreshListView listView;

    private int positionLastRead = 0;

    public TweetTopicsFragment(Context context, LoaderManager loaderManager, long column_id) {

        super();

        this.context = context;
        this.loaderManager = loaderManager;

        try {
            DataFramework.getInstance().open(getActivity(), Utils.packageName);

            column_entity = new Entity("columns", column_id);
            user_entity = new Entity("users", column_entity.getLong("user_id"));

            DataFramework.getInstance().close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void updateInfoTweet(boolean createAdapter) {

        final ArrayList<InfoTweet> infoTweets = new ArrayList<InfoTweet>();
        EntityTweetUser entityTweetUser = new EntityTweetUser(user_entity.getId(), TweetTopicsConstants.COLUMN_TIMELINE);

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
            DataFramework.getInstance().open(getActivity(), Utils.packageName);

            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc");

            DataFramework.getInstance().close();
        } catch (Exception exception) {
            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
        }

        for (int i = 0; i < tweets.size(); i++) {
            infoTweets.add(new InfoTweet(tweets.get(i)));
        }

        /*if (column_entity.getInt("type_id") == TweetTopicsConstants.COLUMN_TIMELINE) {
            // actualizar automaticamente si hace X minutos del ultimo tweets
            if (count > 0) {
                int minutes = Utils.getPreference(context).getInt("prf_time_refresh", 10);

                if (minutes > 0) {
                    int miliseconds = minutes * 60 * 1000;
                    Date d = new Date(mAdapterResponseList.getItem(0).getEntity().getLong("date") + miliseconds); //600000
                    if (new Date().after(d)) {
                        reloadColumnUser(false);
                    } else {
                        showHideMessage();
                    }
                } else {
                    showHideMessage();
                }
            } else {
                reloadColumnUser(false);
            }
        } else {
            if (mAdapterResponseList.getCount() > 0) {
                mEntityUser.saveValueLastIdFromDB();
            }
        }*/

        if (createAdapter)
            tweetsAdapter = new TweetsAdapter(getActivity(), infoTweets, -1);
        else
            tweetsAdapter.addAll(infoTweets);
    }

    private boolean markPositionLastReadAsLastReadId() {
        //sendBroadcastWidgets();
        //TODO:markPositionLastReadAsLastReadId
        /*if (tweetsAdapter != null) {
            if (!tweetsAdapter.isUserLastItemLastRead()) {
                if (tweetsAdapter.getCount() > positionLastRead) {
                    try {
                        long id = tweetsAdapter.getItem(positionLastRead).getId();
                        if (user_entity != null) {
                            switch (column_entity.getInt("type_id")) {
                                case TweetTopicsConstants.COLUMN_TIMELINE:
                                    user_entity.setValue("last_timeline_id", id + "");
                                    user_entity.save();
                                    break;
                                case TweetTopicsConstants.COLUMN_MENTIONS:
                                    user_entity.setValue("last_mention_id", id + "");
                                    user_entity.save();
                                    break;
                                case TweetTopicsConstants.COLUMN_DIRECT_MESSAGES:
                                    user_entity.setValue("last_direct_id", id + "");
                                    user_entity.save();
                                    break;
                            }

                            return true;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }

        }*/
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("TweetTopics 2.0", "Generating adapter");

        //markPositionLastReadAsLastReadId()
        //reloadNewMsgInAllColumns()

        updateInfoTweet(true);

        view = View.inflate(getActivity(), R.layout.tweettopics_fragment, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.tweet_status_listview);
        listView.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
        listView.getRefreshableView().setAdapter(tweetsAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshMethod();
            }
        });
        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                onListItemClick(view, position, id);
            }
        });

        tweetsAdapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onRefreshMethod() {
        updateInfoTweet(false);
        //listView.getRefreshableView().setAdapter(tweetsAdapter);
        tweetsAdapter.notifyDataSetChanged();
        listView.onRefreshComplete();
    }


    private void onListItemClick(View v, int position, long id) {

        Intent intent = new Intent(getActivity(), TweetActivity.class);
        getActivity().startActivity(intent);

    }
}
