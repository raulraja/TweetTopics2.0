package com.javielinux.utils;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtils {

    public static void copy(String source, String destiny) throws IOException {
        try
        {
            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(destiny);
            FileChannel channelSource = fis.getChannel();
            FileChannel channelDestiny = fos.getChannel();
            channelSource.transferTo(0, channelSource.size(), channelDestiny);
            fis.close();
            fos.close();
        }
        catch(Exception e)
        {
            Log.d(Utils.TAG, e.getClass().getName());
        }
    }

}
