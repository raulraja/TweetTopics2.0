package api.loaders;

import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import api.request.SaveFirstTweetsRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.SaveFirstTweetsResponse;
import com.javielinux.tweettopics2.TabGeneral;
import com.javielinux.tweettopics2.TabNewEditSearch;
import database.EntitySearch;
import infos.InfoSaveTweets;

public class SaveFirstTweetsLoader extends AsynchronousLoader<BaseResponse> {

    private long id = 0;

    public SaveFirstTweetsLoader(Context context, SaveFirstTweetsRequest request) {
        super(context);

        this.id = request.getId();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            SaveFirstTweetsResponse response = new SaveFirstTweetsResponse();
            InfoSaveTweets infoSaveTweets = null;

		    EntitySearch entitySearch = new EntitySearch(id);
		    infoSaveTweets = entitySearch.saveTweets(TabNewEditSearch.StaticContext, TabGeneral.twitter, false);

            response.setInfoSaveTweets(infoSaveTweets);
            return response;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(exception, exception.getMessage());
            return response;
        }
    }
}
