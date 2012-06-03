package api.loaders;


import android.content.Context;
import android.graphics.BitmapFactory;
import api.AsynchronousLoader;
import api.request.LoadImageWidgetRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.LoadImageWidgetResponse;
import com.javielinux.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImageWidgetLoader extends AsynchronousLoader<BaseResponse> {

    private LoadImageWidgetRequest request;

    public LoadImageWidgetLoader(Context context, LoadImageWidgetRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            LoadImageWidgetResponse response = new LoadImageWidgetResponse();
            response.setUrl(request.getUrl());
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
