package api.response;

public class PreparingLinkForSidebarResponse implements BaseResponse {

    private boolean ready;

    public boolean getReady() {
        return ready;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
