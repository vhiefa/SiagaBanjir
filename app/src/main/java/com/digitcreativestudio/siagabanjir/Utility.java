package com.digitcreativestudio.siagabanjir;


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
