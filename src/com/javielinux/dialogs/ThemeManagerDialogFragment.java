package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.javielinux.preferences.ColorsApp;
import com.javielinux.task.IntentIntegrator;
import com.javielinux.tweettopics2.R;

import java.util.concurrent.Callable;

public class ThemeManagerDialogFragment extends DialogFragment {

    public static final int ACTIVITY_COLORS_APP = 1201;
    private Callable callable;

    public ThemeManagerDialogFragment(Callable callable) {
         this.callable = callable;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.themes)
                .setItems( R.array.items_manager_theme, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {
                            Intent colorsApp = new Intent(getActivity(), ColorsApp.class);
                            getActivity().startActivityForResult(colorsApp, ACTIVITY_COLORS_APP);
                        } else if (which==1) {
                            try {
                                callable.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (which==2) {
                            IntentIntegrator.initiateScan(getActivity(), R.string.title_download_scan, R.string.msg_download_scan, R.string.alert_dialog_ok, R.string.alert_dialog_cancel);
                        } else if (which==3) {
                            ColorsApp.exportTheme(getActivity());
                        }
                    }
                })
                .create();
    }
}
