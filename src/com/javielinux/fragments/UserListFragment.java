package com.javielinux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.javielinux.adapters.UserListsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.APITweetTopics;
import com.javielinux.api.loaders.UserListsLoader;
import com.javielinux.api.request.UserListsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.UserListsResponse;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;
import twitter4j.UserList;

import java.util.ArrayList;

public class UserListFragment extends Fragment implements APIDelegate<BaseResponse> {

    private ArrayList<UserList> user_list;
    private LinearLayout viewLoading;
    private ListView list;

    private UserListsAdapter adapter;
    private InfoUsers infoUsers;

    public UserListFragment(InfoUsers infoUsers) {

        this.infoUsers = infoUsers;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user_list = new ArrayList<UserList>();
        adapter = new UserListsAdapter(getActivity(), user_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.user_list_fragment, null);

        list = ((ListView)view.findViewById(R.id.user_lists_list));

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}
        });

        viewLoading = (LinearLayout) view.findViewById(R.id.user_lists_loading);

        getUserLists();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void getUserLists() {
        if (infoUsers != null) {
            showLoading();
            APITweetTopics.execute(getActivity(), getLoaderManager(), this, new UserListsRequest(UserListsLoader.SHOW_TWEETS, "", infoUsers.getName()));
        }
    }

    public void showLoading() {
        viewLoading.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
    }

    public void showUserLists() {
        viewLoading.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResults(BaseResponse response) {

        UserListsResponse result = (UserListsResponse) response;

        for (UserList userList: result.getUserList())
            user_list.add(0, userList);

        adapter.notifyDataSetChanged();

        showUserLists();
    }

    @Override
    public void onError(ErrorResponse error) {
        error.getError().printStackTrace();
        showUserLists();
    }
}