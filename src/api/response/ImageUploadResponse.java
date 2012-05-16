package api.response;

import android.graphics.Bitmap;

public class ImageUploadResponse implements BaseResponse {
    private String url;
    private String file;
    private Bitmap bmp;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }
}
