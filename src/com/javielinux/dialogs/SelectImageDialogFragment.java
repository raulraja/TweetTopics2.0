package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import com.javielinux.tweettopics2.R;

import java.io.File;

public class SelectImageDialogFragment extends DialogFragment {

    public static final int ACTIVITY_SELECTIMAGE = 1001;
    public static final int ACTIVITY_CAMERA = 1002;

    private long idUser = 0;
    private String file;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        idUser = getArguments().getLong("id_user");
        file = getArguments().getString("file");

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setItems( R.array.select_type_image, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            File f = new File(file);
                            if (f.exists()) f.delete();

                            Intent intendCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intendCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            intendCapture.putExtra("return-data", true);
                            startActivityForResult(intendCapture, ACTIVITY_CAMERA);
                        } else if (which == 1) {
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    MediaStore.Images.Media.CONTENT_TYPE);
                            startActivityForResult(i, ACTIVITY_SELECTIMAGE);
                        }
                    }
                })
                .create();
    }
}
