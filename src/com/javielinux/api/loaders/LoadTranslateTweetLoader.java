package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadTranslateTweetRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadTranslateTweetResponse;
import com.javielinux.utils.Utils;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import twitter4j.TwitterException;

public class LoadTranslateTweetLoader extends AsynchronousLoader<BaseResponse> {

    private LoadTranslateTweetRequest request;

    public LoadTranslateTweetLoader(Context context, LoadTranslateTweetRequest request) {
        super(context);

        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

    	try {
            LoadTranslateTweetResponse response = new LoadTranslateTweetResponse();

            String lang = request.getLanguage();

            Translate.setKey("2EFDAEA6BE06919111E8FA1FB505BF7A2FC6161B");
            Language language = Language.ENGLISH;

            if (lang.equals("en")) {
               language = Language.ENGLISH;
            } else if (lang.equals("es")) {
               language = Language.SPANISH;
            } else if (lang.equals("fr")) {
               language = Language.FRENCH;
            } else if (lang.equals("de")) {
               language = Language.GERMAN;
            } else if (lang.equals("ja")) {
               language = Language.JAPANESE;
            } else if (lang.equals("pt")) {
               language = Language.PORTUGUESE;
            } else if (lang.equals("it")) {
               language = Language.ITALIAN;
            } else if (lang.equals("ru")) {
               language = Language.RUSSIAN;
            } else if (lang.equals("id")) {
               language = Language.INDONESIAN;
            }
            response.setText(Translate.execute(request.getText(), language));
            return response;

		} catch (TwitterException twitterException) {
            twitterException.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(twitterException, twitterException.getMessage());
            return errorResponse;
		} catch (Exception exception) {
			exception.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(exception, exception.getMessage());
            return errorResponse;
		}
    }
}
