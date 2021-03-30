package com.smarttech.story.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.smarttech.story.R;

import java.text.Collator;
import java.util.Locale;

public class CommonUtils {
    public static final Collator COLLATOR = Collator.getInstance(Locale.FRANCE);


    public static void goToMyStore(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(context
                .getString(R.string.store_url)));
        context.startActivity(intent);
    }

    public static void ratingApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri
                .parse(context.getString(R.string.app_url)));
        context.startActivity(intent);
    }

    public static void shareApp(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");
        String shareMessage = context.getString(R.string.share_message);
        shareMessage = shareMessage + context.getString(R.string.app_url);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                shareMessage);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                context.getString(R.string.share_app_title));

        try {
            context.startActivity(Intent.createChooser(shareIntent,
                    context.getString(R.string.action_share)));
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }


    public static boolean checkNetworkStatus(Context context) {

        ConnectivityManager connectivityManager;

        boolean connected = false;

        try {

            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable()
                    && networkInfo.isConnected();

        } catch (Exception e) {
            System.out
                    .println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }

        return connected;

    }
}
