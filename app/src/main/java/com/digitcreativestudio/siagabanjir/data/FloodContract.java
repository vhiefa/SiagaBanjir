/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.digitcreativestudio.siagabanjir.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class FloodContract {

    public static final String CONTENT_AUTHORITY = "com.digitcreativestudio.siagabanjir";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FLOOD = "flood";
    public static final String PATH_FLOOD_AREA = "floodarea";

    /* Inner class that defines the table contents of the news table */
    public static final class FloodAreaEntry implements BaseColumns {


        private static final String LOG_TAG = FloodAreaEntry.class.getSimpleName();

        public static final Uri CONTENT_URI_2 =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FLOOD_AREA).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_FLOOD_AREA;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_FLOOD_AREA;

        public static final String TABLE_NAME = "floodarea";
        public static final String COLUMN_FLOOD_AREA_ID = "id_floodarea";
        public static final String COLUMN_WIL = "wilayah";
        public static final String COLUMN_KEC = "kecamatan";
        public static final String COLUMN_KEL = "kelurahan";
        public static final String COLUMN_RW = "rw";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";

        public static Uri buildNewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI_2, id);
        }

        public static Uri buildFloodArea() {
            return CONTENT_URI_2.buildUpon().build();
        }

        public static String getIdFromUri2(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildFloodAreaWithID(String id) {
            return CONTENT_URI_2.buildUpon().appendPath(id).build();
        }

    }

    public static final class FloodEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FLOOD).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLOOD;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FLOOD;

        public static final String TABLE_NAME = "flood";

        public static final String COLUMN_FLOOD_ID ="id_flood";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_CAPTION = "caption";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_PHOTO = "photo";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

     /*   public static Uri buildFloodUri(String lat, String lon) {
            return CONTENT_URI.buildUpon().appendPath(lat).appendPath(lon).build();
        } */

        public static Uri buildFloodById(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

      /*  public static String getLatitude(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getLongitude(Uri uri){
            return uri.getPathSegments().get(2);
        }*/

    }

}
