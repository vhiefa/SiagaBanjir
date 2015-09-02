package com.digitcreativestudio.siagabanjir.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.digitcreativestudio.siagabanjir.R;

/**
 * Created by Afifatul Mukaroh
 */

public class Utility {


    public final static boolean isEmailValid(String email) {
    CharSequence target = email;
    if (target == null) {
        return false;
    } else {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}

    public final static String getLatitudefromCoordinat (String koordinat){
        String[] koordintChar = koordinat.split(",");
        return koordintChar[0];
    }

    public final static String getLongitudefromCoordinat (String koordinat){
        String[] koordintChar = koordinat.split(",");
        return koordintChar[1];
    }

   /* public static String getShorterVenue(String venue) {
        String[] titleChar = venue.split("");
        int maxTitleChar = 30;
        if (titleChar.length > maxTitleChar) {
            int i;
            venue = titleChar[1];

            for (i=2;i<=maxTitleChar-3;i++){
                venue = venue+titleChar[i];
            }

            venue = venue + "...";
        }
        return venue;
    } */

    public static Boolean getPreferredNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Boolean notif = prefs.getBoolean(context.getString(R.string.pref_enable_current_notifications_key), Boolean.valueOf(context.getString(R.string.pref_enable_current_notifications_default)));

        return notif;
    }

}
