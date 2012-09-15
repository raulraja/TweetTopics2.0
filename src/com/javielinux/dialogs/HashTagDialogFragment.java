package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.SearchActivity;
import com.javielinux.tweettopics2.TweetTopicsActivity;
import com.javielinux.utils.PreferenceUtils;
import com.javielinux.utils.TweetActions;
import com.javielinux.utils.Utils;

public class HashTagDialogFragment extends DialogFragment {
    private String hashtag = "";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        hashtag = getArguments().getString("hashtag");

        return new AlertDialog.Builder(getActivity())
                .setTitle(hashtag)
                .setItems( R.array.items_hashtag_actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {
                            Entity ent = new Entity("search");
                            ent.setValue("date_create", Utils.now());
                            ent.setValue("last_modified", Utils.now());
                            ent.setValue("use_count", 0);
                            ent.setValue("is_temp", 1);
                            ent.setValue("icon_id", 1);
                            ent.setValue("icon_big", "drawable/letter_hash");
                            ent.setValue("icon_small", "drawable/letter_hash_small");
                            ent.setValue("name", hashtag);
                            ent.setValue("words_and", hashtag);
                            ent.save();
                            // TODO ir del tirón a ver el hashtag
                        } else if (which==1) {
                            Entity ent = new Entity("quiet");
                            ent.setValue("word", hashtag);
                            ent.setValue("type_id", 1);
                            ent.save();
                            Utils.showMessage(getActivity(), getActivity().getString(R.string.hashtag_hidden_correct));
                        } else if (which==2) {
                            Intent edit_search = new Intent(getActivity(), SearchActivity.class);
                            edit_search.putExtra("search", hashtag);
                            startActivityForResult(edit_search, TweetTopicsActivity.ACTIVITY_NEWEDITSEARCH);
                        } else if (which==3) {
                            TweetActions.updateStatus(getActivity(), hashtag);
                        } else if (which==4) {
                            PreferenceUtils.setDefaultTextInTweet(getActivity(), hashtag);
                        }
                    }
                })
                .create();
    }
}
