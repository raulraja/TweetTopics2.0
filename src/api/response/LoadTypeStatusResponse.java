package api.response;

import adapters.RowResponseList;

import java.util.ArrayList;

public class LoadTypeStatusResponse implements BaseResponse {

    private ArrayList<RowResponseList> rowResponseListArrayList;

    public ArrayList<RowResponseList> getRowResponseList() {
        return rowResponseListArrayList;
    }
    public void setRowResponseList(ArrayList<RowResponseList> list) {
        this.rowResponseListArrayList = list;
    }
}
