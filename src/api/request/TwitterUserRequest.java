package api.request;

public class TwitterUserRequest implements BaseRequest {

    private int column;

    public TwitterUserRequest(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }

}
