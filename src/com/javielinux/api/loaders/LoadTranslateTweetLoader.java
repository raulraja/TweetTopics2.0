/*
 * Copyright 2012 Javier Pérez Pacheco and Francisco Díaz Rodriguez
 * TweetTopics 2.0
 * javielinux@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javielinux.api.loaders;

import android.content.Context;
import com.javielinux.api.AsynchronousLoader;
import com.javielinux.api.request.LoadTranslateTweetRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.ErrorResponse;
import com.javielinux.api.response.LoadTranslateTweetResponse;
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
