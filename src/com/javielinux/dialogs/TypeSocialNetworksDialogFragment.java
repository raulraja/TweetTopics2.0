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

package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.javielinux.adapters.IconAndTextSimpleAdapter;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class TypeSocialNetworksDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener onClickListener;

    public TypeSocialNetworksDialogFragment() {
        super();
    }

    public TypeSocialNetworksDialogFragment(DialogInterface.OnClickListener onClickListener) {
        super();
        this.onClickListener = onClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        ArrayList<IconAndTextSimpleAdapter.IconAndText> items = new ArrayList<IconAndTextSimpleAdapter.IconAndText>();
        IconAndTextSimpleAdapter.IconAndText twitter = new IconAndTextSimpleAdapter.IconAndText();
        twitter.resource = R.drawable.icon_twitter_large;
        twitter.text = getString(R.string.twitter_network);
        items.add(twitter);
        IconAndTextSimpleAdapter.IconAndText facebook = new IconAndTextSimpleAdapter.IconAndText();
        facebook.resource = R.drawable.icon_facebook_large;
        facebook.text = getString(R.string.facebook_network);
        items.add(facebook);

        IconAndTextSimpleAdapter adapter = new IconAndTextSimpleAdapter(getActivity(), items);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setAdapter(adapter, onClickListener)
                .create();
    }

}
