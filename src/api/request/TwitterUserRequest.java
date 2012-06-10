package api.request;

public class TwitterUserRequest implements BaseRequest {

    private int column;
    private long userId;

    public TwitterUserRequest(int column, long userId) {
        this.column = column;
        this.userId = userId;
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
}
