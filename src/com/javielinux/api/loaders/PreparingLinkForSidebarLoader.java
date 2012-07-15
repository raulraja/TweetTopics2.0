package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.PreparingLinkForSidebarRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.PreparingLinkForSidebarResponse;
import com.javielinux.utils.CacheData;
import com.javielinux.utils.LinksUtils;

public class PreparingLinkForSidebarLoader extends AsynchronousLoader<BaseResponse> {

    private String link = "";

    public PreparingLinkForSidebarLoader(Context context, PreparingLinkForSidebarRequest request) {
        super(context);

        this.link = request.getLink();
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
            PreparingLinkForSidebarResponse response = new PreparingLinkForSidebarResponse();
			CacheData.putCacheInfoLinks(link, LinksUtils.getInfoTweet(link));

            response.setReady(true);
            return response;
		} catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
			response.setError(e, e.getMessage());
            return response;
		}
    }
}
