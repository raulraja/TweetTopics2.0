package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.StatusRetweetersRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.StatusRetweetersResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class StatusRetweetersLoader extends AsynchronousLoader<BaseResponse> {

    private long id = 0;

    public StatusRetweetersLoader(Context context, StatusRetweetersRequest request) {
        super(context);

        this.id = request.getId();
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
			ConnectionManager.getInstance().open(getContext());
            ResponseList<User> retweeters_list = ConnectionManager.getInstance().getTwitter().getRetweetedBy(id, new Paging(1, 100));

            StatusRetweetersResponse response = new StatusRetweetersResponse();
            response.setUserList(retweeters_list);
            return response;
		} catch (TwitterException e) {
			e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
		}
    }
}
