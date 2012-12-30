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

package com.javielinux.utils;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;
import com.javielinux.database.EntitySearch;
import com.javielinux.database.EntityTweetUser;

public class DBUtils {

    public static long getIdFromUserName(String name) {
        long id = 0;
        Entity entity = DataFramework.getInstance().getTopEntity("users", "name = '"+name+"'", "");
        if (entity!=null) {
            id = entity.getId();
        }
        return id;
    }

    public static int getUnreadTweetsUser(int column, long id) {
        return new EntityTweetUser(id, ColumnsUtils.convertColumnInType(column)).getValueNewCount();
    }

    public static int getUnreadTweetsSearch(long id) {
        return new EntitySearch(id).getValueNewCount();
    }

}
