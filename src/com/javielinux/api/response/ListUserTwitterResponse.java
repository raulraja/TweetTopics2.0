package com.javielinux.api.response;


import com.javielinux.infos.InfoUsers;

import java.util.ArrayList;

public class ListUserTwitterResponse implements BaseResponse {
    private ArrayList<InfoUsers> users;

    public ArrayList<InfoUsers> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<InfoUsers> users) {
        this.users = users;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
