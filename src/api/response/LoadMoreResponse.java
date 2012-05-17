package api.response;


import adapters.RowResponseList;

import java.util.ArrayList;

public class LoadMoreResponse implements BaseResponse {
    private ArrayList<RowResponseList> result = new ArrayList<RowResponseList>();

    public ArrayList<RowResponseList> getResult() {
        return result;
    }

    public void setResult(ArrayList<RowResponseList> result) {
        this.result = result;
    }
}
