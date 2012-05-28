package com.javielinux.fragments;

import adapters.RowResponseList;
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
    private ArrayList<InfoTweet> infoTweets;
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
            column_entity = new Entity("columns", column_id);
            user_entity = new Entity("users", column_entity.getLong("user_id"));
            infoTweets = new ArrayList<InfoTweet>();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void updateInfoTweet() {

        EntityTweetUser entityTweetUser = new EntityTweetUser(user_entity.getId(), column_entity.getInt("type_id"));

        String whereType = "";

        switch (column_entity.getInt("type_id")) {
            case TweetTopicsConstants.COLUMN_TIMELINE:
                Utils.fillHide();
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

        } catch (Exception exception) {
            tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
        }

        int pos = 0;
        int count = 0;
        boolean found = false;
        int countHide = 0;

        for (int i = 0; i < tweets.size(); i++) {

            boolean delete_tweet = false;

            if (i > 0) {
                if (tweets.get(i).getLong("tweet_id") == tweets.get(i - 1).getLong("tweet_id")) {
                    delete_tweet = true;
                }
            }
            if (delete_tweet) {
                try {
                    tweets.get(i).delete();

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                boolean is_timeline = column_entity.getInt("type_id") ==  TweetTopicsConstants.COLUMN_TIMELINE;

                if (is_timeline && Utils.hideUser.contains(tweets.get(i).getString("username").toLowerCase())) { // usuario
                    countHide++;
                } else if (is_timeline && Utils.isHideWordInText(tweets.get(i).getString("text").toLowerCase())) { // palabra
                    countHide++;
                } else if (is_timeline && Utils.isHideSourceInText(tweets.get(i).getString("source").toLowerCase())) { // fuente
                    countHide++;
                } else {
                    InfoTweet infoTweet = new InfoTweet(tweets.get(i));

                    if (!found && entityTweetUser.getValueLastId() >= tweets.get(i).getLong("tweet_id")) {
                        infoTweet.setLastRead(true);
                        pos = count;
                        found = true;
                    }

                    if (i >= tweets.size() - 1 && !found) {
                        infoTweet.setLastRead(true);
                        pos = count;
                        found = true;
                    }

                    infoTweet.setRead(found);

                    try {
                        infoTweets.add(infoTweet);
                        /*if (r.hasMoreTweetDown()) {
                            response.add(new RowResponseList(RowResponseList.TYPE_MORE_TWEETS));
                        }*/
                        count++;
                    } catch (OutOfMemoryError er) {
                        i = tweets.size();
                    }
                }
            }

        }

        if (column_entity.getInt("type_id") == TweetTopicsConstants.COLUMN_TIMELINE) {
            if (count > 0) {
                int minutes = Integer.parseInt(Utils.getPreference(context).getString("prf_time_refresh", "10"));

                if (minutes > 0) {
                    int miliseconds = minutes * 60 * 1000;
                    Date date = new Date(tweetsAdapter.getItem(0).getDate().getTime() + miliseconds); //600000
                    if (new Date().after(date)) {
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
            if (tweetsAdapter.getCount() > 0) {
                entityTweetUser.saveValueLastIdFromDB();
            }
        }
    }


    public void reloadColumnUser(boolean firstIsLastPosition) {
        //TODO: reloadColumnUser
        /*if (!app.isReloadUserTwitter()) {

            if (firstIsLastPosition) {
                mAdapterResponseList.firtsItemIsLastRead();
            }
            app.reloadUserTwitter();
        }*/
    }

    public void showHideMessage() {
        boolean show = Utils.getPreference(getActivity()).getBoolean("prf_quiet_show_msg", true);

        if (show && tweetsAdapter.getHideMessages() > 0) {
            Utils.showMessage(getActivity(), tweetsAdapter.getHideMessages() + " " + context.getString(R.string.tweets_hidden));
        }
    }

    private boolean markPositionLastReadAsLastReadId() {

        //TODO:sendBroadcastWidgets
        //sendBroadcastWidgets();
        if (tweetsAdapter != null) {
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

        }
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("TweetTopics 2.0", "Generating adapter");

        tweetsAdapter = new TweetsAdapter(getActivity(), infoTweets, -1);
        updateInfoTweet();

        markPositionLastReadAsLastReadId();
        //reloadNewMsgInAllColumns()

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
        updateInfoTweet();
        tweetsAdapter.notifyDataSetChanged();
        listView.onRefreshComplete();
    }

    private void onListItemClick(View v, int position, long id) {

        Intent intent = new Intent(getActivity(), TweetActivity.class);
        getActivity().startActivity(intent);

    }
}
