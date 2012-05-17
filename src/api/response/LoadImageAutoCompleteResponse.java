package api.response;


import android.graphics.Bitmap;

public class LoadImageAutoCompleteResponse implements BaseResponse {
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
