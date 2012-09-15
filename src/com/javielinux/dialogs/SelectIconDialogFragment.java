package com.javielinux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.adapters.ImageResourcesAdapter;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class SelectIconDialogFragment extends DialogFragment {

    public interface SelectIconListener {
        void onSelectIcon(long id);
    }

    private SelectIconListener selectIconListener;

    public void setSelectIconListener(SelectIconListener selectIconListener) {
        this.selectIconListener = selectIconListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = R.string.select_icon;

        final ArrayList<Entity> icons = DataFramework.getInstance().getEntityList("icons");
        ArrayList<Integer> resources = new ArrayList<Integer>();

        for (Entity icon : icons) {
            resources.add(getResources().getIdentifier(DataFramework.getInstance().getPackage() + ":drawable/" + icon.getString("icon"), null, null));
        }

        ImageResourcesAdapter adapter = new ImageResourcesAdapter(getActivity(), resources);

        GridView gridView = new GridView(getActivity());
        gridView.setNumColumns(3);
        gridView.setPadding(5,5,5,5);
        gridView.setVerticalSpacing(5);
        gridView.setGravity(Gravity.CENTER);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (selectIconListener != null) {
                    selectIconListener.onSelectIcon(icons.get(pos).getId());
                }
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(gridView)
                .create();
    }
}
