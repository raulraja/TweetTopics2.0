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
