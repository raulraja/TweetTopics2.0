package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.javielinux.tweettopics2.R;

public class AlertDialogFragment extends DialogFragment {

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
        int title = getArguments().getInt("title");
        int message = 0;
        if (getArguments().containsKey("message")) message = getArguments().getInt("message");

        int arrayItems = 0;
        if (getArguments().containsKey("array_items")) arrayItems = getArguments().getInt("array_items");

        int positiveLabel = R.string.alert_dialog_ok;
        if (getArguments().containsKey("positive_label")) positiveLabel = getArguments().getInt("positive_label");

        int negativeLabel = R.string.alert_dialog_cancel;
        if (getArguments().containsKey("negative_label")) negativeLabel = getArguments().getInt("negative_label");

        int neutralLabel = R.string.alert_dialog_close;
        if (getArguments().containsKey("neutral_label")) neutralLabel = getArguments().getInt("neutral_label");

        boolean hasPositiveButton = true;
        if (getArguments().containsKey("has_positive_button")) hasPositiveButton = getArguments().getBoolean("has_positive_button");

        boolean hasNegativeButton = true;
        if (getArguments().containsKey("has_negative_button")) hasNegativeButton = getArguments().getBoolean("has_negative_button");

        boolean hasNeutralButton = false;
        if (getArguments().containsKey("has_neutral_button")) hasNeutralButton = getArguments().getBoolean("has_neutral_button");

        boolean cancelable = true;
        if (getArguments().containsKey("cancelable")) cancelable = getArguments().getBoolean("cancelable");

        AlertDialog.Builder alert =  new AlertDialog.Builder(getActivity());
        alert.setTitle(title);
        if (message>0) {
            alert.setMessage(message);
        }
        if (arrayItems>0) {
            alert.setItems(arrayItems,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (alertButtonListener!=null) alertButtonListener.OnAlertItems(which);
                }
            });
        }
        if (hasPositiveButton) {
            alert.setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (alertButtonListener!=null) alertButtonListener.OnAlertButtonOk();
                    }
                });
        }
        if (hasNegativeButton) {
            alert.setNegativeButton(negativeLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (alertButtonListener!=null) alertButtonListener.OnAlertButtonCancel();
                    }
                });
        }
        if (hasNeutralButton) {
            alert.setNeutralButton(neutralLabel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (alertButtonListener!=null) alertButtonListener.OnAlertButtonNeutral();
                }
            });
        }
        alert.setCancelable(cancelable);
        return alert.create();
    }
}
