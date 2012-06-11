package api.response;

import infos.InfoSaveTweets;

public class TwitterUserResponse implements BaseResponse {

    private long user_id = -1;
    private InfoSaveTweets info = null;
    private int column = 0;

    public long getUserId() {
        return user_id;
    }
    public void setUserId(long user_id) {
        this.user_id = user_id;
    }

    public InfoSaveTweets getInfo() {
        return info;
    }
    public void setInfo(InfoSaveTweets info) {
        this.info = info;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
