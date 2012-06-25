package com.javielinux.api.loaders;


import android.content.Context;
import android.graphics.BitmapFactory;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadImageAutoCompleteRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadImageAutoCompleteResponse;
import com.javielinux.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImageAutoCompleteLoader extends AsynchronousLoader<BaseResponse> {

    private LoadImageAutoCompleteRequest request;

    public LoadImageAutoCompleteLoader(Context context, LoadImageAutoCompleteRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            LoadImageAutoCompleteResponse response = new LoadImageAutoCompleteResponse();
            URL url = new URL(request.getUrl());
            response.setBitmap(BitmapFactory.decodeStream(new Utils.FlushedInputStream(url.openStream())));
            return response;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        } catch (IOException e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        }


    }

}
