package com.javielinux.api.response;

import twitter4j.UserList;

import java.util.ArrayList;

public class GetUserListResponse implements BaseResponse {

    private boolean ready;
    private long nextCursor = -1;
    private ArrayList<UserList> userListArrayList = new ArrayList<UserList>();

    public boolean getReady() {
        return this.ready;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public long getNextCursor() {
        return this.nextCursor;
    }

    public void setNextCursor(long nextCursor) {
        this.nextCursor = nextCursor;
    }

    public ArrayList<UserList> getUserListArrayList() {
        return this.userListArrayList;
    }

    public void setUserListArrayList(ArrayList<UserList> userListArrayList) {
        this.userListArrayList = userListArrayList;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
