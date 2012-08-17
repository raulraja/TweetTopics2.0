package com.javielinux.api.response;

import com.javielinux.infos.InfoUsers;

public class CheckFriendlyUserResponse implements BaseResponse {

    private InfoUsers infoUsers;

    public InfoUsers getInfoUsers() {
        return infoUsers;
    }
    public void setInfoUsers(InfoUsers infoUsers) {
        this.infoUsers = infoUsers;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
