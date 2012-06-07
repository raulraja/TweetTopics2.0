package api.loaders;

import android.content.Context;
import api.AsynchronousLoader;
import api.request.SearchRequest;
import api.response.BaseResponse;
import api.response.ErrorResponse;
import api.response.SearchResponse;
import com.javielinux.twitter.ConnectionManager2;
import database.EntitySearch;
import infos.InfoSaveTweets;

public class SearchLoader extends AsynchronousLoader<BaseResponse> {

    private EntitySearch entitySearch;

    public SearchLoader(Context context, SearchRequest request) {
        super(context);

        this.entitySearch = request.getEntitySearch();
    }

    @Override
    public BaseResponse loadInBackground() {

        try {
            SearchResponse response = new SearchResponse();

            ConnectionManager2.getInstance().open(getContext());

            if (entitySearch.getInt("notifications")==1) {
                response.setInfoSaveTweets(entitySearch.saveTweets(getContext(), ConnectionManager2.getInstance().getAnonymousTwitter(), false));
            } else {
                response.setInfoSaveTweets(new InfoSaveTweets());

                // TODO Cargas el infoTweet con todos los tweets

                /*
                ArrayList<RowResponseList> rowResponseLists = new ArrayList<RowResponseList>();

                if (entitySearch.isUser()) {
                    // La búsqueda es de un usuario, así que buscamos en twitter directamente
                    ResponseList<Status> statuses = ConnectionManager.getInstance().getTwitter().getUserTimeline(entitySearch.getString("from_user"));
                    for (twitter4j.Status status : statuses) {
                        RowResponseList rowResponseList = new RowResponseList(status);
                        rowResponseLists.add(rowResponseList);
                    }
                } else {
                    QueryResult result = ConnectionManager.getInstance().getTwitter().search(entitySearch.getQuery(getContext()));
                    ArrayList<Tweet> tweets = (ArrayList<Tweet>)result.getTweets();
                    for (int i=0; i<tweets.size(); i++) {
                        RowResponseList rowResponseList = new RowResponseList(tweets.get(i));
                        rowResponseLists.add(rowResponseList);
                    }
                }

                response.setListAdapter(new ResponseListAdapter(getContext(), tweetTopicsCore, rowResponseLists, entitySearch.getLong("last_tweet_id")));

                */
            }

            return response;
        /*} catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());

            RateLimitStatus rate = e.getRateLimitStatus();

            if (rate!=null) {
                response.setRateError(rate);
            }

            return response;  */
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse response = new ErrorResponse();
            response.setError(e, e.getMessage());
            return response;
        }
    }
}
