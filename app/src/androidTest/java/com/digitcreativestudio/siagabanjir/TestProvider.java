package com.digitcreativestudio.siagabanjir;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import com.digitcreativestudio.siagabanjir.data1.FloodContract.FloodAreaEntry;

/**
 * Created by Eko.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    public static String link = "http://www.google.com/";
    public static String link2 = "http://www.undip.ac.id/";

    // brings our database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                FloodAreaEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                FloodAreaEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }
    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {

        String link = "http://www.google.com/";

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        //NewsDbHelper dbHelper = new NewsDbHelper(mContext);
        //SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues newsValues = new ContentValues();
        newsValues.put(FloodAreaEntry.COLUMN_WIL, "Ini wilayah");
        newsValues.put(FloodAreaEntry.COLUMN_KEC, "Ini kecamatan");
        newsValues.put(FloodAreaEntry.COLUMN_KEL, "Ini kelurahan");
        newsValues.put(FloodAreaEntry.COLUMN_RW, "RW");
        newsValues.put(FloodAreaEntry.COLUMN_LONGITUDE, "Si longi");
        newsValues.put(FloodAreaEntry.COLUMN_LATITUDE, "Si lati");

        ContentValues newsValues2 = TestDb.createNewsValues(newsRowId);

        Uri newsInsertUri = mContext.getContentResolver()
                .insert(FloodAreaEntry.CONTENT_URI, newsValues2);
        assertTrue(newsInsertUri != null);


        // A cursor is your primary interface to the query results.
        Cursor newsCursor = mContext.getContentResolver().query(
                FloodAreaEntry.CONTENT_URI, // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(newsCursor, newsValues2);

        // Add the news values data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(newsValues2, newsValues);

        TestDb.validateCursor(newsCursor, newsValues2);

        //dbHelper.close();
    }

    public void testGetType() {
        // content://dcs.beritauniversitas/news/
        String type = mContext.getContentResolver().getType(FloodAreaEntry.CONTENT_URI);
        // vnd.android.cursor.dir/dcs.beritauniversitas/news
        assertEquals(FloodAreaEntry.CONTENT_TYPE, type);

    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        void addAllContentValues(ContentValues destination, ContentValues source) {
            for (String key : source.keySet()) {
                destination.put(key, source.getAsString(key));
            }
        }

    long newsRowId;

    static ContentValues createNewsUndipValues(){
        ContentValues newsValues = new ContentValues();
        newsValues.put(FloodAreaEntry.COLUMN_WIL, "Ini wilayah");
        newsValues.put(FloodAreaEntry.COLUMN_KEC, "Ini kecamatan");
        newsValues.put(FloodAreaEntry.COLUMN_KEL, "Ini kelurahan");
        newsValues.put(FloodAreaEntry.COLUMN_RW, "RW");
        newsValues.put(FloodAreaEntry.COLUMN_LONGITUDE, "Si longi");
        newsValues.put(FloodAreaEntry.COLUMN_LATITUDE, "Si lati");

        return  newsValues;

    }

    // Inserts news data for the undip data set.
    public void insertUndipData() {

        ContentValues undipNewsValues = createNewsUndipValues();
        Uri newsInsertUri = mContext.getContentResolver()
                .insert(FloodAreaEntry.CONTENT_URI, undipNewsValues);
        assertTrue(newsInsertUri != null);
    }

    public void testUpdateAndReadNews() {
        insertUndipData();
        String newWil = "INDONESIA";

        // Make an update to one value.
        ContentValues wilUpdate = new ContentValues();
        wilUpdate.put(FloodAreaEntry.COLUMN_WIL, newWil);

        mContext.getContentResolver().update(
                FloodAreaEntry.CONTENT_URI, wilUpdate, null, null);

        // A cursor is your primary interface to the query results.
        Cursor newsCursor = mContext.getContentResolver().query(
                FloodAreaEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make the same update to the full ContentValues for comparison.
        ContentValues undipAltered = createNewsUndipValues();
        undipAltered.put(FloodAreaEntry.COLUMN_WIL, newWil);

        TestDb.validateCursor(newsCursor, undipAltered);
    }

    public void testRemoveContentAndReadNews() {
        insertUndipData();

        mContext.getContentResolver().delete(FloodAreaEntry.CONTENT_URI,
                FloodAreaEntry.COLUMN_RW, null);

        // A cursor is your primary interface to the query results.
        Cursor newsCursor = mContext.getContentResolver().query(
                FloodAreaEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make the same update to the full ContentValues for comparison.
        ContentValues undipAltered = createNewsUndipValues();
        undipAltered.remove(FloodAreaEntry.COLUMN_RW);

        TestDb.validateCursor(newsCursor, undipAltered);
        int idx = newsCursor.getColumnIndex(FloodAreaEntry.COLUMN_RW);
        assertEquals(4, idx);
    }


}
