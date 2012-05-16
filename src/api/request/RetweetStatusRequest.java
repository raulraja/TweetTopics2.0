package api.request;

public class RetweetStatusRequest implements BaseRequest {

    private long id = 0;

    public RetweetStatusRequest(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
