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
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.IconAndTextSimpleAdapter;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ImageUtils;
import com.javielinux.utils.Utils;

import java.util.ArrayList;

public class TwitterUsersConnectedDialogFragment extends DialogFragment {

    private OnSelectedIconAndText onSelectedIconAndText;
    private ArrayList<IconAndTextSimpleAdapter.IconAndText> items;

    public TwitterUsersConnectedDialogFragment(OnSelectedIconAndText onSelectedIconAndText) {
        super();
        this.onSelectedIconAndText = onSelectedIconAndText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        items = new ArrayList<IconAndTextSimpleAdapter.IconAndText>();

        ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("users", "service is null or service = \"twitter.com\"");

        for (Entity ent : ents) {
            IconAndTextSimpleAdapter.IconAndText item = new IconAndTextSimpleAdapter.IconAndText();
            item.bitmap = ImageUtils.getBitmapAvatar(ent.getId(), Utils.AVATAR_LARGE);
            item.text = ent.getString("name");
            item.extra = ent.getId();
            items.add(item);
        }

        IconAndTextSimpleAdapter adapter = new IconAndTextSimpleAdapter(getActivity(), items);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.users)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onSelectedIconAndText.OnSelectedItem(items.get(i));
                    }
                })
                .create();
    }

}
