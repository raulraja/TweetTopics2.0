package api.request;

public class GetConversationRequest implements BaseRequest {

    private long id = 0;

    public GetConversationRequest(long id) {
        this.id = id;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
