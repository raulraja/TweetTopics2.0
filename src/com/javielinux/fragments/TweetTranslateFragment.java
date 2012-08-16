package com.javielinux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadTranslateTweetRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadTranslateTweetResponse;
import com.javielinux.infos.InfoTweet;
import com.javielinux.tweettopics2.R;

public class TweetTranslateFragment extends Fragment implements APIDelegate<BaseResponse> {

    private InfoTweet infoTweet;
    private String language;

    private TextView tweetText;
    private LinearLayout viewLoading;

    public TweetTranslateFragment(InfoTweet infoTweet, String language) {
        this.infoTweet = infoTweet;
        this.language = language;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.tweet_translate_fragment, null);
        tweetText = (TextView)view.findViewById(R.id.tweet_text);
        viewLoading = (LinearLayout)view.findViewById(R.id.tweet_view_loading);

        translateTweet();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void showLoading() {
        viewLoading.setVisibility(View.VISIBLE);
        tweetText.setVisibility(View.GONE);
    }

    public void showTweetText() {
        viewLoading.setVisibility(View.GONE);
        tweetText.setVisibility(View.VISIBLE);
    }

    private void translateTweet() {
        showLoading();
        APITweetTopics.execute(getActivity(), getLoaderManager(), this, new LoadTranslateTweetRequest(infoTweet.getText(), language));
    }

    @Override
    public void onResults(BaseResponse response) {

        LoadTranslateTweetResponse result = (LoadTranslateTweetResponse)response;

        showTweetText();
        tweetText.setText(result.getText());
    }

    @Override
    public void onError(ErrorResponse error) {
        showTweetText();
        error.getError().printStackTrace();
    }
}
