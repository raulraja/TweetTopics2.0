package api.loaders;

import android.content.Context;
import android.os.Bundle;
import api.APIResult;
import api.AsynchronousLoader;
import com.javielinux.tweettopics2.TabGeneral;
import com.javielinux.tweettopics2.TabNewEditSearch;
import database.EntitySearch;
import infos.InfoSaveTweets;

public class SaveFirstTweetsLoader extends AsynchronousLoader<APIResult> {

    private long id = 0;

    public SaveFirstTweetsLoader(Context context, Bundle bundle) {
        super(context);

        this.id = bundle.getLong("id");
    }

    @Override
    public APIResult loadInBackground() {
        APIResult out = new APIResult();
        InfoSaveTweets infoSaveTweets = null;

        try
        {
		    EntitySearch entitySearch = new EntitySearch(id);
		    infoSaveTweets = entitySearch.saveTweets(TabNewEditSearch.StaticContext, TabGeneral.twitter, false);

            out.addParameter("info_save_tweet", infoSaveTweets);
            return out;
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            out.setError(exception, exception.getMessage());
            return out;
        }
    }
}
