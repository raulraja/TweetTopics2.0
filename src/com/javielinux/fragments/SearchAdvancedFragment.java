package com.javielinux.fragments;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.dataframework.DataFramework;
import com.javielinux.database.EntitySearch;
import com.javielinux.tweettopics2.R;
import com.javielinux.utils.DialogUtils;
import com.javielinux.utils.Utils;

import java.util.Locale;

public class SearchAdvancedFragment extends Fragment {

    private EntitySearch search_entity;

    private Spinner languages;
    private Spinner attitude;
    private Spinner filter;
    private CheckBox noRetweet;
    private EditText source;
    private CheckBox notifications;
    private CheckBox notificationsBar;
    private Button btInfoNotifications;

    private boolean searchIsNotification = false;

    public SearchAdvancedFragment(EntitySearch search_entity) {
        this.search_entity = search_entity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.search_advanced_fragment, null);

        btInfoNotifications = (Button)view.findViewById(R.id.bt_info_notifications);
        btInfoNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file = "notifications_use.txt";

                if (Locale.getDefault().getLanguage().equals("es")) {
                    file = "notifications_use_es.txt";
                }

                try {
                    AlertDialog builder = DialogUtils.PersonalDialogBuilder.create(getActivity(), getActivity().getString(R.string.notifications), file);
                    builder.show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        languages = (Spinner)view.findViewById(R.id.sp_languages);
        attitude = (Spinner)view.findViewById(R.id.sp_attitude);
        filter = (Spinner)view.findViewById(R.id.sp_filter);
        noRetweet = (CheckBox)view.findViewById(R.id.cb_no_retweet);
        source = (EditText)view.findViewById(R.id.et_source);

        ArrayAdapter<?> adapterLanguages = ArrayAdapter.createFromResource(getActivity(), R.array.languages, android.R.layout.simple_spinner_item);
        adapterLanguages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languages.setAdapter(adapterLanguages);

        ArrayAdapter<?> adapterAttitude = ArrayAdapter.createFromResource(getActivity(), R.array.attitude, android.R.layout.simple_spinner_item);
        adapterAttitude.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        attitude.setAdapter(adapterAttitude);

        ArrayAdapter<?> adapterFilter = ArrayAdapter.createFromResource(getActivity(), R.array.filter, android.R.layout.simple_spinner_item);
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapterFilter);

        notifications = (CheckBox)view.findViewById(R.id.cb_notifications);
        notificationsBar = (CheckBox)view.findViewById(R.id.cb_notifications_bar);

        notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
                if (isChecked) {
                    if (searchIsNotification) {
                        showFields();
                    } else {
                        int max = Utils.MAX_NOTIFICATIONS;

                        if (Utils.isLite(getActivity()) ) {
                            max = Utils.MAX_NOTIFICATIONS_LITE;
                        }

                        int size = DataFramework.getInstance().getEntityList("search", "notifications=1").size() + 1;

                        if (size <= max) {
                            showFields();
                        } else {
                            notifications.setChecked(false);
                            if (Utils.isLite(getActivity()) ) {
                                Utils.showMessage(getActivity(), getActivity().getString(R.string.max_notifications_lite));
                            } else {
                                Utils.showMessage(getActivity(), getActivity().getString(R.string.max_notifications));
                            }
                        }
                    }
                } else {
                    hideFields();
                }
            }


        });

        populateFields();

        return view;
    }

    private void showFields() {
        notificationsBar.setVisibility(View.VISIBLE);
    }

    private void hideFields() {
        notificationsBar.setVisibility(View.GONE);
    }

    private void populateFields() {
        if (!search_entity.getString("lang").equals("")) {
            String[] language_values = getActivity().getResources().getStringArray(R.array.languages_values);
            for (int i=0; i< language_values.length; i++) {
                if (language_values[i].equals(search_entity.getString("lang"))) {
                    languages.setSelection(i);
                }
            }
        }

        source.setText(search_entity.getString("source"));
        attitude.setSelection(search_entity.getInt("attitude"));
        filter.setSelection(search_entity.getInt("filter"));

        if (search_entity.getInt("no_retweet") == 1) noRetweet.setChecked(true);


        if (search_entity.getInt("notifications") == 1) {
            searchIsNotification = true;
            notifications.setChecked(true);
            showFields();
        }

        if (search_entity.getInt("notifications_bar")==1) notificationsBar.setChecked(true);
    }
}