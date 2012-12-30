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
import com.javielinux.preferences.ColorsApp;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LoadThemeDialogFragment extends DialogFragment {

    private Callable callable;

    public LoadThemeDialogFragment(Callable callable) {
        this.callable = callable;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList<Entity> ents = DataFramework.getInstance().getEntityList("themes");
        CharSequence[] c = new CharSequence[ents.size()];
        for (int i = 0; i < ents.size(); i++) {
            c[i] = ents.get(i).getString("name");
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.load_theme)
                .setItems(c, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ColorsApp.loadTheme(getActivity(), ents.get(which).getString("theme"));
                        Utils.showShortMessage(getActivity(), getActivity().getString(R.string.refresh_theme));
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .create();
    }
}
