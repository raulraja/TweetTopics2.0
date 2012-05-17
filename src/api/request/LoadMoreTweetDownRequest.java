package api.request;

public class LoadMoreTweetDownRequest implements BaseRequest {

    private long sinceId;
    private long maxId;
    private int pos;
    private int count;
    private long userId;

    public LoadMoreTweetDownRequest(long userId, long sinceId, long maxId, int pos, int count) {
        this.sinceId = sinceId;
        this.maxId = maxId;
        this.pos = pos;
        this.count = count;
    }


    public long getSinceId() {
        return sinceId;
    }

    public void setSinceId(long sinceId) {
        this.sinceId = sinceId;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
