package com.javielinux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;

public class NoFoundFragment extends Fragment {

    private String text;

    public NoFoundFragment(String text) {
        this.text = text;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.no_found_fragment, null);
        ((TextView)view.findViewById(R.id.text)).setText(getString(R.string.column_no_found, text));
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
