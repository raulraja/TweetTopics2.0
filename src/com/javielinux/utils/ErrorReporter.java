package com.javielinux.utils;


import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import com.javielinux.tweettopics2.R;
import com.javielinux.twitter.TwitterApplication;

import java.io.*;

public class ErrorReporter implements Thread.UncaughtExceptionHandler
{
    Thread.UncaughtExceptionHandler originalHandler;
    TwitterApplication app;
    public static String FILENAME_ERROR = "stack-tweettopics.stacktrace";
    private static ErrorReporter mInstance;

    public ErrorReporter(Thread.UncaughtExceptionHandler originalHandler, Application app){
		this.originalHandler = originalHandler;
        this.app = (TwitterApplication) app;
	}

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.d(Utils.TAG,"Force Close in TweetTopics");
        PreferenceUtils.setFinishForceClose(app.getApplicationContext(), true);
        StringBuilder sb = new StringBuilder();
        sb.append("Package App: " + Utils.packageName);
        try {
            sb.append("\rVersion manifest: " + app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sb.append("\rVersion App: " + app.getApplicationContext().getString(R.string.version));
        sb.append("\rAndroid version: " + Build.VERSION.RELEASE);
        sb.append("\rPhone: " + Build.MODEL);
        try
        {
            PackageManager pm = app.getApplicationContext().getPackageManager();
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(app.getApplicationContext().getPackageName(), 0);
            sb.append("\rBoard: " + android.os.Build.BOARD);
            sb.append("\rBrand: " + android.os.Build.BRAND);
            sb.append("\rDevice: " + android.os.Build.DEVICE);
            sb.append("\rDisplay: " + android.os.Build.DISPLAY);
            sb.append("\rFingerPrint: " + android.os.Build.FINGERPRINT);
            sb.append("\rHost: " + android.os.Build.HOST);
            sb.append("\rId: " + android.os.Build.ID);
            sb.append("\rProduct: " + android.os.Build.PRODUCT);
            sb.append("\rTags: " + android.os.Build.TAGS);
            sb.append("\rTime: " + android.os.Build.TIME);
            sb.append("\rType: " + android.os.Build.TYPE);
            sb.append("\rUser: " + android.os.Build.USER);

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        sb.append("\r--------------------------------------\r");
        sb.append(Log.getStackTraceString(throwable));

        SaveAsFile(sb.toString());

        //Resume original error
        originalHandler.uncaughtException(thread, throwable);
    }


    private void SaveAsFile( String errorContent )
    {
        try {
            FileWriter fileWriter = new FileWriter(new File(app.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS), "/" + FILENAME_ERROR));
            BufferedWriter out = new BufferedWriter(fileWriter);
            out.write(errorContent);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getErrors(Context cnt) {
        try {
            String errorText = "";
            BufferedReader input = new BufferedReader(new FileReader(cnt.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS) + "/" + FILENAME_ERROR));
            String line;
            while (( line = input.readLine()) != null)
            {
                errorText += line + "\n";
            }
            input.close();
            return errorText;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
