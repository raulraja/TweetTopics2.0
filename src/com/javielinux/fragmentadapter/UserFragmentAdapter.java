package com.javielinux.fragmentadapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.javielinux.fragments.UserInfoFragment;
import com.javielinux.fragments.UserTimelineFragment;
import com.javielinux.infos.InfoUsers;
import com.javielinux.tweettopics2.R;

import java.util.ArrayList;

public class UserFragmentAdapter extends FragmentPagerAdapter  {

    public static int TAB_INFO = 0;
    public static int TAB_TIMELINE = 1;

    private InfoUsers infoUsers;

    private ArrayList<Integer> user_fragment_code = new ArrayList<Integer>();
    private ArrayList<String> user_fragment_titles = new ArrayList<String>();

    private Context context;

    public UserFragmentAdapter(Context context, FragmentManager fragmentManager, InfoUsers infoUsers) {
        super(fragmentManager);
        this.infoUsers = infoUsers;

        this.context = context;

        loadColumns();
    }

    void loadColumns() {

        user_fragment_code.add(TAB_INFO);
        user_fragment_titles.add(context.getString(R.string.user));

        user_fragment_code.add(TAB_TIMELINE);
        user_fragment_titles.add(context.getString(R.string.timeline));

    }


    @Override
    public Fragment getItem(int index) {
       if (user_fragment_code.get(index)==TAB_INFO) {
            return new UserInfoFragment();
       } else if (user_fragment_code.get(index)==TAB_TIMELINE) {
           return new UserTimelineFragment();
       }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return user_fragment_code.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return user_fragment_titles.get(position % user_fragment_code.size()).toUpperCase();
    }
}
