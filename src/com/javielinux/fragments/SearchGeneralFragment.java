package com.javielinux.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import com.android.dataframework.Entity;
import com.javielinux.database.EntitySearch;
import com.javielinux.dialogs.SelectIconDialogFragment;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.Utils;

public class SearchGeneralFragment extends Fragment {

    private EntitySearch search_entity;

    public EditText name;
    public EditText searchOr;
    public EditText searchAnd;
    public EditText searchNot;
    public EditText searchFromUser;
    public EditText searchToUser;
    public ImageButton btIcons;
    public long iconId;
    public String iconFile;


    public SearchGeneralFragment(EntitySearch search_entity) {
        this.search_entity = search_entity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.search_general_fragment, null);

        ThemeManager mThemeManager = new ThemeManager(getActivity());
        mThemeManager.setTheme();

        btIcons = (ImageButton) view.findViewById(R.id.bt_icon);
        btIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectIconDialogFragment frag = new SelectIconDialogFragment();
                frag.setSelectIconListener(new SelectIconDialogFragment.SelectIconListener() {
                    @Override
                    public void onSelectIcon(long id) {
                        selectIcon(id);
                    }
                });
                frag.show(getActivity().getSupportFragmentManager(), "dialog");
            }
        });

        name = (EditText) view.findViewById(R.id.et_name);
        searchAnd = (EditText) view.findViewById(R.id.et_words_and);
        searchOr = (EditText) view.findViewById(R.id.et_words_or);
        searchNot = (EditText) view.findViewById(R.id.et_words_not);
        searchFromUser = (EditText) view.findViewById(R.id.et_from_user);
        searchToUser = (EditText) view.findViewById(R.id.et_to_user);

        populateFields();

        return view;
    }

    private void populateFields() {
        if (search_entity.isUpdate()) {
            name.setText(search_entity.getString("name"));
            searchOr.setText(search_entity.getString("words_or"));
            searchAnd.setText(search_entity.getString("words_and"));
            searchNot.setText(search_entity.getString("words_not"));
            searchFromUser.setText(search_entity.getString("from_user"));
            searchToUser.setText(search_entity.getString("to_user"));
            btIcons.setImageDrawable(Utils.getDrawable(getActivity(), search_entity.getString("icon_big")));
            iconId = search_entity.getLong("icon_id");
            iconFile = search_entity.getString("icon_token_file");
        } else {
            selectIcon(1);
        }
    }

    public void selectIcon(long id) {
        iconId = id;
        iconFile = "";
        Entity icon = new Entity("icons", id);
        btIcons.setImageDrawable(icon.getDrawable("icon"));
    }

    public void searchIcon() {
        final EditText et = new EditText(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(this.getString(R.string.search_avatar));
        builder.setMessage(this.getString(R.string.search_avatar_msg));
        builder.setView(et);
        builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchAvatarInTwitter(et.getText().toString());
            }

        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void searchAvatarInTwitter(String text) {
        // TODO SearchGeneralFragment: searchAvatarInTwitter
    }
}
