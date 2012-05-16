package api.request;

public class ImageUploadRequest implements BaseRequest {

    private String filename;

    public ImageUploadRequest(String filename) {
        this.filename = filename;

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
