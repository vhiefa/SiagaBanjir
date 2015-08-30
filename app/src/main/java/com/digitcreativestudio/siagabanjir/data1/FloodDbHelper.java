package com.digitcreativestudio.siagabanjir.data1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.digitcreativestudio.siagabanjir.data1.FloodContract.FloodAreaEntry;

/**
 * Created by Eko.
 */
public class FloodDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "floodarea.db";

    public FloodDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations. A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        // TBD
        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + FloodAreaEntry.TABLE_NAME + " (" +
                FloodAreaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // the ID of the location entry associated with this news data
                FloodAreaEntry.COLUMN_WIL + " TEXT NOT NULL, " +
                FloodAreaEntry.COLUMN_KEC + " TEXT NOT NULL, " +
                FloodAreaEntry.COLUMN_KEL + " TEXT NOT NULL, " +
                FloodAreaEntry.COLUMN_RW + " TEXT NOT NULL, " +
                FloodAreaEntry.COLUMN_LONGITUDE + " TEXT NOT NULL," +
                FloodAreaEntry.COLUMN_LATITUDE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL(" DROP TABLE IF EXIST " + FloodAreaEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
