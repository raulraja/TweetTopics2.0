package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.twitter.ConnectionManager;
import com.javielinux.utils.TweetTopicsUtils;
import com.javielinux.utils.Utils;
import twitter4j.TwitterException;

public class CreateDefaultColumnsUserDialogFragment extends DialogFragment {

    private long userId;
    private Entity userEntity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        userId = getArguments().getLong("user_id");

        userEntity = new Entity("users", userId);

        CharSequence[] choices = new CharSequence[3];
        choices[0] = getString(R.string.timeline);
        choices[1] = getString(R.string.mentions);
        choices[2] = getString(R.string.direct_messages);

        final boolean[] isChoices = new boolean[]{true, true, true};

        LinearLayout llTitle = new LinearLayout(getActivity());
        llTitle.setOrientation(LinearLayout.VERTICAL);
        final CheckBox boxInvite = new CheckBox(getActivity());
        boxInvite.setText(R.string.follow_tweettopics);
        boxInvite.setChecked(true);
        llTitle.addView(boxInvite);
        TextView txtTitle = new TextView(getActivity());
        txtTitle.setText(R.string.create_columns);
        txtTitle.setTextSize(25);
        llTitle.addView(txtTitle);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(llTitle);
        builder.setMultiChoiceItems(choices, isChoices,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                        isChoices[whichButton] = isChecked;
                    }
                });
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // choices
                int count = DataFramework.getInstance().getEntityListCount("columns", "") + 1;
                if (isChoices[0]) {
                    Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_TIMELINE);
                    Entity timeline = new Entity("columns");
                    timeline.setValue("description", type.getString("description"));
                    timeline.setValue("type_id", type);
                    timeline.setValue("position", count);
                    timeline.setValue("user_id", userEntity.getId());
                    timeline.save();
                    count++;
                }
                if (isChoices[1]) {
                    Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_MENTIONS);
                    Entity mentions = new Entity("columns");
                    mentions.setValue("description", type.getString("description"));
                    mentions.setValue("type_id", type);
                    mentions.setValue("position", count);
                    mentions.setValue("user_id", userEntity.getId());
                    mentions.save();
                    count++;
                }
                if (isChoices[2]) {
                    Entity type = new Entity("type_columns", (long) TweetTopicsUtils.COLUMN_DIRECT_MESSAGES);
                    Entity dms = new Entity("columns");
                    dms.setValue("description", type.getString("description"));
                    dms.setValue("type_id", type);
                    dms.setValue("position", count);
                    dms.setValue("user_id", userEntity.getId());
                    dms.save();
                }

                // create friend
                if (boxInvite.isChecked()) {
                    try {
                        ConnectionManager.getInstance().getTwitter(userEntity.getId()).createFriendship("tweettopics_app");
                    } catch (TwitterException e1) {
                        e1.printStackTrace();
                    }
                    Utils.showMessage(getActivity(), getActivity().getString(R.string.thanks));
                }
            }

        });

        return builder.create();

    }
}
