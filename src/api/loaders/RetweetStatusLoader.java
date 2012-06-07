package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.RetweetStatusRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.RetweetStatusResponse;
import com.javielinux.twitter.ConnectionManager2;
import twitter4j.TwitterException;

public class RetweetStatusLoader extends AsynchronousLoader<BaseResponse> {

    private RetweetStatusRequest request;

    public RetweetStatusLoader(Context context, RetweetStatusRequest request) {

        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        //TODO: Comprobar el valor devuelto con el valor esperado (error - ready)

		try {
            RetweetStatusResponse response = new RetweetStatusResponse();

			ConnectionManager2.getInstance().open(getContext());
			ConnectionManager2.getInstance().getTwitter(request.getUserId()).retweetStatus(request.getId());

            response.setReady(true);
            return response;
		} catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
		}
    }
}
