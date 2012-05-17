package api.response;


import android.graphics.Bitmap;

public class LoadImageWidgetResponse implements BaseResponse {
    private Bitmap bitmap;
    private String url;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
