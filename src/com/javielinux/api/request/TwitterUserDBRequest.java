package com.javielinux.api.request;

public class TwitterUserDBRequest implements BaseRequest {

    private int column;
    private int typeUserColumn;
    private long userId;

    public TwitterUserDBRequest(int column, long userId, int typeUserColumn) {
        this.column = column;
        this.userId = userId;
        this.typeUserColumn = typeUserColumn;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
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
