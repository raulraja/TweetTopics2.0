package com.javielinux.api.request;

import com.javielinux.infos.InfoUsers;

public class CheckFriendlyUserRequest implements BaseRequest {

    private String user = "";
    private InfoUsers infoUsers;

    public CheckFriendlyUserRequest(InfoUsers infoUsers, String user) {
        this.infoUsers = infoUsers;
        this.user = user;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user.replace("@", "");
    }

    public InfoUsers getInfoUsers() {
        return infoUsers;
    }

    public void setInfoUsers(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }
}
