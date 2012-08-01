package com.javielinux.fragments;

import android.support.v4.app.Fragment;
import com.android.dataframework.Entity;

abstract public class BaseListFragment extends Fragment {

    protected boolean flinging = false;

    public boolean isFlinging() {
        return flinging;
    }

    abstract void setFlinging(boolean flinging);

    abstract public Entity getColumnEntity();

}
