package com.javielinux.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.widget.ScrollView;
import android.widget.TextView;
import com.javielinux.tweettopics2.R;
import com.javielinux.tweettopics2.ThemeManager;

import java.util.Locale;

public class DialogUtils {


    public static class PersonalDialogBuilder {
        public static AlertDialog create( Context context, String title, String file ) throws PackageManager.NameNotFoundException {
            return create(context, title, file, false);
        }

        public static AlertDialog create( Context context, String title, String file, boolean checkTheme ) throws PackageManager.NameNotFoundException {
            String aboutTitle = String.format(title);

            String aboutText = Utils.getAsset(context, file);

            ScrollView mainView=new ScrollView(context);

            final TextView message = new TextView(context);

            mainView.addView(message);

            message.setPadding(5, 5, 5, 5);
            message.setText(aboutText);

            ThemeManager themeManager = new ThemeManager(context);
            if (checkTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && themeManager.getTheme() == ThemeManager.THEME_DEFAULT) {
                message.setTextColor(Color.BLACK);
            } else {
                message.setTextColor(Color.WHITE);
            }

            return new AlertDialog.Builder(context)
                    .setTitle(aboutTitle)
                    .setCancelable(true)
                    .setIcon(R.drawable.icon)
                    .setPositiveButton(context.getString(android.R.string.ok), null).setView(mainView).create();

        }
    }

    public static class BuyProDialogBuilder {
        public static AlertDialog create(final Context context) throws PackageManager.NameNotFoundException {

            String aboutTitle = String.format(context.getString(R.string.why_buy));

            String file = "buypro_en.txt";
            if (Locale.getDefault().getLanguage().equals("es")) {
                file = "buypro_es.txt";
            }

            String aboutText = Utils.getAsset(context, file);

            ScrollView mainView=new ScrollView(context);

            final TextView message = new TextView(context);

            mainView.addView(message);

            message.setPadding(5, 5, 5, 5);
            message.setText(aboutText);
            message.setTextColor(Color.WHITE);

            return new AlertDialog.Builder(context)
                    .setTitle(aboutTitle)
                    .setCancelable(true)
                    .setIcon(R.drawable.icon)
                    .setPositiveButton(context.getString(R.string.buy_pro), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse("market://search?q=pname:" + Utils.packageNamePRO);
                            Intent buyProIntent = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(buyProIntent);
                        }
                    })
                    .setNegativeButton(context.getString(android.R.string.cancel), null).setView(mainView).create();

        }
    }

    public static class RateAppDialogBuilder {
        public static AlertDialog create(final Context context) throws PackageManager.NameNotFoundException {
            String aboutTitle = String.format(context.getString(R.string.rate_app_title));
            String file = "rateapp_en.txt";

            if (Locale.getDefault().getLanguage().equals("es")) {
                file = "rateapp_es.txt";
            }

            String aboutText = Utils.getAsset(context, file);
            ScrollView mainView=new ScrollView(context);

            final TextView message = new TextView(context);

            mainView.addView(message);
            message.setPadding(5, 5, 5, 5);
            message.setText(aboutText);
            message.setTextColor(Color.WHITE);

            return new AlertDialog.Builder(context)
                    .setTitle(aboutTitle)
                    .setCancelable(true)
                    .setIcon(R.drawable.icon)
                    .setPositiveButton(context.getString(R.string.rate_app), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = null;
                            if (Utils.isLite(context))
                                uri = Uri.parse("market://details?id=" + Utils.packageNamePRO);
                            else
                                uri = Uri.parse("market://details?id=" + Utils.packageName);
                            Intent buyProIntent = new Intent(Intent.ACTION_VIEW, uri);
                            context.startActivity(buyProIntent);
                        }
                    })
                    .setNegativeButton(context.getString(android.R.string.cancel), null).setView(mainView).create();
        }
    }
}
