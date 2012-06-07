package api.request;

public class CheckConversationRequest implements BaseRequest {

    private long userId = 0;
    private int from = 0;
    private long conversation = 0;

    public CheckConversationRequest(long userId, int from, long conversation) {
        this.userId = userId;
        this.from = from;
        this.conversation = conversation;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public long getConversation() {
        return conversation;
    }

    public void setConversation(long conversation) {
        this.conversation = conversation;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
