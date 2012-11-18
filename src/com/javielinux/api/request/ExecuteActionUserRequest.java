package com.javielinux.api.request;

import com.javielinux.infos.InfoUsers;

public class ExecuteActionUserRequest implements BaseRequest {

    private InfoUsers.Friend friend;
    private InfoUsers infoUsers;
    private long userActiveId;
    private int userListId;
    private String action;

    public ExecuteActionUserRequest(String action, InfoUsers.Friend friend, InfoUsers infoUsers, long userActiveId, int userListId) {
        this.action = action;
        this.friend = friend;
        this.infoUsers = infoUsers;
        this.userActiveId = userActiveId;
        this.userListId = userListId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public InfoUsers getInfoUsers() {
        return infoUsers;
    }

    public void setInfoUsers(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }

    public InfoUsers.Friend getFriend() {
        return friend;
    }

    public void setFriend(InfoUsers.Friend friend) {
        this.friend = friend;
    }

    public long getUserActiveId() {
        return userActiveId;
    }

    public void setUserActiveId(long userActiveId) {
        this.userActiveId = userActiveId;
    }

    public int getUserListId() {
        return userListId;
    }

    public void setUserListId(int userListId) {
        this.userListId = userListId;
    }
}
