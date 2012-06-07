package api.request;

public class DirectMessageRequest implements BaseRequest {

    private long userId = 0;
    private int modeTweetLonger = 0;
    private String text = "";
    private String user = "";

    public DirectMessageRequest(long userId, int modeTweetLonger, String user, String text) {
        this.modeTweetLonger = modeTweetLonger;
        this.userId = userId;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
