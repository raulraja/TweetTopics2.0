package com.javielinux.tweettopics2;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.LoadImageWidgetRequest;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadImageWidgetResponse;
import com.javielinux.dialogs.HashTagDialogFragment;
import com.javielinux.fragmentadapter.TweetFragmentAdapter;
import com.javielinux.infos.InfoTweet;
import com.javielinux.utils.TweetActions;
import com.javielinux.utils.Utils;
import com.viewpagerindicator.TabPageIndicator;

import java.io.File;
import java.util.ArrayList;

public class TweetActivity extends BaseLayersActivity {

    public static final String KEY_EXTRAS_TWEET = "tweet";

    private ViewPager pager;
    private TweetFragmentAdapter fragmentAdapter;
    private TabPageIndicator indicator;
    private ThemeManager themeManager;
    private InfoTweet infoTweet;

    private ImageView imgAvatar;
    private TextView txtUsername;
    private TextView txtDate;
    private TextView txtText;

    private LinearLayout llRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        themeManager.setTranslucentTheme();

        Bundle extras = getIntent().getExtras();
        if (extras!=null) {
            if (extras.containsKey(KEY_EXTRAS_TWEET)) {
                infoTweet = (InfoTweet) extras.getParcelable(KEY_EXTRAS_TWEET);
            }
        }

        if (infoTweet==null) {
            Utils.showMessage(this, R.string.error_general);
            finish();
        }

        setContentView(R.layout.tweet_activity);

        imgAvatar = ((ImageView)findViewById(R.id.tweet_avatar));
        imgAvatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                goToLink("@"+(infoTweet.isRetweet()?infoTweet.getUsernameRetweet():infoTweet.getUsername()));
            }

        });

        String urlAvatar = infoTweet.getUrlAvatar();
        String name = infoTweet.getUsername();
        String fullname = infoTweet.getFullname();
        if (infoTweet.isRetweet()) {
            name = infoTweet.getUsernameRetweet();
            urlAvatar = infoTweet.getUrlAvatarRetweet();
            fullname = infoTweet.getFullnameRetweet();
        }

        File file = Utils.getFileForSaveURL(this, urlAvatar);
        if (file.exists()) {
            imgAvatar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            APITweetTopics.execute(this, getSupportLoaderManager(), new APIDelegate<LoadImageWidgetResponse>() {
                @Override
                public void onResults(LoadImageWidgetResponse result) {
                    try {
                        imgAvatar.setImageBitmap(result.getBitmap());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ErrorResponse error) {
                }
            }, new LoadImageWidgetRequest(urlAvatar));

        }

        txtUsername = ((TextView)findViewById(R.id.tweet_username));
        txtUsername.setText( name + ((fullname.equals(""))?"":" (" + fullname + ")") );

        txtDate = ((TextView)findViewById(R.id.tweet_date));
        txtDate.setText(Utils.timeFromTweetExtended(this, infoTweet.getDate()));

        txtText = ((TextView)findViewById(R.id.tweet_text));
        String html = infoTweet.getTextHTMLFinal();
        if (html.equals("")) html = Utils.toHTML(this, infoTweet.getText());
        txtText.setText(Html.fromHtml(html));

        fragmentAdapter = new TweetFragmentAdapter(this, getSupportFragmentManager(), infoTweet);

        pager = (ViewPager)findViewById(R.id.tweet_pager);
        pager.setAdapter(fragmentAdapter);

        indicator = (TabPageIndicator)findViewById(R.id.tweet_indicator);
        indicator.setViewPager(pager);

        (findViewById(R.id.tweet_btn_favorite)).setOnClickListener(clickFavorite);
        (findViewById(R.id.tweet_btn_reply)).setOnClickListener(clickReply);
        (findViewById(R.id.tweet_btn_retweet)).setOnClickListener(clickRetweet);
        (findViewById(R.id.tweet_btn_translate)).setOnClickListener(clickTranslate);
        (findViewById(R.id.tweet_btn_more)).setOnClickListener(clickMore);

        llRoot = (LinearLayout)findViewById(R.id.tweet_ll);

        refreshTheme();

    }

    public void goToLink(String link) {
        if (link.startsWith("@")) {
            Bundle bundle = new Bundle();
            bundle.putString(UserActivity.KEY_EXTRAS_USER, link.replace("@", ""));
            startAnimationActivity(UserActivity.class, bundle);
        } else if (link.startsWith("#")) {
            HashTagDialogFragment frag = new HashTagDialogFragment();
            Bundle args = new Bundle();
            args.putString("hashtag", link);
            frag.setArguments(args);
            frag.show(getSupportFragmentManager(), "dialog");
        } else {
            if (link.startsWith("www")) {
                link = "http://"+link;
            }
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void refreshTheme() {
        if (activityAnimation == Utils.ACTIVITY_ANIMATION_RIGHT) {
            llRoot.setPadding(29,0,0,0);
            llRoot.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar : R.drawable.bg_sidebar_dark);
        } else {
            llRoot.setPadding(0,29,0,0);
            llRoot.setBackgroundResource((themeManager.getTheme() == 1) ? R.drawable.bg_sidebar_left : R.drawable.bg_sidebar_left_dark);
        }
    }

    View.OnClickListener clickFavorite = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TweetActions.goToFavorite(TweetActivity.this, infoTweet);
        }
    };

    View.OnClickListener clickReply = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TweetActions.goToReply(TweetActivity.this, infoTweet);
        }
    };

    View.OnClickListener clickRetweet = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TweetActions.showDialogRetweet(TweetActivity.this, infoTweet);
        }
    };

    View.OnClickListener clickTranslate = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Utils.showMessage(TweetActivity.this, "TODO translate");
        }
    };

    View.OnClickListener clickMore = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            final ArrayList<String> arCode = new ArrayList<String>();
            ArrayList<String> ar = new ArrayList<String>();

            if (!infoTweet.isDm()) {
                if (infoTweet.isSavedTweet())
                    ar.add(getString(R.string.delete_read_after));
                else
                    ar.add(getString(R.string.create_read_after));
                arCode.add("read_after");

                ar.add(getString(R.string.send_direct_message));
                arCode.add("send_dm");

                ar.add(getString(R.string.view_map));
                arCode.add("view_map");

                ar.add(getString(R.string.show_retweeters));
                arCode.add("show_retweeters");
            }

            // TODO Borrar tweet de un usuario
             /*
            if (infoTweet.isTimeline()) {
                if (infoTweet.getUsername().equals(mTweetTopicsCore.getTweetTopics().getActiveUser().getString("name"))) {
                    ar.add(getString(R.string.delete_tweet));
                    arCode.add("delete_tweet");
                }
            }
                 */
            ar.add(getString(R.string.copy_to_clipboard));
            arCode.add("copy_to_clipboard");

            ar.add(getString(R.string.share));
            arCode.add("share");

            CharSequence[] c = new CharSequence[ar.size()];
            for (int i=0; i<ar.size(); i++) {
                c[i] = ar.get(i);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(TweetActivity.this);
            builder.setTitle(R.string.actions);
            builder.setItems(c, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TweetActions.execByCode(arCode.get(which), TweetActivity.this, infoTweet);
                }


            });
            builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            builder.create();
            builder.show();
        }
    };

}
