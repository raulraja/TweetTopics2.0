package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.javielinux.tweettopics2.R;

public class BuyProDialogFragment extends DialogFragment {

    private String file;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(R.string.buyKeyMessage)
                .setPositiveButton(R.string.buyKey, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri uri = Uri.parse("market://search?q=pname:com.javielinux.tweettopics.pro");
                        Intent buyProIntent = new Intent(Intent.ACTION_VIEW, uri);
                        getActivity().startActivity(buyProIntent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }
}
