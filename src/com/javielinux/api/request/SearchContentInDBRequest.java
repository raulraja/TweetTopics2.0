package com.javielinux.api.request;

public class SearchContentInDBRequest implements BaseRequest {

    public enum TypeContent {
        USERS, HASHTAGS
    }

    private TypeContent type = TypeContent.USERS;
    private String search = "";

    public SearchContentInDBRequest(String search, TypeContent type) {
        this.search = search;
        this.type = type;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public TypeContent getType() {
        return type;
    }

    public void setType(TypeContent type) {
        this.type = type;
    }
}
