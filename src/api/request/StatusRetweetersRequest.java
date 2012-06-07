package api.request;

public class StatusRetweetersRequest implements BaseRequest {

    private long id = 0;
    private long user_id = 0;

    public StatusRetweetersRequest(long user_id, long id) {
        this.id = id;
        this.user_id = user_id;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return user_id;
    }
    public void setUserId(long user_id) {
        this.user_id = user_id;
    }
}
