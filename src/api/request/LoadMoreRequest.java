package api.request;

public class LoadMoreRequest implements BaseRequest {

    private long targetId;
    private int typeList;
    private int typeLastColumn;

    public LoadMoreRequest(long targetId, int typeList, int typeLastColumn) {
        this.targetId = targetId;
        this.typeList = typeList;
        this.typeLastColumn = typeLastColumn;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public int getTypeList() {
        return typeList;
    }

    public void setTypeList(int typeList) {
        this.typeList = typeList;
    }

    public int getTypeLastColumn() {
        return typeLastColumn;
    }

    public void setTypeLastColumn(int typeLastColumn) {
        this.typeLastColumn = typeLastColumn;
    }
}
