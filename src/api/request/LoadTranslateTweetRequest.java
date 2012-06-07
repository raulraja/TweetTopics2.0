package api.request;

public class LoadTranslateTweetRequest implements BaseRequest {

    private String text = "";

    public LoadTranslateTweetRequest(String text) {
        this.text = text;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
