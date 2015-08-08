package com.digitcreativestudio.siagabanjir;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.digitcreativestudio.siagabanjir.utils.JSONParser;

/**
 * Created by Afifatul on 8/7/2015.
 */
public class FetchReportTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchReportTask.class.getSimpleName();
    private final Context mContext;
    String latitude = "", longitude =""; //ini nanti current lat dan long si user

    JSONArray reportResult = null;

    public FetchReportTask(Context context) {
        mContext = context;
    }
/*
    // brings our database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                EventContract.ReportEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                EventContract.ReportEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        cursor.close();
    }
*/

    public String getReportList()
    {
        final String TAG_SUCCESS = "success";
        final String TAG_LAPORAN = "laporan";
        final String TAG_ID = "id_laporan";
        final String TAG_WAKTU = "waktu_laporan";
        final String TAG_PHOTO_URL = "photo_url";
        final String TAG_DESC = "deskripsi";
        final String TAG_LAT = "latitude";
        final String TAG_LONG = "longitude";

        List<NameValuePair> parameter = new ArrayList<NameValuePair>();

        JSONParser jParser = new JSONParser();

        try {
            String url_get_laporan = "http://api.vhiefa.net76.net/siagabanjir/dapatkan_laporan.php?lat="+latitude+"&long="+longitude;
            JSONObject json = jParser.makeHttpRequest(url_get_laporan,"POST", parameter);

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

              /*      ContentValues eventValues = new ContentValues();


                    eventValues.put(ReportEntry.COLUMN_EVENT_ID, id_laporan);
                    eventValues.put(ReportEntry.COLUMN_TITLE, title);
                    eventValues.put(ReportEntry.COLUMN_DATE,EventContract.getDbDateString(tanggal));
                    eventValues.put(ReportEntry.COLUMN_VENUE, venue);
                    eventValues.put(ReportEntry.COLUMN_DESCRIPTION, description);
                    eventValues.put(ReportEntry.COLUMN_CATEGORY, category);
                    eventValues.put(ReportEntry.COLUMN_ORGANIZER, organizer);


                    cVVector.add(eventValues);*/

                }

            /*    if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    mContext.getContentResolver().bulkInsert(ReportEntry.CONTENT_URI, cvArray);

                } */

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
    protected Void doInBackground(Void... params) {
            getReportList();
            return null;
    }
}
