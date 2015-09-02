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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class FloodProvider extends ContentProvider {
    private final String LOG_TAG = FloodProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FloodDbHelper mOpenHelper;

    static final int FLOOD = 100;
    static final int FLOOD_BY_ID = 101;
    static final int FLOOD_AREA = 300;
   // static final int FLOOD_AREA_BY_ID = 301;
    static final int FLOOD_AREA_BY_KELURAHAN = 301;


    private static final SQLiteQueryBuilder sFloodSettingQueryBuilder;

    static{
        sFloodSettingQueryBuilder = new SQLiteQueryBuilder();
        
        // Table = flood
        sFloodSettingQueryBuilder.setTables(
                FloodContract.FloodEntry.TABLE_NAME);
        // Table = floodarea
        sFloodSettingQueryBuilder.setTables(
                FloodContract.FloodAreaEntry.TABLE_NAME);
    }

    private static final String sById =
            FloodContract.FloodEntry.TABLE_NAME +
                    "." + FloodContract.FloodEntry.COLUMN_FLOOD_ID + " = ?";

  /*  private static final String sById2 =
            FloodContract.FloodAreaEntry.TABLE_NAME +
                    "." + FloodContract.FloodAreaEntry.COLUMN_FLOOD_AREA_ID + " = ?"; */

    private static final String sByKelurahan =
            FloodContract.FloodAreaEntry.TABLE_NAME +
                    "." + FloodContract.FloodAreaEntry.COLUMN_KEL + " = ?";


    private Cursor getFlood(Uri uri, String[] projection, String sortOrder){
        return mOpenHelper.getReadableDatabase().query(
                FloodContract.FloodEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFloodArea(Uri uri, String[] projection, String sortOrder){
        return mOpenHelper.getReadableDatabase().query(
                FloodContract.FloodAreaEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getById(Uri uri, String[] projection, String sortOrder){
        String id = FloodContract.FloodEntry.getIdFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                FloodContract.FloodEntry.TABLE_NAME,
                null,
                sById,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

 /*   private Cursor getById2(Uri uri, String[] projection, String sortOrder){
        String id = FloodContract.FloodAreaEntry.getIdFromUri2(uri);
        return mOpenHelper.getReadableDatabase().query(
                FloodContract.FloodAreaEntry.TABLE_NAME,
                null,
                sById,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    } */


    private Cursor getFloodAreaByKelurahan (Uri uri, String[] projection, String sortOrder){
        String kel = FloodContract.FloodAreaEntry.getKelurahanFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                FloodContract.FloodAreaEntry.TABLE_NAME,
                null,
                sByKelurahan,
                new String[]{kel},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FloodContract.CONTENT_AUTHORITY;

        // path for each content provider
        matcher.addURI(authority, FloodContract.PATH_FLOOD, FLOOD);
        matcher.addURI(authority, FloodContract.PATH_FLOOD + "/*", FLOOD_BY_ID);

        matcher.addURI(authority, FloodContract.PATH_FLOOD_AREA, FLOOD_AREA);
        matcher.addURI(authority, FloodContract.PATH_FLOOD_AREA + "/*", FLOOD_AREA_BY_KELURAHAN);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new FloodDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FLOOD_BY_ID :
                return FloodContract.FloodEntry.CONTENT_ITEM_TYPE;
            case FLOOD:
                return FloodContract.FloodEntry.CONTENT_TYPE;
            case FLOOD_AREA_BY_KELURAHAN :
              //  return FloodContract.FloodAreaEntry.CONTENT_ITEM_TYPE;
                return FloodContract.FloodAreaEntry.CONTENT_TYPE;
            case FLOOD_AREA:
                return FloodContract.FloodAreaEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FLOOD:{
                retCursor = getFlood(uri, projection, sortOrder);
                break;
            }
            case FLOOD_BY_ID:{
                retCursor = getById(uri, projection, sortOrder);
                break;
            }
            case FLOOD_AREA:{
                retCursor = getFloodArea(uri, projection, sortOrder);
                break;
            }
            case FLOOD_AREA_BY_KELURAHAN:{
                retCursor = getFloodAreaByKelurahan(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FLOOD: {
                long _id = db.insert(FloodContract.FloodEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FloodContract.FloodEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FLOOD_AREA: {
                long _id = db.insert(FloodContract.FloodAreaEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = FloodContract.FloodAreaEntry.buildFloodArea();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case FLOOD:
                rowsDeleted = db.delete(
                        FloodContract.FloodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FLOOD_AREA:
                rowsDeleted = db.delete(
                        FloodContract.FloodAreaEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FLOOD:
                rowsUpdated = db.update(FloodContract.FloodEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FLOOD_AREA:
                rowsUpdated = db.update(FloodContract.FloodAreaEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //Log.v(LOG_TAG, String.valueOf(existing.moveToFirst()));
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FLOOD: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FloodContract.FloodEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case FLOOD_AREA: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(FloodContract.FloodAreaEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}