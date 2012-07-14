package com.javielinux.api.loaders;


import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadLinkRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadLinkResponse;
import com.javielinux.utils.LinksUtils;
import com.javielinux.utils.Utils;
import infos.CacheData;
import infos.InfoLink;
import infos.InfoWeb;

public class LoadLinkLoader extends AsynchronousLoader<BaseResponse> {

    private LoadLinkRequest request;

    public LoadLinkLoader(Context context, LoadLinkRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        LoadLinkResponse response = new LoadLinkResponse();

        InfoLink il = request.getInfoLink();

        if (il==null) {
            il = LinksUtils.getInfoTweet(request.getLink());
            if (il!=null) {
                CacheData.putCacheImages(request.getLink(), il);
            }
        }

        if (il!=null) {
            if (il.getType()==2) { // es un link
                try {
                    InfoWeb info = new InfoWeb(il.getLink());

                    if (!info.getTitle().equals("")) {
                        il.setTitle(info.getTitle());
                    }
                    if (!info.getDescription().equals("")) {
                        il.setDescription(info.getDescription());
                    }
                    if (!"".equals(info.getImage())) {
                        il.setLinkImageThumb(info.getImage());
                    }

                    il.setExtensiveInfo(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setError(e, e.getMessage());
                    return errorResponse;
                }

            } else {
                int h = Utils.dip2px(getContext(), Utils.HEIGHT_IMAGE);
                if (il.getType()==1) h = Utils.dip2px(getContext(), Utils.HEIGHT_VIDEO);
                il.setExtensiveInfo(true);
            }

        }

        if (il!=null) CacheData.putCacheImages(request.getLink(), il);

        response.setInfoLink(il);

        return response;


    }

}
