package com.javielinux.utils;

import android.app.AlertDialog;
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
import preferences.Colors;

import java.util.ArrayList;

public class UserActions {

    public static boolean execByCode(String code, FragmentActivity activity, InfoUsers infoUsers) {
        if (code.equals("coloring")) {
            goToColoringTweets(activity, infoUsers);
        } else if (code.equals("create_block")) {
            goToCreateBlock(activity, infoUsers);
        } else if (code.equals("report_spam")) {
            goToReportSpam(activity, infoUsers);
        } else if (code.equals("included_list")) {
            goToIncludeList(activity, infoUsers);
        } else if (code.equals("hide")) {
            goToHide(activity, infoUsers);
        } else if (code.equals("create_topic")) {
            goToCreateTopic(activity, infoUsers);
        } else if (code.equals("send_direct")) {
            goToDirect(activity, infoUsers);
        }
        return false;
    }

    private static void goToDirect(FragmentActivity activity, InfoUsers infoUsers) {
        Intent newstatus = new Intent(activity, NewStatusActivity.class);
        newstatus.putExtra("type", NewStatusActivity.TYPE_DIRECT_MESSAGE);
        newstatus.putExtra("username_direct_message", infoUsers.getName());
        activity.startActivity(newstatus);
    }

    private static void goToCreateTopic(FragmentActivity activity, InfoUsers infoUsers) {
        Intent newsearch = new Intent(activity, TabNewEditSearch.class);
        newsearch.putExtra("user", infoUsers.getName());
        activity.startActivity(newsearch);
    }

    private static void goToHide(FragmentActivity activity, InfoUsers infoUsers) {
        Entity ent = new Entity("quiet");
        ent.setValue("word", infoUsers.getName());
        ent.setValue("type_id", 2);
        ent.save();
        Utils.showMessage(activity, activity.getString(R.string.user_hidden_correct));
    }

    private static void goToReportSpam(FragmentActivity activity, InfoUsers infoUsers) {
        // TODO report spam
    }

    private static void goToIncludeList(FragmentActivity activity, InfoUsers infoUsers) {
        // TODO include list
    }

    private static void goToCreateBlock(FragmentActivity activity, InfoUsers infoUsers) {
        // TODO create block
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
