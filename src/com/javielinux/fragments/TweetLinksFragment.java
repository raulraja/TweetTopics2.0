/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.javielinux.adapters.TweetsLinkAdapter;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.tweettopics2.TweetActivity;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.LinksUtils;

public class TweetLinksFragment extends Fragment {

    private static String KEY_SAVE_STATE_TWEET = "KEY_SAVE_STATE_TWEET";

    private InfoTweet infoTweet;
    private ListView list;

    private TweetsLinkAdapter adapter;

    public TweetLinksFragment() {
        super();
    }

    public TweetLinksFragment(InfoTweet infoTweet) {
        init(infoTweet);
    }

    public void init(InfoTweet infoTweet) {
        this.infoTweet = infoTweet;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_SAVE_STATE_TWEET, infoTweet);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null && savedInstanceState.containsKey(KEY_SAVE_STATE_TWEET)) {
            init((InfoTweet) savedInstanceState.getParcelable(KEY_SAVE_STATE_TWEET));
        }
        adapter = new TweetsLinkAdapter(getActivity(), getLoaderManager(),
                LinksUtils.pullLinks(infoTweet.getText(), infoTweet.getContentURLs()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.tweet_links_fragment, null);

        list =  ((ListView)view.findViewById(R.id.tweet_links_list));
        // poner estilo de la listas de las preferencias del usuario
        ThemeManager themeManager = new ThemeManager(getActivity());
        list.setDivider(ImageUtils.createDividerDrawable(getActivity(), themeManager.getColor("color_divider_tweet")));
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("prf_use_divider_tweet", true)) {
            list.setDividerHeight(2);
        } else {
            list.setDividerHeight(0);
        }
        list.setFadingEdgeLength(6);
        list.setCacheColorHint(themeManager.getColor("color_shadow_listview"));

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((TweetActivity)getActivity()).goToLink(adapter.getItem(i));
            }
        });

        return view;
    }

}
