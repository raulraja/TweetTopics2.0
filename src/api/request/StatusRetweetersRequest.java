package api.request;

public class StatusRetweetersRequest implements BaseRequest {

    private long id = 0;

    public StatusRetweetersRequest(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
