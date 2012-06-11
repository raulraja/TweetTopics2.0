package api.response;


import adapters.UserTwitterListAdapter;

public class ListUserTwitterResponse implements BaseResponse {
    private UserTwitterListAdapter adapter;

    public UserTwitterListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(UserTwitterListAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
