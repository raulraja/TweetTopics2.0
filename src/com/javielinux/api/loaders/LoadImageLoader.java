package com.javielinux.api.loaders;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadImageRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.LoadImageResponse;
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
            Log.d(Utils.TAG,"Descargar avatar: " + avatar);
            Bitmap bmp = downloadImage(avatar);

            if (bmp!=null) {
                CacheData.putCacheAvatars(avatar, bmp);
            }
        }

        for (String image : request.getSearchImages()) {
            Log.d(Utils.TAG,"Descargar image: " + image);
            InfoLink il = Utils.getThumbTweet(image);
            if (il!=null) {
                CacheData.putCacheImages(image, il);
            }
        }

        return new LoadImageResponse();

    }

    public Bitmap downloadImage(String u) {
        Bitmap bmp = null;

        try {

            File file = Utils.getFileForSaveURL(getContext(), u);
            if (!file.exists()) {
                bmp = Utils.saveAvatar(u, file);
            } else {
                bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Utils.TAG, "Could not load image.", e);
        }

        return bmp;

    }

}
