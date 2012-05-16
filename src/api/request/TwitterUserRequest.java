package api.request;

public class TwitterUserRequest implements BaseRequest {

    private int column;
    private boolean loadOtherColumns;

    public TwitterUserRequest(int column, boolean loadOtherColumns) {
        this.column = column;
        this.loadOtherColumns = loadOtherColumns;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }

    public boolean getLoadOtherColumns() {
        return loadOtherColumns;
    }
    public void setLoadOtherColumns(boolean loadOtherColumns) {
        this.loadOtherColumns = loadOtherColumns;
    }
}
