package api.api.loaders;

import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class RetweetStatusLoader extends AsynchronousLoader<APIResult> {

    private long id = 0;

    public RetweetStatusLoader(Context context, Bundle bundle) {

        super(context);

        this.id = bundle.getLong("id");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();

		try {
			ConnectionManager.getInstance().open(getContext());
			ConnectionManager.getInstance().getTwitter().retweetStatus(id);

            out.addParameter("ready", true);
            return out;
		} catch (TwitterException e) {
			out.setError(e, e.getMessage());
            return out;
		}
    }
}
