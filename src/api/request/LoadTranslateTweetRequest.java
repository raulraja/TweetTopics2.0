package api.request;

public class LoadTranslateTweetRequest implements BaseRequest {

    private long id = 0;

    public LoadTranslateTweetRequest(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
