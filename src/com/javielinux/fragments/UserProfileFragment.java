package com.javielinux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.javielinux.adapters.UserProfileAdapter;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;

public class UserProfileFragment extends Fragment {

    private InfoUsers infoUsers;
    private UserProfileAdapter adapter;
    private ListView list;

    public UserProfileFragment(InfoUsers infoUsers) {
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
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
