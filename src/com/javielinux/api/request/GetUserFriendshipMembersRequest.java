package com.javielinux.api.request;

public class GetUserFriendshipMembersRequest implements BaseRequest {

    private int type = 0;
    private String user = "";

    public GetUserFriendshipMembersRequest(int type, String user) {
        this.type = type;
        this.user = user;
    }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetUserFriendshipMembersRequest that = (GetUserFriendshipMembersRequest) o;

        if (type != that.type) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }
}
