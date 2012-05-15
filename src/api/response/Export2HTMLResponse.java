package api.response;


public class Export2HTMLResponse implements BaseResponse {
    private boolean sent;

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
