package com.digitcreativestudio.siagabanjir.utils;


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
        return koordintChar[1];
    }

    public final static String getLongitudefromCoordinat (String koordinat){
        String[] koordintChar = koordinat.split(",");
        return koordintChar[2];
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



}