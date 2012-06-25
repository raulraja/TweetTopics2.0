package com.javielinux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.javielinux.adapters.TweetsLinkAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;
import infos.InfoTweet;

public class TweetLinksFragment extends Fragment {

    private InfoTweet infoTweet;
    private ListView list;

    private TweetsLinkAdapter adapter;

    public TweetLinksFragment(InfoTweet infoTweet) {
        this.infoTweet = infoTweet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new TweetsLinkAdapter(getActivity(), getLoaderManager(),
                Utils.pullLinks(infoTweet.getText(), infoTweet.getContentURLs()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.tweet_links_fragment, null);

        list =  ((ListView)view.findViewById(R.id.tweet_links_list));

        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
