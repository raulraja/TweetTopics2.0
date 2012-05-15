package api.loaders;

import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class StatusRetweetersLoader extends AsynchronousLoader<APIResult> {

    private long id = 0;

    public StatusRetweetersLoader(Context context, Bundle bundle) {
        super(context);

        this.id = bundle.getLong("id");
    }

    @Override
    public APIResult loadInBackground() {

        APIResult out = new APIResult();

		try {
			ConnectionManager.getInstance().open(getContext());
            ResponseList<User> retweeters_list = ConnectionManager.getInstance().getTwitter().getRetweetedBy(id, new Paging(1, 100));

            out.addParameter("retweeters_list", retweeters_list);
            return out;
		} catch (TwitterException e) {
			e.printStackTrace();
            out.setError(e, e.getMessage());
            return out;
		}
    }
}
