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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.digitcreativestudio.siagabanjir.data.FloodContract.FloodEntry;

/**
 * Manages a local database for weather data.
 */
public class FloodDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "flood.db";

    public FloodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        final String SQL_CREATE_FLOOD_TABLE = "CREATE TABLE " + FloodEntry.TABLE_NAME + " (" +

                FloodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + //id untuk database lokal

                FloodEntry.COLUMN_FLOOD_ID + " TEXT NOT NULL, " + //id dari database server
                FloodEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                FloodEntry.COLUMN_CAPTION + " TEXT NOT NUll, " +
                FloodEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                FloodEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                FloodEntry.COLUMN_PHOTO + " TEXT NOT NULL, " +
                FloodEntry.COLUMN_NEW + " INTEGER DEFAULT 1, " +
                
                "UNIQUE (" + FloodEntry.COLUMN_FLOOD_ID + ") ON CONFLICT REPLACE);"; //agar data dengan id dr server yang sama tidak menduplikat di database lokal

        sqLiteDatabase.execSQL(SQL_CREATE_FLOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FloodEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
