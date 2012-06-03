package com.javielinux.tweettopics2;

import adapters.UsersAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.ClipboardManager;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.Utils;
import infos.InfoTweet;
import preferences.RetweetsTypes;
import updatestatus.ServiceUpdateStatus;

import java.util.ArrayList;

public class TweetActions {

    public static final int ACTIVITY_NEWEDITSEARCH = 0;
    public static final int ACTIVITY_PREFERENCES = 1;
    public static final int ACTIVITY_NEWSTATUS = 2;
    public static final int ACTIVITY_USER = 3;
    public static final int ACTIVITY_WALLPAPER = 4;
    public static final int ACTIVITY_COLORS_APP = 5;

    public static boolean execByCode(String code, Activity activity, InfoTweet infoTweet) {
        /*
          "reply", "retweet", "lastread", "readafter",
          "favorite", "share", "mention", "map",
          "clipboard", "send_dm", "delete_tweet"};
          */
        if (code.equals("reply")) {
            goToReply(activity, infoTweet);
        } else if (code.equals("retweet")) {
            showDialogRetweet(activity, infoTweet);
        } else if (code.equals("lastread")) {
            //return this.goToMarkLastReadId(mTweetTopicsCore, pos);
        } else if (code.equals("readafter")) {
            saveTweet(activity, infoTweet);
        } else if (code.equals("favorite")) {
            //this.goToFavorite(mTweetTopicsCore);
        } else if (code.equals("share")) {
            goToShare(activity, infoTweet);
        } else if (code.equals("mention")) {
            goToMention(activity, infoTweet);
        } else if (code.equals("clipboard")) {
            copyToClipboard(activity, infoTweet);
        } else if (code.equals("send_dm")) {
            directMessage(activity, infoTweet.getUsername());
        } else if (code.equals("delete_tweet")) {
            //this.goToDeleteTweet(mTweetTopicsCore);
        } else if (code.equals("delete_up_tweets")) {
            //this.goToDeleteTop(mTweetTopicsCore);
        }
        return false;
    }

    public static void copyToClipboard(Activity activity, InfoTweet infoTweet) {
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(infoTweet.getText());
        Utils.showMessage(activity, activity.getString(R.string.copied_to_clipboard));
    }

    public static void goToMention(Activity activity, InfoTweet infoTweet) {
        updateStatus(activity, NewStatus.TYPE_NORMAL, "@" + infoTweet.getUsername(), infoTweet);
    }

    public static void goToShare(Activity activity, InfoTweet infoTweet) {
        Intent msg=new Intent(Intent.ACTION_SEND);
        msg.putExtra(Intent.EXTRA_TEXT, infoTweet.getUsername() + ": " + infoTweet.getText());
        msg.setType("text/plain");
        activity.startActivity(msg);
    }

