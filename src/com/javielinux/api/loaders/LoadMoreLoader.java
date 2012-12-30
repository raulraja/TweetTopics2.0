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
import com.javielinux.api.request.LoadMoreRequest;
import com.javielinux.api.response.BaseResponse;
import com.javielinux.api.response.LoadMoreResponse;

public class LoadMoreLoader extends AsynchronousLoader<BaseResponse> {

    private LoadMoreRequest request;

    public LoadMoreLoader(Context context, LoadMoreRequest request) {
        super(context);
        this.request = request;
    }

    @Override
    public BaseResponse loadInBackground() {

        // TODO esto hay que hacerlo... hasta los cojones ya

        //try {

            LoadMoreResponse response = new LoadMoreResponse();
            /*
            ArrayList<RowResponseList> result = new ArrayList<RowResponseList>();

            ConnectionManager.getInstance().open(getContext());
            if (request.getTargetId()>0) {
                if (request.getTypeList()== TweetTopicsCore.TYPE_LIST_COLUMNUSER) {
                    if (request.getTypeLastColumn()==TweetTopicsCore.TIMELINE) {
                        ResponseList<Status> statii = ConnectionManager.getInstance().getTwitter().getHomeTimeline(new Paging(1).maxId(request.getTargetId()));
                        if (statii.size()>1) {
                            for (int i=1; i<statii.size(); i++) {
                                result.add(new RowResponseList(statii.get(i)));
                            }
                        }
                    } else if (request.getTypeLastColumn()==TweetTopicsCore.MENTIONS) {
                        ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getMentions(new Paging(1).maxId(request.getTargetId()));
                        if (statii.size()>1) {
                            for (int i=1; i<statii.size(); i++) {
                                result.add(new RowResponseList(statii.get(i)));
                            }
                        }
                    } else if (request.getTypeLastColumn()==TweetTopicsCore.DIRECTMESSAGES) {
                        ResponseList<DirectMessage> dms = ConnectionManager.getInstance().getTwitter().getDirectMessages(new Paging(1).maxId(request.getTargetId()));
                        if (dms.size()>1) {
                            for (int i=1; i<dms.size(); i++) {
                                result.add(new RowResponseList(dms.get(i)));
                            }
                        }
                    }
                }
                if (request.getTypeList()==TweetTopicsCore.TYPE_LIST_LISTUSERS) {
                    ResponseList<twitter4j.Status> statii = ConnectionManager.getInstance().getTwitter().getUserListStatuses(
                            TweetTopicsCore.mCurrentList.getId(), new Paging(1).maxId(request.getTargetId()));
                    if (statii.size()>1) {
                        for (int i=1; i<statii.size(); i++) {
                            result.add(new RowResponseList(statii.get(i)));
                        }
                    }
                }
            }

            response.setResult(result);
            */

            return response;

        /*} catch (TwitterException e) {
            e.printStackTrace();
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(e, e.getMessage());
            return errorResponse;
        }  */

    }

}
