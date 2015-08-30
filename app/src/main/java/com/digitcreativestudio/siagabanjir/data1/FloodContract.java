package com.digitcreativestudio.siagabanjir.data1;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Eko.
 */
public class FloodContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.digitcreativestudio.siagabanjir";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FLOOD_AREA = "floodaarea";
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }


    /* Inner class that defines the table contents of the news table */
    public static final class FloodAreaEntry implements BaseColumns {

        private static final String LOG_TAG = FloodAreaEntry.class.getSimpleName();

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FLOOD_AREA).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_FLOOD_AREA;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_FLOOD_AREA;

        public static final String TABLE_NAME = "floodarea";
        public static final String COLUMN_WIL = "wilayah";
        public static final String COLUMN_KEC = "kecamatan";
        public static final String COLUMN_KEL = "kelurahan";
        public static final String COLUMN_RW = "rw";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";

        public static Uri buildNewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildNewsUniv() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildNewsUnivWithID(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static Uri buildNewsUnivWithDate(String date) {
            return CONTENT_URI.buildUpon().appendPath(date).build();
        }

        public static String getUnivSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        public static String getUniversity(Uri uri) {
            return uri.getQueryParameter(COLUMN_WIL);
        }

    }
}
