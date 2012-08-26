package com.javielinux.api.request;

import com.javielinux.infos.InfoUsers;

public class ExecuteActionUserRequest implements BaseRequest {

    private InfoUsers.Friend friend;
    private InfoUsers infoUsers;
    private String action;

    public ExecuteActionUserRequest(String action, InfoUsers.Friend friend, InfoUsers infoUsers) {
        this.action = action;
        this.friend = friend;
        this.infoUsers = infoUsers;
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
}
