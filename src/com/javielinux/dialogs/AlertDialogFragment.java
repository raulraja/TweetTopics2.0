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
import com.javielinux.tweettopics2.R;

import java.util.List;

public class AlertDialogFragment extends DialogFragment {

    public static final String KEY_ALERT_TITLE = "title";
    public static final String KEY_ALERT_TITLE_TEXT = "title_text";
    public static final String KEY_ALERT_MESSAGE = "message";
    public static final String KEY_ALERT_ARRAY_ITEMS = "array_items";
    public static final String KEY_ALERT_ARRAY_STRING_ITEMS = "array_items_string";
    public static final String KEY_ALERT_POSITIVE_LABEL = "positive_label";
    public static final String KEY_ALERT_NEGATIVE_LABEL = "negative_label";
    public static final String KEY_ALERT_NEUTRAL_LABEL = "neutral_label";
    public static final String KEY_ALERT_HAS_POSITIVE_BUTTON = "has_positive_button";
    public static final String KEY_ALERT_HAS_NEGATIVE_BUTTON = "has_negative_button";
    public static final String KEY_ALERT_HAS_NEUTRAL_BUTTON = "has_neutral_button";
    public static final String KEY_ALERT_CANCELABLE = "cancelable";

    public void setAlertButtonListener(AlertButtonListener alertButtonListener) {
        this.alertButtonListener = alertButtonListener;
    }

    public interface AlertButtonListener {
        void OnAlertButtonOk();

        void OnAlertButtonCancel();

        void OnAlertButtonNeutral();

        void OnAlertItems(int which);
    }

    private AlertButtonListener alertButtonListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = 0;
        if (getArguments().containsKey(KEY_ALERT_TITLE)) title = getArguments().getInt(KEY_ALERT_TITLE);

        String titleTxt = null;
        if (getArguments().containsKey(KEY_ALERT_TITLE_TEXT)) titleTxt = getArguments().getString(KEY_ALERT_TITLE_TEXT);

        int message = 0;
        if (getArguments().containsKey(KEY_ALERT_MESSAGE)) message = getArguments().getInt(KEY_ALERT_MESSAGE);

        int arrayItems = 0;
        if (getArguments().containsKey(KEY_ALERT_ARRAY_ITEMS))
            arrayItems = getArguments().getInt(KEY_ALERT_ARRAY_ITEMS);

        List<String> arrayItemString = null;
        if (getArguments().containsKey(KEY_ALERT_ARRAY_STRING_ITEMS))
            arrayItemString = getArguments().getStringArrayList(KEY_ALERT_ARRAY_STRING_ITEMS);

        int positiveLabel = R.string.alert_dialog_ok;
        if (getArguments().containsKey(KEY_ALERT_POSITIVE_LABEL))
            positiveLabel = getArguments().getInt(KEY_ALERT_POSITIVE_LABEL);

        int negativeLabel = R.string.alert_dialog_cancel;
        if (getArguments().containsKey(KEY_ALERT_NEGATIVE_LABEL))
            negativeLabel = getArguments().getInt(KEY_ALERT_NEGATIVE_LABEL);

        int neutralLabel = R.string.alert_dialog_close;
        if (getArguments().containsKey(KEY_ALERT_NEUTRAL_LABEL))
            neutralLabel = getArguments().getInt(KEY_ALERT_NEUTRAL_LABEL);

        boolean hasPositiveButton = true;
        if (getArguments().containsKey(KEY_ALERT_HAS_POSITIVE_BUTTON))
            hasPositiveButton = getArguments().getBoolean(KEY_ALERT_HAS_POSITIVE_BUTTON);

        boolean hasNegativeButton = true;
        if (getArguments().containsKey(KEY_ALERT_HAS_NEGATIVE_BUTTON))
            hasNegativeButton = getArguments().getBoolean(KEY_ALERT_HAS_NEGATIVE_BUTTON);

        boolean hasNeutralButton = false;
        if (getArguments().containsKey(KEY_ALERT_HAS_NEUTRAL_BUTTON))
            hasNeutralButton = getArguments().getBoolean(KEY_ALERT_HAS_NEUTRAL_BUTTON);

        boolean cancelable = true;
        if (getArguments().containsKey(KEY_ALERT_CANCELABLE))
            cancelable = getArguments().getBoolean(KEY_ALERT_CANCELABLE);

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        if (title > 0) {
            alert.setTitle(title);
        }
        if (titleTxt != null) {
            alert.setTitle(titleTxt);
        }
        if (message > 0) {
            alert.setMessage(message);
        }
        if (arrayItems > 0) {
            alert.setItems(arrayItems, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (alertButtonListener != null) alertButtonListener.OnAlertItems(which);
                }
            });
        }
        if (arrayItemString != null) {
            CharSequence[] charSequences = new CharSequence[arrayItemString.size()];
            int count = 0;
            for (String item : arrayItemString) {
                charSequences[count] = item;
                count++;
            }
            alert.setItems(charSequences, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (alertButtonListener != null) alertButtonListener.OnAlertItems(which);
                }
            });
        }
        if (hasPositiveButton) {
            alert.setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (alertButtonListener != null) alertButtonListener.OnAlertButtonOk();
                }
            });
        }
        if (hasNegativeButton) {
            alert.setNegativeButton(negativeLabel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (alertButtonListener != null) alertButtonListener.OnAlertButtonCancel();
                }
            });
        }
        if (hasNeutralButton) {
            alert.setNeutralButton(neutralLabel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (alertButtonListener != null) alertButtonListener.OnAlertButtonNeutral();
                }
            });
        }
        alert.setCancelable(cancelable);
        return alert.create();
    }
}
