package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;

public class CreateListTwitterDialogFragment extends DialogFragment {

    public interface CreateListListener {
        void onCreateList(String title, String description, boolean isPublic);
    }

    private CreateListListener onCreateListListener;

    public CreateListTwitterDialogFragment(CreateListListener onCreateListListener) {
        super();
        this.onCreateListListener = onCreateListListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ViewGroup view = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.create_list_dialog, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.newList)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = ((TextView) view.findViewById(R.id.dialog_title)).getText().toString();
                        String description = ((TextView) view.findViewById(R.id.dialog_description)).getText().toString();
                        boolean isPublic = ((CheckBox) view.findViewById(R.id.dialog_is_public)).isChecked();
                        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
                            onCreateListListener.onCreateList(title, description, isPublic);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

}
