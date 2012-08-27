package com.javielinux.api.request;

import com.javielinux.infos.InfoUsers;

public class UserMentionsRequest implements BaseRequest {

    private InfoUsers infoUsers = null;

    public UserMentionsRequest(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }

    public InfoUsers getInfoUsers() {
        return this.infoUsers;
    }
    public void setInfoUsers(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }
}
