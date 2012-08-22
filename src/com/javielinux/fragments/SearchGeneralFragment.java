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
import com.javielinux.database.EntitySearch;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.Utils;

public class SearchGeneralFragment extends Fragment {

    private EntitySearch search_entity;

    private EditText name;
    private EditText searchOr;
    private EditText searchAnd;
    private EditText searchNot;
    private EditText searchFromUser;
    private EditText searchToUser;
    private ImageButton btIcons;
    private EditText iconId;
    private EditText iconFile;

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

        btIcons = (ImageButton)view.findViewById(R.id.bt_icon);
        btIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}
        });

        iconId = (EditText)view.findViewById(R.id.icon_id);
        iconId.setText("1");

        iconFile = (EditText)view.findViewById(R.id.icon_file);

        name = (EditText)view.findViewById(R.id.et_name);
        searchAnd = (EditText)view.findViewById(R.id.et_words_and);
        searchOr = (EditText)view.findViewById(R.id.et_words_or);
        searchNot = (EditText)view.findViewById(R.id.et_words_not);
        searchFromUser = (EditText)view.findViewById(R.id.et_from_user);
        searchToUser = (EditText)view.findViewById(R.id.et_to_user);

        populateFields();

        return view;
    }

    private void populateFields() {
        name.setText(search_entity.getString("name"));
        searchOr.setText(search_entity.getString("words_or"));
        searchAnd.setText(search_entity.getString("words_and"));
        searchNot.setText(search_entity.getString("words_not"));
        searchFromUser.setText(search_entity.getString("from_user"));
        searchToUser.setText(search_entity.getString("to_user"));
        btIcons.setImageDrawable(Utils.getDrawable(getActivity(), search_entity.getString("icon_big")));
        //iconId.setText(search_entity.getString("icon_id"));
        iconFile.setText(search_entity.getString("icon_token_file"));
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
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void searchAvatarInTwitter(String text) {
        // TODO SearchGeneralFragment: searchAvatarInTwitter
    }
}
