package api.response;


import twitter4j.Status;

public class GetConversationResponse implements BaseResponse {
    private Status conversationStatus;

    public Status getConversationStatus() {
        return conversationStatus;
    }

    public void setConversationStatus(Status conversationStatus) {
        this.conversationStatus = conversationStatus;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
