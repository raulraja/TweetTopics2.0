package com.javielinux.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.javielinux.adapters.UserProfileAdapter;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;
import com.javielinux.utils.ImageUtils;

public class UserProfileFragment extends Fragment {

    private InfoUsers infoUsers;
    private UserProfileAdapter adapter;
    private ListView list;

    public UserProfileFragment() {
        super();
    }

    public UserProfileFragment(InfoUsers infoUsers) {
        super();

        this.infoUsers = infoUsers;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new UserProfileAdapter(getActivity(), infoUsers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.user_profile_fragment, null);

        list = (ListView) view.findViewById(R.id.user_profile_list);
        // poner estilo de la listas de las preferencias del usuario
        ThemeManager themeManager = new ThemeManager(getActivity());
        list.setDivider(ImageUtils.createDividerDrawable(getActivity(), themeManager.getColor("color_divider_tweet")));
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("prf_use_divider_tweet", true)) {
            list.setDividerHeight(2);
        } else {
            list.setDividerHeight(0);
        }
        list.setFadingEdgeLength(6);
        list.setCacheColorHint(themeManager.getColor("color_shadow_listview"));
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
