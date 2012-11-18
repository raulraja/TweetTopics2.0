package com.javielinux.api.request;

public class CreateUserListsRequest implements BaseRequest {

    private long userId = 0;
    private String title = "";
    private String description = "";
    private boolean isPublic = false;

    public CreateUserListsRequest(long userId, String title, String description, boolean aPublic) {
        this.description = description;
        isPublic = aPublic;
        this.title = title;
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
