package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.StatusRetweetersRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.StatusRetweetersResponse;
import com.javielinux.twitter.ConnectionManager2;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class StatusRetweetersLoader extends AsynchronousLoader<BaseResponse> {

    private StatusRetweetersRequest request;

    public StatusRetweetersLoader(Context context, StatusRetweetersRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

		try {
			ConnectionManager2.getInstance().open(getContext());
            ResponseList<User> retweeters_list = ConnectionManager2.getInstance().getTwitter(request.getUserId()).getRetweetedBy(request.getId(), new Paging(1, 100));

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
