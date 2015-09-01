package com.digitcreativestudio.siagabanjir;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.digitcreativestudio.siagabanjir.data.FloodContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Eko.
 */
public class FetchFloodAreaTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchFloodAreaTask.class.getSimpleName();

    private final Context mContext;

    public FetchFloodAreaTask(Context context) {
        mContext = context;

    }

    // brings our database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                FloodContract.FloodAreaEntry.CONTENT_URI_2,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                FloodContract.FloodAreaEntry.CONTENT_URI_2,
                null,
                null,
                null,
                null
        );
        cursor.close();
    }

    private boolean DEBUG = true;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void getNewsDataFromJson(String feedJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String FM_RESPONSEDATA = "Value";
        final String FM_WIL = "Wilayah";
        final String FM_KEC = "Kecamatan";
        final String FM_KEL = "Kelurahan";
        final String FM_RW = "RW";
        final String FM_LONGITUDE = "longitude";
        final String FM_LATITUDE = "latitude";

        //JSONObject jsonObject = new JSONObject(feedJsonStr);


        //JSONObject jsonObject_responseData = jsonObject.getJSONObject(FM_RESPONSEDATA);
        //JSONArray jsonArray_result = jsonObject.getJSONArray("Value");

        JSONArray jsonArray = new JSONArray(feedJsonStr);

        Vector<ContentValues> cVVector = new Vector<ContentValues>(jsonArray.length());

        //String[] resultStrs = new String[numList];
        for (int i = 0; i < jsonArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            //String day;
            Log.v(LOG_TAG, "Panjang JSONArray =" +jsonArray.length());
            String wilayah;
            String kecamatan;
            String keluarahan;
            String rw;
            String longitude;
            String latidude;
            int id;

            JSONObject listNews = jsonArray.getJSONObject(i);

            Log.v(LOG_TAG, "ISI JSONArray ke "+i+" =" +listNews);

            id = i;
            wilayah = listNews.getString(FM_WIL);
            kecamatan = listNews.getString(FM_KEC);
            keluarahan = listNews.getString(FM_KEL);
            rw = listNews.getString(FM_RW);

            ContentValues newsValues = new ContentValues();
            newsValues.put(FloodContract.FloodAreaEntry.COLUMN_FLOOD_AREA_ID, id);
            newsValues.put(FloodContract.FloodAreaEntry.COLUMN_WIL, wilayah);
            newsValues.put(FloodContract.FloodAreaEntry.COLUMN_KEC, kecamatan);
            newsValues.put(FloodContract.FloodAreaEntry.COLUMN_KEL, keluarahan);
            newsValues.put(FloodContract.FloodAreaEntry.COLUMN_RW, rw);
            newsValues.put(FloodContract.FloodAreaEntry.COLUMN_LONGITUDE, "0");
            newsValues.put(FloodContract.FloodAreaEntry.COLUMN_LATITUDE, "0");

            cVVector.add(newsValues);
        }

        if (cVVector.size() > 0){
            ContentValues[] cvArray2 = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray2);
            mContext.getContentResolver().bulkInsert(FloodContract.FloodAreaEntry.CONTENT_URI_2, cvArray2);
            Log.v(LOG_TAG, "Semua data berhasil dimasukkan");
        }

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(FloodContract.FloodAreaEntry.CONTENT_URI_2, cvArray);
            deleteAllRecords();
            Log.v(LOG_TAG, "All databases was deleted");
            int rowsInserted = mContext.getContentResolver()
                    .bulkInsert(FloodContract.FloodAreaEntry.CONTENT_URI_2, cvArray);

            Log.v(LOG_TAG, "inserted new " + rowsInserted + " rows of news data");
        }
    }


    @Override
    protected Void doInBackground(Void... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String feedJsonStr = null;
        int numList = 10;

        try {
            // Construct the URL
            URL url = new URL("http://tamlyn.org/tools/csv2json/index.php?csv=http%3A%2F%2Fdata.go.id%2Fdataset%2F42bafe1b-2b14-4a1c-8efd-c4bb843abe7c%2Fresource%2Ff741d348-29d6-456f-bcf4-542b0b8871aa%2Fdownload%2Fkelurahanrawanbanjirdkijakarta.csv");

            Log.v(LOG_TAG, "Built URI Flood Area " + url);

            // Create the request to Google Feed Api, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            feedJsonStr = buffer.toString();
            Log.v(LOG_TAG, "HASIL :" + feedJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the news data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getNewsDataFromJson(feedJsonStr);
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the feed url.
        return null;

    }

}