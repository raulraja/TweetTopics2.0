package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.RetweetStatusRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.RetweetStatusResponse;
import com.javielinux.twitter.ConnectionManager;
import twitter4j.TwitterException;

public class RetweetStatusLoader extends AsynchronousLoader<BaseResponse> {

    private long id = 0;

    public RetweetStatusLoader(Context context, RetweetStatusRequest request) {

        super(context);

        this.id = request.getId();
    }

    @Override
    public BaseResponse loadInBackground() {

        //TODO: Comprobar el valor devuelto con el valor esperado (error - ready)

		try {
            RetweetStatusResponse response = new RetweetStatusResponse();

			ConnectionManager.getInstance().open(getContext());
			ConnectionManager.getInstance().getTwitter().retweetStatus(id);

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
