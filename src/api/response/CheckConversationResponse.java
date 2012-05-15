package api.response;


import twitter4j.Status;

public class CheckConversationResponse implements BaseResponse {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
