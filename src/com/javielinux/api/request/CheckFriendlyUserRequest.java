package com.javielinux.api.request;

import com.javielinux.infos.InfoUsers;

public class CheckFriendlyUserRequest implements BaseRequest {

    private String user = "";
    private String userCheck = "";
    private InfoUsers infoUsers;

    public CheckFriendlyUserRequest(InfoUsers infoUsers, String user, String userCheck) {
        this.infoUsers = infoUsers;
        this.user = user;
        this.userCheck = userCheck;
    }

    public String getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(String userCheck) {
        this.userCheck = userCheck;
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
