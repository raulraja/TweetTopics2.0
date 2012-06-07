package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.TrendsLocationRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.TrendsLocationResponse;
import com.javielinux.twitter.ConnectionManager2;
import twitter4j.TwitterException;

public class TrendsLocationLoader extends AsynchronousLoader<BaseResponse> {

    public TrendsLocationLoader(Context context, TrendsLocationRequest request) {
        super(context);
    }

    @Override
    public BaseResponse loadInBackground() {
        try {
            TrendsLocationResponse response = new TrendsLocationResponse();

            ConnectionManager2.getInstance().open(getContext());

            response.setLocationList(ConnectionManager2.getInstance().getAnonymousTwitter().getAvailableTrends());
            return response;
        } catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
