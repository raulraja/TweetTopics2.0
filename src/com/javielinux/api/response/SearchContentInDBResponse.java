package com.javielinux.api.response;


import java.util.List;

public class SearchContentInDBResponse implements BaseResponse {
    private List<Object> objectList;

    public List<Object> getObjectList() {
        return objectList;
    }

    public void setObjectList(List<Object> objectList) {
        this.objectList = objectList;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
