package com.javielinux.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.ColoringTweetsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.request.ExecuteActionUserRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.preferences.Colors;
import com.javielinux.tweettopics2.NewStatusActivity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.SearchActivity;
import com.javielinux.tweettopics2.ShowImageActivity;
import com.javielinux.twitter.ConnectionManager;
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
    public static String USER_ACTION_VIEW_PHOTO_PROFILE = "view_photo_profile";

    public static InfoUsers execByCode(String code, FragmentActivity activity, long fromUser, InfoUsers infoUsers) {
         return execByCode(code, activity, fromUser, infoUsers, null);
    }

    public static InfoUsers execByCode(String code, final FragmentActivity activity, long fromUser, InfoUsers infoUsers, Object extra) {
        if (code.equals(USER_ACTION_COLORING)) {
            goToColoringTweets(activity, infoUsers);
        } else if (code.equals(USER_ACTION_CREATE_BLOCK)) {
            APITweetTopics.execute(activity, activity.getSupportLoaderManager(), new APIDelegate() {
                @Override
                public void onResults(BaseResponse result) {
                }

                @Override
                public void onError(ErrorResponse error) {

                }
            }, new ExecuteActionUserRequest(UserActions.USER_ACTION_CREATE_BLOCK, null, infoUsers));
        } else if (code.equals(USER_ACTION_REPORT_SPAM)) {
            APITweetTopics.execute(activity, activity.getSupportLoaderManager(), new APIDelegate() {
                @Override
                public void onResults(BaseResponse result) {
                }

                @Override
                public void onError(ErrorResponse error) {

                }
            }, new ExecuteActionUserRequest(UserActions.USER_ACTION_REPORT_SPAM, null, infoUsers));
        } else if (code.equals(USER_ACTION_INCLUDED_LIST)) {
            APITweetTopics.execute(activity, activity.getSupportLoaderManager(), new APIDelegate() {
                @Override
                public void onResults(BaseResponse result) {
                }

                @Override
                public void onError(ErrorResponse error) {

                }
            }, new ExecuteActionUserRequest(UserActions.USER_ACTION_INCLUDED_LIST, null, infoUsers));
        } else if (code.equals(USER_ACTION_HIDE)) {
            goToHide(activity, infoUsers);
        } else if (code.equals(USER_ACTION_CREATE_TOPIC)) {
            goToCreateTopic(activity, infoUsers);
        } else if (code.equals(USER_ACTION_SEND_DIRECT)) {
            goToDirect(activity, fromUser, infoUsers);
        } else if (code.equals(USER_ACTION_VIEW_PHOTO_PROFILE)) {
            goToImageProfile(activity, infoUsers);
        } else if (code.equals(USER_ACTION_CHANGE_RELATIONSHIP)) {
            return goToChangeRelationship(activity, infoUsers, (InfoUsers.Friend)extra);
        }
        return null;
    }

    public static void goToImageProfile(FragmentActivity activity, InfoUsers infoUsers) {
        Intent showImage = new Intent(activity, ShowImageActivity.class);
        showImage.putExtra(ShowImageActivity.KEY_EXTRA_URL_IMAGE, infoUsers.getURLAvatar(InfoUsers.SIZE_ORIGINAL));
        activity.startActivity(showImage);
    }

    public static void goToDirect(FragmentActivity activity, long fromUser, InfoUsers infoUsers) {
        Intent newstatus = new Intent(activity, NewStatusActivity.class);
        if (fromUser>0) newstatus.putExtra("start_user_id", fromUser);
        newstatus.putExtra("type", NewStatusActivity.TYPE_DIRECT_MESSAGE);
        newstatus.putExtra("username_direct_message", infoUsers.getName());
        activity.startActivity(newstatus);
    }

    public static void goToCreateTopic(FragmentActivity activity, InfoUsers infoUsers) {
        Intent edit_search = new Intent(activity, SearchActivity.class);
        edit_search.putExtra("user", infoUsers.getName());
        activity.startActivity(edit_search);
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
            Utils.showMessage(context, context.getString(R.string.user_report_spam));
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
            if (twitter.existsBlock(infoUsers.getName())) {
                twitter.destroyBlock(infoUsers.getName());
                Utils.showMessage(context, context.getString(R.string.user_unlock));
            } else {
                twitter.createBlock(infoUsers.getName());
                Utils.showMessage(context, context.getString(R.string.user_block));
            }
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
