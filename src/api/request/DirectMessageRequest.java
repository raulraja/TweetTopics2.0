package api.request;

public class DirectMessageRequest implements BaseRequest {

    private int modeTweetLonger = 0;
    private String text = "";
    private String user = "";

    public DirectMessageRequest(int modeTweetLonger, String user, String text) {
        this.modeTweetLonger = modeTweetLonger;
        this.user = user;
        this.text = text;
    }


    public int getModeTweetLonger() {
        return modeTweetLonger;
    }

    public void setModeTweetLonger(int modeTweetLonger) {
        this.modeTweetLonger = modeTweetLonger;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
