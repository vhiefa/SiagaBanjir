package com.digitcreativestudio.siagabanjir;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.digitcreativestudio.siagabanjir.data1.FloodContract.FloodAreaEntry;
import com.digitcreativestudio.siagabanjir.data1.FloodDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by Eko.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(FloodDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new FloodDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }
    public void testInsertReadDb() {
        // Test data we're going to insert into the DB to see if it works.
        String link = "http://www.google.com/";
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        FloodDbHelper dbHelper = new FloodDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues newsValues = new ContentValues();
        newsValues.put(FloodAreaEntry.COLUMN_WIL, "Ini wilayah");
        newsValues.put(FloodAreaEntry.COLUMN_KEC, "Ini kecamatan");
        newsValues.put(FloodAreaEntry.COLUMN_KEL, "Ini kelurahan");
        newsValues.put(FloodAreaEntry.COLUMN_RW, "RW");
        newsValues.put(FloodAreaEntry.COLUMN_LONGITUDE, "Si longi");
        newsValues.put(FloodAreaEntry.COLUMN_LATITUDE, "Si lati");


        long newsRowId = db.insert(FloodAreaEntry.TABLE_NAME, null, newsValues);
        assertTrue(newsRowId != -1);
        // A cursor is your primary interface to the query results.
        Cursor newsCursor = db.query(
                FloodAreaEntry.TABLE_NAME, // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(newsCursor, newsValues);

        dbHelper.close();
    }

    static ContentValues createNewsValues(long newsRowId) {
        String link = "http://www.google.com/";
        ContentValues newsValues = new ContentValues();
        newsValues.put(FloodAreaEntry.COLUMN_WIL, "Ini wilayah");
        newsValues.put(FloodAreaEntry.COLUMN_KEC, "Ini kecamatan");
        newsValues.put(FloodAreaEntry.COLUMN_KEL, "Ini kelurahan");
        newsValues.put(FloodAreaEntry.COLUMN_RW, "RW");
        newsValues.put(FloodAreaEntry.COLUMN_LONGITUDE, "Si longi");
        newsValues.put(FloodAreaEntry.COLUMN_LATITUDE, "Si lati");

        return newsValues;
        }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set <Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            //assertEquals(expectedValue, valueCursor.getString(idx));
            }
        valueCursor.close();
        }
}
