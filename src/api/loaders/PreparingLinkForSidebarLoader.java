package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.PreparingLinkForSidebarRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.PreparingLinkForSidebarResponse;
import com.javielinux.utils.Utils;
import infos.CacheData;

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
			CacheData.putCacheImages(link, Utils.getThumbTweet(link));

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
