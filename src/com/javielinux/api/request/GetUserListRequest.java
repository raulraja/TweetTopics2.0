package com.javielinux.api.request;

import com.android.dataframework.Entity;

public class GetUserListRequest implements BaseRequest {

    private Entity entity = null;

    public GetUserListRequest(Entity entity) {
        this.entity = entity;

    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
