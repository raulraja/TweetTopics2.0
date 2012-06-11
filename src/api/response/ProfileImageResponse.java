package api.response;

public class ProfileImageResponse implements BaseResponse {

    private boolean ready;

    public boolean getReady() {
        return ready;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public boolean isError() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
