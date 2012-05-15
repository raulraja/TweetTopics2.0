package api.request;

public class CheckConversationRequest implements BaseRequest {

    private int from = 0;
    private long conversation = 0;

    public CheckConversationRequest(int from, long conversation) {
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
}
