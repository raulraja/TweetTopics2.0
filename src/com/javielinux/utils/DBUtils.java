package com.javielinux.utils;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class DBUtils {

    public static long getIdFromUserName(String name) {
        long id = 0;
        Entity entity = DataFramework.getInstance().getTopEntity("users", "name = '"+name+"'", "");
        if (entity!=null) {
            id = entity.getId();
        }
        return id;
    }

}
