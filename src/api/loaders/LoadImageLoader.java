package api.loaders;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import api.AsynchronousLoader;
import api.request.LoadImageRequest;
import api.response.BaseResponse;
import com.javielinux.tweettopics2.TweetTopicsCore;
import com.javielinux.utils.Utils;
import infos.CacheData;
import infos.InfoLink;

import java.io.File;

public class LoadImageLoader extends AsynchronousLoader<BaseResponse> {

    private LoadImageRequest request;

    public LoadImageLoader(Context context, LoadImageRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        for (String avatar : request.getSearchAvatars()) {
            Bitmap bmp = downloadImage(avatar);

            if (bmp!=null) {
                CacheData.putCacheAvatars(avatar, bmp);
            }
        }

        for (String image : request.getSearchImages()) {
            InfoLink il = Utils.getThumbTweet(image);
            if (il!=null) {
                CacheData.putCacheImages(image, il);
            }
        }

        return null;

    }

    public Bitmap downloadImage(String u) {
        Bitmap bmp = null;

        try {
            if (TweetTopicsCore.isTypeList(TweetTopicsCore.TYPE_LIST_COLUMNUSER)) {
                File file = Utils.getFileForSaveURL(getContext(), u);
                if (!file.exists()) {
                    bmp = Utils.saveAvatar(u, file);
                } else {
                    bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                }

            } else {
                bmp = Utils.getAvatar(u);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Utils.TAG, "Could not load image.", e);
        }

        return bmp;

    }

}
