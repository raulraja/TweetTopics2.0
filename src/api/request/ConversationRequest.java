package api.request;

public class ConversationRequest implements BaseRequest {

    private long id = 0;

    public ConversationRequest(long id) {
        this.id = id;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
