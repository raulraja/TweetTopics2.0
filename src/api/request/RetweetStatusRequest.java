package api.request;

public class RetweetStatusRequest implements BaseRequest {

    private long id = 0;
    private long userId = 0;

    public RetweetStatusRequest(long userId, long id) {
        this.id = id;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
}
