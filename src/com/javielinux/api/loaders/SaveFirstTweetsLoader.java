package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.SaveFirstTweetsRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.SaveFirstTweetsResponse;
import com.javielinux.database.EntitySearch;
import com.javielinux.tweettopics2.TabGeneral;
import com.javielinux.tweettopics2.TabNewEditSearch;
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
		    infoSaveTweets = entitySearch.saveTweets(TabNewEditSearch.StaticContext, TabGeneral.twitter, false, -1);

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
