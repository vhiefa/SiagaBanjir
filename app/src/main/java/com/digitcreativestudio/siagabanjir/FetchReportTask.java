package com.digitcreativestudio.siagabanjir;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.digitcreativestudio.siagabanjir.data.FloodContract;
import com.digitcreativestudio.siagabanjir.data.FloodContract.FloodEntry;
import com.digitcreativestudio.siagabanjir.utils.JSONParser;

/**
 * Created by Afifatul on 8/7/2015.
 */
public class FetchReportTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReportTask.class.getSimpleName();
    private final Context mContext;
   // String latitude = "", longitude =""; //ini nanti current lat dan long si user

    JSONArray reportResult = null;

    public FetchReportTask(Context context) {
        mContext = context;
    }

    // brings our database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                FloodContract.FloodEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                FloodContract.FloodEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        cursor.close();
    }

    public String getReportList(String lati, String longi) throws JSONException
    {
        final String TAG_SUCCESS = "success";
        final String TAG_LAPORAN = "laporan";
        final String TAG_ID = "id_laporan";
        final String TAG_WAKTU = "waktu_laporan";
        final String TAG_PHOTO_URL = "photo_url";
        final String TAG_DESC = "deskripsi";
        final String TAG_LAT = "latitude";
        final String TAG_LONG = "longitude";



        JSONParser jParser = new JSONParser();

        try {
            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
          //  String url_get_laporan = "http://api.vhiefa.net76.net/siagabanjir/dapatkan_laporan.php?lat="+lati+"&long="+longi;
            String url_get_laporan = "http://api.digitcreativestudio.com/siagabanjir/dapatkan_laporan.php?lat="+lati+"&long="+longi;
            JSONObject json = jParser.makeHttpRequest(url_get_laporan,"GET", parameter);

            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                reportResult = json.getJSONArray(TAG_LAPORAN);
                // Get and insert the new report information into the database
                Vector<ContentValues> cVVector = new Vector<ContentValues>(reportResult.length());

                for (int i = 0; i< reportResult.length(); i++){
                    String id_laporan, waktu_laporan, photo_url, deskripsi, latitude, longitude;

                    JSONObject c = reportResult.getJSONObject(i);

                    id_laporan = c.getString(TAG_ID);
                    waktu_laporan = c.getString(TAG_WAKTU);
                    photo_url =c.getString(TAG_PHOTO_URL);
                    deskripsi = c.getString(TAG_DESC);
                    latitude = c.getString(TAG_LAT);
                    longitude = c.getString(TAG_LONG);

                    Log.v("Data dr json", "desc " + deskripsi);

                    ContentValues floodValues = new ContentValues();

                    floodValues.put(FloodEntry.COLUMN_FLOOD_ID, id_laporan);
                    floodValues.put(FloodEntry.COLUMN_TIME,waktu_laporan);
                    floodValues.put(FloodEntry.COLUMN_PHOTO, photo_url);
                    floodValues.put(FloodEntry.COLUMN_CAPTION, deskripsi);
                    floodValues.put(FloodEntry.COLUMN_LATITUDE, latitude);
                    floodValues.put(FloodEntry.COLUMN_LONGITUDE, longitude);

                    cVVector.add(floodValues);


                }

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(FloodEntry.CONTENT_URI, cvArray);
                    Log.v("Penambahan", "data ");

                }

                return "OK";
            }
            else {
                //Tidak Ada Record Data (SUCCESS = 0)
                return "no results";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception Caught";
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        // If there's NO COORDINAT, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String latitude = params[0];
        String longitude = params[1];

        try {
            getReportList(latitude, longitude);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
