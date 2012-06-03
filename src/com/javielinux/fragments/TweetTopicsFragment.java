package com.javielinux.fragments;

import adapters.TweetsAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import api.APIDelegate;
import api.APITweetTopics;
import api.request.TwitterUserRequest;
import api.response.ErrorResponse;
import api.response.TwitterUserResponse;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.utils.TweetTopicsConstants;
import com.javielinux.utils.Utils;
import database.EntityTweetUser;
import infos.InfoSaveTweets;
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

    private LinearLayout viewLoading;
    private LinearLayout viewNoInternet;
    private LinearLayout viewUpdate;

    private int positionLastRead = 0;

    private int typeUserColumn = 0;

    private boolean flinging; // if user is doing scroll in listview

    public TweetTopicsFragment(Context context, LoaderManager loaderManager, long column_id) {

        super();

        this.context = context;
        this.loaderManager = loaderManager;

        try {
            column_entity = new Entity("columns", column_id);
            if (column_entity.getInt("type_id")==TweetTopicsConstants.COLUMN_TIMELINE) {
                typeUserColumn = TweetTopicsConstants.TWEET_TYPE_TIMELINE;
            } else if (column_entity.getInt("type_id")==TweetTopicsConstants.COLUMN_MENTIONS) {
                typeUserColumn = TweetTopicsConstants.TWEET_TYPE_MENTIONS;
            } else if (column_entity.getInt("type_id")==TweetTopicsConstants.COLUMN_DIRECT_MESSAGES) {
                typeUserColumn = TweetTopicsConstants.TWEET_TYPE_DIRECTMESSAGES;
            }
            user_entity = new Entity("users", column_entity.getLong("user_id"));
            infoTweets = new ArrayList<InfoTweet>();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void preLoadInfoTweetIfIsNecessary() {

        EntityTweetUser entityTweetUser = new EntityTweetUser(user_entity.getId(), typeUserColumn);

        if (column_entity.getInt("type_id")!=TweetTopicsConstants.COLUMN_TIMELINE &&
                column_entity.getInt("type_id")!=TweetTopicsConstants.COLUMN_MENTIONS &&
                column_entity.getInt("type_id")!=TweetTopicsConstants.COLUMN_DIRECT_MESSAGES) {
            return;
        }

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
                    Log.d(Utils.TAG,entityTweetUser.getFieldLastId() + " getValueLastId: "+entityTweetUser.getValueLastId() + " tweet_id: "+tweets.get(i).getLong("tweet_id"));
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

        tweetsAdapter.setHideMessages(countHide);
        tweetsAdapter.setLastReadPosition(pos);
        positionLastRead = pos;

    }

    public void showUpdating() {
        viewUpdate.setVisibility(View.VISIBLE);
    }

    public void hideUpdating() {
        viewUpdate.setVisibility(View.GONE);
    }

    public void showLoading() {
        viewLoading.setVisibility(View.VISIBLE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    }

    public void showNoInternet() {
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    public void showTweetsList() {
        viewLoading.setVisibility(View.GONE);
        viewNoInternet.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

    public void reloadColumnUser(boolean firstIsLastPosition) {

        if (column_entity.getInt("type_id")!=TweetTopicsConstants.COLUMN_TIMELINE &&
                column_entity.getInt("type_id")!=TweetTopicsConstants.COLUMN_MENTIONS &&
                column_entity.getInt("type_id")!=TweetTopicsConstants.COLUMN_DIRECT_MESSAGES) {
            return;
        }

        APITweetTopics.execute(context, loaderManager, new APIDelegate<TwitterUserResponse>() {
            @Override
            public void onResults(TwitterUserResponse result) {

                hideUpdating();
                listView.onRefreshComplete();

                InfoSaveTweets infoSaveTweets = result.getInfo();

                if (infoSaveTweets.getNewMessages() > 0) {
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

                    whereType += " AND tweet_id >= '" + Utils.fillZeros("" + infoSaveTweets.getOlderId()) + "'";

                    ArrayList<Entity> tweets;

                    try {
                        tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc");
                    } catch (Exception exception) {
                        tweets = DataFramework.getInstance().getEntityList("tweets_user", "user_tt_id = " + column_entity.getLong("user_id") + whereType, "date desc, has_more_tweets_down asc", "0," + Utils.MAX_ROW_BYSEARCH);
                    }

                    int firstVisible = listView.getRefreshableView().getFirstVisiblePosition();
                    int count = 0;
                    boolean found = false;
                    int countHide = 0;
                    boolean is_timeline = column_entity.getInt("type_id") ==  TweetTopicsConstants.COLUMN_TIMELINE;
                    EntityTweetUser entityTweetUser = new EntityTweetUser(user_entity.getId(), column_entity.getInt("type_id"));

                    for (int i = tweets.size()-1; i >=0; i--) {
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
                                found = true;
                            }

                            if (i >= tweets.size() - 1 && !found) {
                                infoTweet.setLastRead(true);
                                found = true;
                            }

                            infoTweet.setRead(found);

                            try {
                                infoTweets.add(0, infoTweet);
                                /*if (r.hasMoreTweetDown()) {
                                    response.add(new RowResponseList(RowResponseList.TYPE_MORE_TWEETS));
                                }*/
                                count++;
                            } catch (OutOfMemoryError er) {
                                i = tweets.size();
                            }
                        }
                    }

                    tweetsAdapter.addHideMessages(countHide);
                    tweetsAdapter.setLastReadPosition(tweetsAdapter.getLastReadPosition() + count);

                    tweetsAdapter.notifyDataSetChanged();

                    listView.getRefreshableView().setSelection(firstVisible + count);

                    showTweetsList();
                }
            }

            @Override
            public void onError(ErrorResponse error) {
                error.getError().printStackTrace();
            }
        }, new TwitterUserRequest(column_entity.getInt("type_id"), user_entity));
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweetsAdapter = new TweetsAdapter(getActivity(), infoTweets, -1);
        preLoadInfoTweetIfIsNecessary();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(Utils.TAG, "onCreateView: " + column_entity.getString("description") + " : " + infoTweets.size());

        //markPositionLastReadAsLastReadId();

        view = View.inflate(getActivity(), R.layout.tweettopics_fragment, null);

        listView = (PullToRefreshListView) view.findViewById(R.id.tweet_status_listview);
        listView.getRefreshableView().setCacheColorHint(Color.TRANSPARENT);
        listView.getRefreshableView().setAdapter(tweetsAdapter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadColumnUser(false);
            }
        });
        listView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                onListItemClick(view, position, id);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (positionLastRead > firstVisibleItem) {
                    positionLastRead = firstVisibleItem;
                    if (firstVisibleItem==0) markPositionLastReadAsLastReadId();
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //if (scrollState == SCROLL_STATE_TOUCH_SCROLL) closeSidebar();

                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    flinging = true;
                }

                if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING && scrollState != SCROLL_STATE_TOUCH_SCROLL) {
                    flinging = false;
                    //TweetListItem.executeLoadTasks();
                }
            }

        });

        listView.getRefreshableView().setSelection(tweetsAdapter.getLastReadPosition());

        viewLoading = (LinearLayout) view.findViewById(R.id.tweet_view_loading);
        viewNoInternet = (LinearLayout) view.findViewById(R.id.tweet_view_no_internet);
        viewUpdate = (LinearLayout) view.findViewById(R.id.tweet_view_update);

        boolean getTweetsFromInternet = false;

        if (infoTweets.size()<=0) {
            showLoading();
            getTweetsFromInternet = true;
        } else {
            if (column_entity.getInt("type_id") == TweetTopicsConstants.COLUMN_TIMELINE) {

                int minutes = Integer.parseInt(Utils.getPreference(context).getString("prf_time_refresh", "10"));

                if (minutes > 0) {
                    int miliseconds = minutes * 60 * 1000;
                    Date date = new Date(tweetsAdapter.getItem(0).getDate().getTime() + miliseconds);

                    if (new Date().after(date)) {
                        showUpdating();
                        getTweetsFromInternet = true;
                    }
                }

            }
        }

        if (getTweetsFromInternet) {
            reloadColumnUser(false);
        } else {
            showHideMessage();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    private void onListItemClick(View v, int position, long id) {

        Intent intent = new Intent(getActivity(), TweetActivity.class);
        intent.putExtra(TweetActivity.KEY_EXTRAS_TWEET, tweetsAdapter.getItem(position-1));
        getActivity().startActivity(intent);

    }
}
