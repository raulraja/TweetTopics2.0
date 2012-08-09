package com.javielinux.api.request;

import com.javielinux.database.EntitySearch;

public class TwitterUserDBRequest implements BaseRequest {

    private int column;
    private int typeUserColumn;
    private EntitySearch searchEntity;
    private long userId;

    public TwitterUserDBRequest(int column, long userId, EntitySearch searchEntity, int typeUserColumn) {
        this.column = column;
        this.searchEntity = searchEntity;
        this.userId = userId;
        this.typeUserColumn = typeUserColumn;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }

    public EntitySearch getSearchEntity() {
        return searchEntity;
    }

    public void setSearchEntity(EntitySearch searchEntity) {
        this.searchEntity = searchEntity;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getTypeUserColumn() {
        return typeUserColumn;
    }

    public void setTypeUserColumn(int typeUserColumn) {
        this.typeUserColumn = typeUserColumn;
    }
}
