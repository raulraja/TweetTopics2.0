package api.response;


import infos.InfoSaveTweets;

public class TwitterResponse implements BaseResponse {
    private long userId;
    private int column;
    private InfoSaveTweets info;


    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public InfoSaveTweets getInfo() {
        return info;
    }

    public void setInfo(InfoSaveTweets info) {
        this.info = info;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
