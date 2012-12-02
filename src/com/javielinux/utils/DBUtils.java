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
