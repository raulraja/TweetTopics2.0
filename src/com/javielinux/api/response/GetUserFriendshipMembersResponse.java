package com.javielinux.api.response;

public class GetUserFriendshipMembersResponse implements BaseResponse {

    private long[] friendshipMembersIds;

    public long[] getFriendshipMembersIds() {
        return friendshipMembersIds;
    }
    public void setFriendshipMembersIds(long[] list) {
        this.friendshipMembersIds = list;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
