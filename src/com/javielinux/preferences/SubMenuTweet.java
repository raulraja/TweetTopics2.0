package com.javielinux.preferences;

import android.os.Bundle;
import android.widget.ListView;
import com.javielinux.infos.InfoSubMenuTweet;
import com.javielinux.tweettopics2.BaseActivity;

import java.util.ArrayList;

public class SubMenuTweet extends BaseActivity {
    private ListView mListView;
    private SubMenuTweetsAdapter mAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mListView = new ListView(this);
        
        ArrayList<InfoSubMenuTweet> infos = new ArrayList<InfoSubMenuTweet>();
        
        for (String code : InfoSubMenuTweet.codesSubMenuTweets) {
      		infos.add(new InfoSubMenuTweet(this, code));
        }
                
        mAdapter = new SubMenuTweetsAdapter(this, infos);
        
        mListView.setAdapter(mAdapter);
        
        setContentView(mListView);
        
    }
}
