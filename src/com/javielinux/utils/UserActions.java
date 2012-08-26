package com.javielinux.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.ColoringTweetsAdapter;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.TabNewEditSearch;
import com.javielinux.twitter.ConnectionManager;
import preferences.Colors;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;

public class UserActions {

    public static String USER_ACTION_COLORING = "coloring";
    public static String USER_ACTION_CREATE_BLOCK = "create_block";
    public static String USER_ACTION_REPORT_SPAM = "report_spam";
    public static String USER_ACTION_INCLUDED_LIST = "included_list";
    public static String USER_ACTION_HIDE = "hide";
    public static String USER_ACTION_CREATE_TOPIC = "create_topic";
    public static String USER_ACTION_SEND_DIRECT = "send_direct";
    public static String USER_ACTION_CHANGE_RELATIONSHIP = "change_relationship";

    public static InfoUsers execByCode(String code, FragmentActivity activity, InfoUsers infoUsers) {
         return execByCode(code, activity, infoUsers, null);
    }

    public static InfoUsers execByCode(String code, FragmentActivity activity, InfoUsers infoUsers, Object extra) {
        if (code.equals(USER_ACTION_COLORING)) {
            goToColoringTweets(activity, infoUsers);
        } else if (code.equals(USER_ACTION_CREATE_BLOCK)) {
            goToCreateBlock(activity, infoUsers);
        } else if (code.equals(USER_ACTION_REPORT_SPAM)) {
            goToReportSpam(activity, infoUsers);
        } else if (code.equals(USER_ACTION_INCLUDED_LIST)) {
            goToIncludeList(activity, infoUsers);
        } else if (code.equals(USER_ACTION_HIDE)) {
            goToHide(activity, infoUsers);
        } else if (code.equals(USER_ACTION_CREATE_TOPIC)) {
            goToCreateTopic(activity, infoUsers);
        } else if (code.equals(USER_ACTION_SEND_DIRECT)) {
            goToDirect(activity, infoUsers);
        } else if (code.equals(USER_ACTION_CHANGE_RELATIONSHIP)) {
            return goToChangeRelationship(activity, infoUsers, (InfoUsers.Friend)extra);
        }
        return null;
    }

    public static void goToDirect(FragmentActivity activity, InfoUsers infoUsers) {
        Intent newstatus = new Intent(activity, NewStatusActivity.class);
        newstatus.putExtra("type", NewStatusActivity.TYPE_DIRECT_MESSAGE);
        newstatus.putExtra("username_direct_message", infoUsers.getName());
        activity.startActivity(newstatus);
    }

    public static void goToCreateTopic(FragmentActivity activity, InfoUsers infoUsers) {
        Intent newsearch = new Intent(activity, TabNewEditSearch.class);
        newsearch.putExtra("user", infoUsers.getName());
        activity.startActivity(newsearch);
    }

    public static void goToHide(FragmentActivity activity, InfoUsers infoUsers) {
        Entity ent = new Entity("quiet");
        ent.setValue("word", infoUsers.getName());
        ent.setValue("type_id", 2);
        ent.save();
        Utils.showMessage(activity, activity.getString(R.string.user_hidden_correct));
    }

    public static void goToReportSpam(Context context, InfoUsers infoUsers) {
        ConnectionManager.getInstance().open(context);
        Twitter twitter = ConnectionManager.getInstance().getTwitter(DBUtils.getIdFromUserName(infoUsers.getName()));
        try {
            twitter.reportSpam(infoUsers.getName());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public static void goToIncludeList(Context context, InfoUsers infoUsers) {
        // TODO include list
    }

    public static void goToCreateBlock(Context context, InfoUsers infoUsers) {
        ConnectionManager.getInstance().open(context);
        Twitter twitter = ConnectionManager.getInstance().getTwitter(DBUtils.getIdFromUserName(infoUsers.getName()));
        try {
            twitter.createBlock(infoUsers.getName());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public static InfoUsers goToChangeRelationship(Context context, InfoUsers infoUsers, InfoUsers.Friend friend) {
        ConnectionManager.getInstance().open(context);
        Twitter twitter = ConnectionManager.getInstance().getTwitter(DBUtils.getIdFromUserName(friend.user));
        try {
            if (friend.follower) {
                twitter.destroyFriendship(infoUsers.getName());
                friend.follower = false;
            } else {
                twitter.createFriendship(infoUsers.getName());
                friend.follower = true;
            }
            infoUsers.replaceFriendly(friend.user, friend);
            return infoUsers;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void goToColoringTweets(final FragmentActivity activity, final InfoUsers infoUsers) {

        final ArrayList<Entity> entsColor = DataFramework.getInstance().getEntityList("type_colors");

        ColoringTweetsAdapter ad = new ColoringTweetsAdapter(activity, entsColor);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.colors);
        builder.setAdapter(ad, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Entity ent = new Entity("colors");
                ent.setValue("word", infoUsers.getName());
                ent.setValue("type_id", 2);
                ent.setValue("type_color_id", entsColor.get(which).getId());
                ent.save();
                Utils.showMessage(activity, activity.getString(R.string.color_add_user));

                // TODO refrescar las listas para colorear

            }


        });
        builder.setPositiveButton(R.string.new_item, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent newuser = new Intent(activity, Colors.class);
                activity.startActivity(newuser);
            }
        });
        builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Entity color = DataFramework.getInstance().getTopEntity("colors", "type_id=2 and word=\""+infoUsers.getName()+"\"", "");
                if (color!=null) {
                    color.delete();
                    Utils.showMessage(activity, activity.getString(R.string.color_delete_user));
                    // TODO refrescar las listas para dejar de colorear
                }
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create();
        builder.show();
    }

}
