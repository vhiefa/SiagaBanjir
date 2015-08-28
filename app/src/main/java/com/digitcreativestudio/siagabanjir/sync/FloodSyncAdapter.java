package com.digitcreativestudio.siagabanjir.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.digitcreativestudio.siagabanjir.MainActivity;
import com.digitcreativestudio.siagabanjir.R;
import com.digitcreativestudio.siagabanjir.data.FloodContract;
import com.digitcreativestudio.siagabanjir.utils.JSONParser;
import com.digitcreativestudio.siagabanjir.utils.MyLocationListener;
import com.digitcreativestudio.siagabanjir.utils.Utility;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by faqih_000 on 5/9/2015.
 * Edited by Fifa 26/8/2015
 */
public class FloodSyncAdapter extends AbstractThreadedSyncAdapter{

    private final String LOG_TAG = FloodSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60;// * 3;// 60 * 3; //3 jam
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
   //private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    final int FLOOD_NOTIFICATION_ID = 3153;

    Context context;
    JSONArray reportResult = null;
    JSONParser jParser;// = new JSONParser();


    private static final String[] NOTIFY_FLOOD_PROJECTION = new String[] {
            FloodContract.FloodEntry.COLUMN_FLOOD_ID,
            FloodContract.FloodEntry.COLUMN_CAPTION,
            FloodContract.FloodEntry.COLUMN_LATITUDE,
            FloodContract.FloodEntry.COLUMN_LONGITUDE,
            FloodContract.FloodEntry.COLUMN_PHOTO,
            FloodContract.FloodEntry.COLUMN_TIME
    };

    // these indices must match the projection
    private static final int INDEX_FLOOD_ID = 0;
    private static final int INDEX_CAPTION = 1;
    private static final int INDEX_LAT = 2;
    private static final int INDEX_LONG = 3;
    private static final int INDEX_PHOTO = 4;
    private static final int INDEX_TIME = 5;

    public FloodSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        context = getContext();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayCurrentNotifKey = context.getString(R.string.pref_enable_current_notifications_key);
        boolean displayCurrentNotifications = prefs.getBoolean(displayCurrentNotifKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_current_notifications_default)));

        String displayhomeNotifKey = context.getString(R.string.pref_enable_home_notifications_key);
        boolean displayHomeNotifications = prefs.getBoolean(displayhomeNotifKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_home_notifications_default)));

        if ( displayCurrentNotifications == true ) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default
            criteria.setCostAllowed(false);
            // get the best provider depending on the criteria
            String prvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(prvider);
            MyLocationListener mylistener = new MyLocationListener(context);
            if (location != null) {
                mylistener.onLocationChanged(location);
                String currlatitude = String.valueOf(mylistener.getLatitude()); //get current latitude of user
                String currlongitude = String.valueOf(mylistener.getLongitude()); //get current longitude of user
                try {
                    fetchFloodReport(currlatitude, currlongitude); //mendapatkan data banjir dari API sekaligus notify
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            } else {
                // leads to the settings because there is no last known location
                //showSettingsAlert(provider);
            }
            // location updates: at least 10 meter and 3 minutes change
            //locationManager.requestLocationUpdates(provider, 1000*60*3, 10, mylistener);

        }

        if (displayHomeNotifications == true){
            SharedPreferences prf = context.getSharedPreferences("HomeLocaPref", 0);
            String koordinat = prf.getString("home_location", "");
            String homeLatitude = Utility.getLatitudefromCoordinat(koordinat);
            String homeLongitude = Utility.getLongitudefromCoordinat(koordinat);
            try {
                fetchFloodReport(homeLatitude, homeLongitude); //mendapatkan data banjir dari API sekaligus notify
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }


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



    private void notifyFlood(String id) {

      //  SharedPreferences prefNotif = PreferenceManager.getDefaultSharedPreferences(context);

      //  String lastNotificationKey = context.getString(R.string.pref_last_notification);
      //  long lastSync = prefNotif.getLong(lastNotificationKey, 0);

       // if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
            // Last sync was more than 1 day ago, let's send a notification with the flood report.

            Uri floodUri = FloodContract.FloodEntry.buildFloodById(id);

            // we'll query our contentProvider, as always
            Cursor cursor = context.getContentResolver().query(floodUri, NOTIFY_FLOOD_PROJECTION, null, null, null);

            if (cursor.moveToFirst()) {
                String floodId = cursor.getString(INDEX_FLOOD_ID);
                String longi = cursor.getString(INDEX_LONG);
                String lati = cursor.getString(INDEX_LAT);
                String desc = cursor.getString(INDEX_CAPTION);
                String photo = cursor.getString(INDEX_PHOTO);
                String time = cursor.getString(INDEX_TIME);



               // int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
                String title = context.getString(R.string.app_name);

                // Define the text of the forecast.
                String contentText = time+"\n"+desc;

                // NotificationCompatBuilder is a very convenient way to build backward-compatible
                // notifications.  Just throw in some data.
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                              //  .setSmallIcon(iconId)
                                .setContentTitle(title)
                                .setContentText(contentText);

                // Make something interesting happen when the user clicks on the notification.
                // In this case, opening the app is sufficient.
                Intent resultIntent = new Intent(context, MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                // FLOOD_NOTIFICATION_ID allows you to update the notification later on.
                mNotificationManager.notify(FLOOD_NOTIFICATION_ID, mBuilder.build());


                //refreshing last sync
              //  SharedPreferences.Editor editor = prefNotif.edit();
              //  editor.putLong(lastNotificationKey, System.currentTimeMillis());
             //   editor.commit();
           // }
        }
    }


    public String fetchFloodReport(String lati, String longi) throws JSONException
    {
        final String TAG_SUCCESS = "success";
        final String TAG_LAPORAN = "laporan";
        final String TAG_ID = "id_laporan";
        final String TAG_WAKTU = "waktu_laporan";
        final String TAG_PHOTO_URL = "photo_url";
        final String TAG_DESC = "deskripsi";
        final String TAG_LAT = "latitude";
        final String TAG_LONG = "longitude";

        jParser = new JSONParser();

        try {
            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            String url_get_laporan = "http://api.vhiefa.net76.net/siagabanjir/dapatkan_laporan.php?lat="+lati+"&long="+longi;
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

                    Log.v("Data dari json", "desc " + deskripsi);

                    Uri floodUri = FloodContract.FloodEntry.buildFloodById(id_laporan);

                    // we'll query our contentProvider, as always
                    Cursor cursor = context.getContentResolver().query(floodUri, NOTIFY_FLOOD_PROJECTION, null, null, null);

                    if  (cursor.getCount() <= 0) { //jika di dalam database belum ada flood_report ber-id tersebut maka :

                        ContentValues floodValues = new ContentValues();

                        floodValues.put(FloodContract.FloodEntry.COLUMN_FLOOD_ID, id_laporan);
                        floodValues.put(FloodContract.FloodEntry.COLUMN_TIME, waktu_laporan);
                        floodValues.put(FloodContract.FloodEntry.COLUMN_PHOTO, photo_url);
                        floodValues.put(FloodContract.FloodEntry.COLUMN_CAPTION, deskripsi);
                        floodValues.put(FloodContract.FloodEntry.COLUMN_LATITUDE, latitude);
                        floodValues.put(FloodContract.FloodEntry.COLUMN_LONGITUDE, longitude);

                        cVVector.add(floodValues); //tambahkan ke database

                        notifyFlood(id_laporan); //notify flood_report ber-id tersebut
                    }

                }

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    context.getContentResolver().bulkInsert(FloodContract.FloodEntry.CONTENT_URI, cvArray);

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

}
