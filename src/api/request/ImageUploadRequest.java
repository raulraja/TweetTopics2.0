package api.request;

public class ImageUploadRequest implements BaseRequest {

    private long userId;
    private String filename;

    public ImageUploadRequest(long userId, String filename) {
        this.filename = filename;
        this.userId = userId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
