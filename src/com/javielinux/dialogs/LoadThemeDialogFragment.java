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
