package com.javielinux.api.request;

import java.util.ArrayList;

public class LoadImageRequest implements BaseRequest {

    private ArrayList<String> searchAvatars;
    private ArrayList<String> searchImages;

    public LoadImageRequest(ArrayList<String> searchAvatars, ArrayList<String> searchImages) {
        this.searchAvatars = searchAvatars;
        this.searchImages = searchImages;
    }


    public ArrayList<String> getSearchAvatars() {
        return searchAvatars;
    }

    public void setSearchAvatars(ArrayList<String> searchAvatars) {
        this.searchAvatars = searchAvatars;
    }

    public ArrayList<String> getSearchImages() {
        return searchImages;
    }

    public void setSearchImages(ArrayList<String> searchImages) {
        this.searchImages = searchImages;
    }
}
