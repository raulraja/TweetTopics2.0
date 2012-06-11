package api.response;


public class DirectMessageResponse implements BaseResponse {
    private boolean sent;

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    @Override
    public boolean isError() {
        return false;
    }
}
