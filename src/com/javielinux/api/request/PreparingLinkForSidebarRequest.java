package com.javielinux.api.request;

public class PreparingLinkForSidebarRequest implements BaseRequest {

    private String link = "";

    public PreparingLinkForSidebarRequest(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
}
