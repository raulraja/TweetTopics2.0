package com.javielinux.utils;

public interface ListFragmentListener {
    public static int SAVED_ON_LIST_VIEW = -1;
    public static int FORCE_FIRST_VISIBLE = -2;
    void onMarkPositionLastReadAsLastReadId(int position);
}
