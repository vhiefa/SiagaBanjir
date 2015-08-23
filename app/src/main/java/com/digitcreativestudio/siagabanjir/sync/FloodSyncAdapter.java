package com.digitcreativestudio.siagabanjir.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.digitcreativestudio.siagabanjir.R;
import com.digitcreativestudio.siagabanjir.data.FloodContract;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by faqih_000 on 5/9/2015.
 */
public class FloodSyncAdapter extends AbstractThreadedSyncAdapter{

    public static final long SYNC_INTERVAL = 60 * 60;
    public static final long SYNC_FLEXTIME = SYNC_INTERVAL/4;
    public static final String SYNC_STATUS = "status";
    public String status;
    final int FLOOD_NOTIFICATION_ID = 3153;
    final String NOTIFICATION_GROUP = "flood_notification_group";

    Intent isSync;

    public FloodSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        /*
        *
        * GETTING USER'S LATITUDE AND LONGITUDE
        * GETTING DATA FROM SERVER AND SAVE IT IN LOCAL DATABASE
        * IF THERE'RE NEW RECORD NOTIFY USER,
        * WHEN NOTIFICATION TOUCHED IT SEND USER TO “activity detail laporan banjir”
        *
        * */

        Double latitude = -6.166894, longitude = 106.861803; //hanya untuk default (tes), nanti ini akan keganti dengan current lat long si user


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.
        String floodJsonStr = null;

        try {
            // URL from google sheet
            URL url = new URL("...");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
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
                return;
            }
            floodJsonStr = buffer.toString();

        } catch (IOException e) {
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }
        }

        try {
            if(floodJsonStr != null) {
                getFloodDataFromJson(floodJsonStr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return;

    }

    public static void syncImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if(null == accountManager.getPassword(newAccount)){
            if(!accountManager.addAccountExplicitly(newAccount, "", null)){
                return null;
            }

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    public static void configurePeriodicSync(Context context, long syncInterval, long flexTime){
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }else{
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context){
        FloodSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }

    private void getFloodDataFromJson(String floodJsonStr)
            throws JSONException {
        List<String> makulIds = new ArrayList<>();

        // These are the names of the JSON objects that need to be extracted.
        /*
        *
        * HERE'S PLACE FOR NAMES OF JSON OBJECTS THAT NEED TO BE EXTRACTED
        *
        * */

        Vector<ContentValues> cVVector = new Vector<ContentValues>();

        /*
        *
        * HERE'S PLACE FOR PARSING JSON
        *
        * */

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            getContext().getContentResolver().bulkInsert(FloodContract.FloodEntry.CONTENT_URI, cvArray);
            notifyFlood();
        }

    }



    private void notifyFlood(){
        Context context = getContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
        /* CHECKING NOTIFICATION CONFIGURATION */
        String mNotif = prefs.getString("value", "default");

        if(mNotif.equals("on")) {

            Uri floodUri = FloodContract.FloodEntry.CONTENT_URI;

            Cursor cursor = context.getContentResolver().query(
                    floodUri,
                    null,
                    null,
                    null,
                    null
            );

            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                boolean isNew = cursor.getInt(cursor.getColumnIndex(FloodContract.FloodEntry.COLUMN_NEW)) == 0 ? false : true;

                if (isNew) {
                    /*
                    *
                    * HERE'S PLACE FOR NOTIFICATION'S CODE, WILL BE WRITE SOON
                    *
                    * */

                    //Update "new" column to 0
                    Uri updateUri = FloodContract.FloodEntry.CONTENT_URI;

                    String floodID = cursor.getString(cursor.getColumnIndex(FloodContract.FloodEntry._ID));

                    ContentValues values = new ContentValues();

                    values.put(FloodContract.FloodEntry.COLUMN_NEW, "0");

                    String selection = FloodContract.FloodEntry.TABLE_NAME +
                            "." + FloodContract.FloodEntry._ID + " = ?";

                    context.getContentResolver().update(
                            floodUri,
                            values,
                            selection,
                            new String[]{floodID}
                    );


                }

                cursor.moveToNext();
            }

            cursor.close();

        }
    }
}
