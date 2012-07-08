package com.javielinux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.javielinux.adapters.TweetsAdapter;
import com.javielinux.api.APIDelegate;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;

public class UserListsFragment extends Fragment implements APIDelegate<BaseResponse> {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResults(BaseResponse result) {
    }

    @Override
    public void onError(ErrorResponse error) {
    }
}
