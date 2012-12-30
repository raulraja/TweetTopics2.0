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
import android.view.View;
import android.widget.TextView;
import com.android.dataframework.Entity;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.ColumnsUtils;

import java.util.concurrent.Callable;

public class EditColumnDialogFragment extends DialogFragment {

    private Entity entity;
    private Callable callable;

    public EditColumnDialogFragment(Entity entity, Callable callable) {
        this.entity = entity;
        this.callable = callable;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.edit;

        View v = View.inflate(getActivity(), R.layout.edit_column_dialog, null);

        final TextView name = (TextView)v.findViewById(R.id.edit_column_name);
        name.setText(ColumnsUtils.getTitleColumn(getActivity(), entity));

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(v)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        entity.setValue("description", name.getText().toString());
                        entity.setValue("user_desc_as_title", 1);
                        entity.save();
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}
