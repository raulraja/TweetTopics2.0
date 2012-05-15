package api.api.loaders;

import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.tweettopics2.Utils;
import infos.CacheData;

public class PreparingLinkForSidebarLoader extends AsynchronousLoader<APIResult> {

    private String link = "";

    public PreparingLinkForSidebarLoader(Context context, Bundle bundle) {
        super(context);

        this.link = bundle.getString("link");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();

		try {
			CacheData.putCacheImages(link, Utils.getThumbTweet(link));

            out.addParameter("ready", true);
            return out;
		} catch (Exception e) {
			out.setError(e, e.getMessage());
            return out;
		}
    }
}