    public static void saveTweet(Activity activity, InfoTweet infoTweet) {
        try {
            if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_READAFTER)) {
                Entity ent = new Entity("saved_tweets", infoTweet.getIdDB());
                ent.delete();
                // TODO borrar registro de la pantalla
                Utils.showMessage(activity, activity.getString(R.string.favorite_delete));
            } else {
                Entity ent = new Entity("saved_tweets");
                ent.setValue("url_avatar", infoTweet.getUrlAvatar());
                ent.setValue("username", infoTweet.getUsername());
                ent.setValue("user_id", infoTweet.getUserId());
                ent.setValue("tweet_id", infoTweet.getId()+"");
                ent.setValue("text", infoTweet.getText());
                ent.setValue("text_urls", infoTweet.getTextURLs());
                ent.setValue("source", infoTweet.getSource());
                ent.setValue("to_username", infoTweet.getToUsername());
                ent.setValue("to_user_id", infoTweet.getToUserId());
                ent.setValue("date", infoTweet.getDate().getTime()+"");
                ent.setValue("latitude", infoTweet.getLatitude());
                ent.setValue("longitude", infoTweet.getLongitude());
                ent.save();
                Utils.showMessage(activity, activity.getString(R.string.favorite_save));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showMessage(activity, activity.getString(R.string.favorite_no_save));
        }
    }

    public static void goToReply(Activity activity, InfoTweet infoTweet) {
        if (infoTweet.isDm()) {
            directMessage(activity, infoTweet.getUsername());
        } else {
            ArrayList<String> users = Utils.pullLinksUsers(infoTweet.getText());
            int count = users.size();
            if (!users.contains("@"+infoTweet.getUsername())) count++;

            Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
            if (e!=null) {
                if (users.contains("@"+e.getString("name"))) count--;
            }

            if (count>1) {
                showDialogReply(activity, infoTweet);
            } else {
                updateStatus(activity, NewStatus.TYPE_REPLY, "", infoTweet);
            }
        }

    }

    public static void showDialogReply(final Activity activity, final InfoTweet it) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.actions)
                .setItems(R.array.actions_reply, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (it != null) {
                                ArrayList<String> users = Utils.pullLinksUsers(it.getText());
                                String text = "";
                                String user = "";
                                Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
                                if (e != null) {
                                    user = e.getString("name");
                                }
                                for (int i = 0; i < users.size(); i++) {
                                    if ((!users.get(i).toLowerCase().equals("@" + it.getUsername().toLowerCase()))
                                            && (!users.get(i).toLowerCase().equals("@" + user.toLowerCase()))) {
                                        text += users.get(i) + " ";
                                    }
                                }
                                updateStatus(activity, NewStatus.TYPE_REPLY, text, it);
                            }
                        } else if (which == 1) {
                            if (it != null) {
                                ArrayList<String> users = Utils.pullLinksUsers(it.getText());
                                String text = " //cc ";
                                String user = "";
                                Entity e = DataFramework.getInstance().getTopEntity("users", "active=1", "");
                                if (e != null) {
                                    user = e.getString("name");
                                }
                                for (int i = 0; i < users.size(); i++) {
                                    if ((!users.get(i).toLowerCase().equals("@" + it.getUsername().toLowerCase()))
                                            && (!users.get(i).toLowerCase().equals("@" + user.toLowerCase()))) {
                                        text += users.get(i) + " ";
                                    }
                                }
                                updateStatus(activity, NewStatus.TYPE_REPLY_ON_COPY, text, it);
                            }
                        } else if (which == 2) {
                            if (it != null) {
                                updateStatus(activity, NewStatus.TYPE_REPLY, "", it);
                            }
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    public static void directMessage(Activity activity, String username) {
        Intent newstatus = new Intent(activity, NewStatus.class);
        newstatus.putExtra("type", NewStatus.TYPE_DIRECT_MESSAGE);
        newstatus.putExtra("username_direct_message", username);
        activity.startActivityForResult(newstatus, ACTIVITY_NEWSTATUS);

    }

    public static void updateStatus(final Activity activity, final String text) {
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        if (ents.size() == 1) {

            sendStatus(activity, ents.get(0).getId() + "", text);

        } else {
            final UsersAdapter adapter = new UsersAdapter(activity, ents);

            AlertDialog builder = new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setTitle(R.string.users)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendStatus(activity, adapter.getItem(which).getId() + "", text);
                        }

                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
            builder.show();
        }
    }

    public static void updateStatus(Activity activity, int type, String text, InfoTweet tweet) {
        updateStatus(activity, type, text, tweet, "");
    }

    private static void updateStatus(Activity activity, int type, String text, InfoTweet tweet, String prev) {
        Intent newstatus = new Intent(activity, NewStatus.class);
        newstatus.putExtra("text", text);
        newstatus.putExtra("type", type);
        newstatus.putExtra("retweet_prev", prev);
        if (tweet != null) {
            if (type == NewStatus.TYPE_REPLY || type == NewStatus.TYPE_REPLY_ON_COPY) {
                newstatus.putExtra("reply_tweetid", tweet.getId());
            }
            if (tweet.isRetweet()) {
                newstatus.putExtra("reply_avatar", tweet.getUrlAvatarRetweet());
                newstatus.putExtra("reply_screenname", tweet.getUsernameRetweet());
                newstatus.putExtra("reply_text", tweet.getTextRetweet());
            } else {
                newstatus.putExtra("reply_avatar", tweet.getUrlAvatar());
                newstatus.putExtra("reply_screenname", tweet.getUsername());
                newstatus.putExtra("reply_text", tweet.getText());
            }
        }
        activity.startActivityForResult(newstatus, ACTIVITY_NEWSTATUS);
    }

    public static void showDialogRetweet(final Activity activity, final InfoTweet it) {
        final ArrayList<String> phrases = new ArrayList<String>();
        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("types_retweets");

        CharSequence[] c = new CharSequence[ents.size() + 3];
        c[0] = activity.getString(R.string.retweet_now);
        phrases.add("_RN_");
        c[1] = activity.getString(R.string.edit_message);
        phrases.add("_EM_");
        c[2] = activity.getString(R.string.retweet_url);
        phrases.add("_RU_");
        for (int i = 0; i < ents.size(); i++) {
            c[i + 3] = activity.getString(R.string.retweet) + " \"" + ents.get(i).getString("phrase") + "\" " + activity.getString(R.string.now);
            phrases.add(ents.get(i).getString("phrase"));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.retweet);
        builder.setItems(c, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phrase = phrases.get(which);
                if (phrase.equals("_RN_")) {
                    if (it != null) {
                        retweetStatus(activity, it.getId());
                    }
                } else if (phrase.equals("_EM_")) {
                    if (it != null) {
                        updateStatus(activity, NewStatus.TYPE_RETWEET, it.getText(), it);
                    }
                } else if (phrase.equals("_RU_")) {
                    if (it != null) {
                        updateStatus(activity, NewStatus.TYPE_RETWEET, it.getUrlTweet(), it);
                    }
                } else {
                    if (it != null) {
                        String text = phrase + " RT: @" + it.getUsername() + ": " + it.getText();
                        if (text.length() > 140) {
                            updateStatus(activity, NewStatus.TYPE_RETWEET, it.getText(), it, phrase);
                        } else {
                            updateStatus(activity, text);
                        }
                    }
                }
            }


        });
        builder.setNeutralButton(R.string.show_retweets, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent newstatus = new Intent(activity, RetweetsTypes.class);
                activity.startActivity(newstatus);
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();
    }

    public static void sendStatus(Activity activity, String users, String text) {
        Entity ent = new Entity("send_tweets");
        ent.setValue("users", users);
        ent.setValue("text", text);
        ent.setValue("is_sent", 0);
        ent.setValue("type_id", 1);
        ent.setValue("username_direct", "");
        ent.setValue("photos", "");
        ent.setValue("mode_tweetlonger", NewStatus.MODE_TL_NONE);
        ent.setValue("reply_tweet_id", "-1");
        ent.setValue("use_geo", PreferenceUtils.getGeo(activity) ? "1" : "0");
        ent.save();

        activity.startService(new Intent(activity, ServiceUpdateStatus.class));
    }


    public static void sendRetweet(Activity activity, String users, long tweet_id) {
        Entity ent = new Entity("send_tweets");
        ent.setValue("users", users);
        ent.setValue("is_sent", 0);
        ent.setValue("type_id", 3);
        ent.setValue("reply_tweet_id", tweet_id);
        ent.save();

        activity.startService(new Intent(activity, ServiceUpdateStatus.class));
    }

    public static void retweetStatus(final Activity activity, final long tweet_id) {

        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        if (ents.size() == 1) {

            sendRetweet(activity, ents.get(0).getId() + "", tweet_id);

        } else {

            final UsersAdapter adapter = new UsersAdapter(activity, ents);

            AlertDialog builder = new AlertDialog.Builder(activity)
                    .setCancelable(true)
                    .setTitle(R.string.users)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendRetweet(activity, adapter.getItem(which).getId() + "", tweet_id);
                        }

                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create();
            builder.show();

        }

    }


}
