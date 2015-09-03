package com.digitcreativestudio.siagabanjir;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.data.FloodContract;
import com.digitcreativestudio.siagabanjir.utils.MyLocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Afifatul on 8/8/2015.
 */
public class CheckMyLocationActivity extends ActionBarActivity {

    private static final String LOG_TAG = CheckMyLocationActivity.class.getSimpleName();

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    Location location;
    TextView lokasisaya;
    private GoogleMap googleMap;
    String kelurahan;
    String kelurahan2;

    Context context;
    Double latitude = null, longitude = null; //hanya untuk default (tes), nanti ini akan keganti dengan current lat long si user

    private static final String[] FLOOD_AREA_COLUMNS = {
            FloodContract.FloodAreaEntry.TABLE_NAME + "." + FloodContract.FloodAreaEntry._ID,
            FloodContract.FloodAreaEntry.COLUMN_FLOOD_AREA_ID,
            FloodContract.FloodAreaEntry.COLUMN_WIL,
            FloodContract.FloodAreaEntry.COLUMN_KEC,
            FloodContract.FloodAreaEntry.COLUMN_KEL,
            FloodContract.FloodAreaEntry.COLUMN_RW
    };

    public static final int COL_FLOOD_AREA_ID = 1;
    public static final int COL_WIL = 2;
    public static final int COL_KEC = 3;
    public static final int COL_KEL = 4;
    public static final int COL_RW = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_my_location);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C62828")));

        try {
            // Loading map
            initilizeMap();
            googleMap.setMyLocationEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Button checkMyLocation = (Button) findViewById(R.id.checkmylocation);
        lokasisaya = (TextView) findViewById(R.id.lokasisaya);
        context = this;

        checkMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude==null && longitude==null){
                    Toast.makeText(getApplicationContext(),
                            "Sedang mencoba mendapatkan lokasi Anda! Coba sebentar lagi!", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    GetCurrentAddressAsyncTask currentadd = new GetCurrentAddressAsyncTask();
                    currentadd.execute();
                }
            }
        });

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default

        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        provider = locationManager.getBestProvider(criteria, false);

        // the last known location of this provider
        location = locationManager.getLastKnownLocation(provider);

        mylistener = new MyLocationListener(CheckMyLocationActivity.this);

        if (location != null) {
            mylistener.onLocationChanged(location);
            latitude = mylistener.getLatitude();
            longitude = mylistener.getLongitude();
          /*  Toast.makeText(
                    getApplicationContext(),
                    "Lat : "+latitude+
                            "Long : "+longitude,
                    Toast.LENGTH_LONG).show();*/

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(16).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        } else {
            // leads to the settings because there is no last known location
            showSettingsAlert(provider);
        }
        // location updates: at least 10 meter and 3 minutes change
        locationManager.requestLocationUpdates(provider, 1000*60*3, 10, mylistener);

    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                CheckMyLocationActivity.this);

        alertDialog.setTitle(provider + " setting");

        alertDialog
                .setMessage(provider + " belum aktif, silahkan aktifkan terlebih dulu.");

        alertDialog.setPositiveButton("Setting",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        CheckMyLocationActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Batal",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private class GetCurrentAddressAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        String address;
        int flag;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = new ProgressDialog(CheckMyLocationActivity.this);
            dialog.setMessage("Loading address. Please wait...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            address=getAddress(context, latitude, longitude);
            //address=getAddress(context, -6.166894, 106.861803); // ini berada di lokasi rawan banjir
            return address;
        }

        public  String getAddress(Context ctx, double latitude, double longitude) {

            String alamat=null;
            try {
                Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    flag=0;
                    Address address = addresses.get(0);
                 /*   String jalan = (address.getAddressLine(0));
                    String locality=address.getLocality();
                    String tambahan = address.getAdminArea()+address.getFeatureName()+address.getPremises()+address.getSubAdminArea()+address.getSubLocality()+address.getSubThoroughfare()+address.getThoroughfare()+address.getUrl();
                    String country=address.getCountryName();
                    String country_code=address.getCountryCode();
                    String zipcode=address.getPostalCode();*/

                    kelurahan = address.getSubLocality();
                    Log.v(LOG_TAG, "Kelurahan: " + kelurahan);
                    String kecamatan = address.getLocality();
                    String wilayah = address.getSubAdminArea();

                    alamat = "Anda berada di\nKelurahan : "+kelurahan +"\nKecamatan : "+ kecamatan +"\nWilayah : "+ wilayah; //ini nanti yang dicocokan dengan database kerawanan
                }
                else{
                    flag =1;
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
                flag=2;
            }
            return alamat;
        }

        @Override
        protected void onPostExecute(String resultString) {
            dialog.dismiss();


            if (flag==1){
                Toast.makeText(
                        getApplicationContext(),
                        "Alamat tidak bisa didapatkan, cek koneksi internet Anda",
                        Toast.LENGTH_LONG).show();
            }
            else if (flag==2){
                Toast.makeText(
                        getApplicationContext(),
                        "Alamat tidak bisa didapatkan, cek koneksi internet Anda",
                        Toast.LENGTH_LONG).show();
            }
            else if (flag==0){
                lokasisaya.setText(address);
                //PUT CODE TO FECTH FLOAD AREA FROM DATABASE HERE

                Uri floodUri = FloodContract.FloodAreaEntry.buildFloodAreaWithKelurahan4(kelurahan2);

                Log.v(LOG_TAG, "Flood Uri: "+ floodUri);

                // we'll query our contentProvider, as always
                Cursor cursor = context.getContentResolver().query(floodUri, FLOOD_AREA_COLUMNS,null, null, "_ID ASC");
                String hasil = "";
                String pesan = "";
                boolean rawan = false;
                int index = 1;

                while (cursor.moveToNext()) {

                    // Gets the value from the column.
                    hasil = cursor.getString(cursor.getColumnIndex(FloodContract.FloodAreaEntry.COLUMN_KEL));
                    //hasil = cursor.getColumnName(index);
                    if (hasil.equals(kelurahan.toUpperCase())) {
                        rawan = true;
                    }

                    Log.v(LOG_TAG,"Isi "+cursor.getString(index)+": " + hasil);
                }
              if (cursor.getCount() == 0){
                    pesan = "Lokasi RAWAN belum tersedia! Silahkan masuk ke menu Wilayah Rawan untuk mengupdate lokasi RAWAN.";
                    Toast.makeText(
                            getApplicationContext(),pesan
                            ,
                            Toast.LENGTH_LONG).show();
                }else{
                    if(rawan){
                        pesan = "Anda berada di lokasi RAWAN BANJIR!";
                        Toast.makeText(
                                getApplicationContext(),pesan
                                ,
                                Toast.LENGTH_LONG).show();
                    }else{
                        pesan = "Anda TIDAK berada di lokasi rawan banjir";
                        Toast.makeText(
                                getApplicationContext(),
                                pesan,
                                Toast.LENGTH_LONG).show();
                    }
                }
                Log.v(LOG_TAG, "Hasil analisa: " + pesan);

                Log.v(LOG_TAG, "Kursor: "+ cursor);
                Log.v(LOG_TAG, "Jumlah kursor: "+ cursor.getCount());

                if  (cursor.getCount() <= 0) { //jika di dalam database tidak ada kelurahan tsb maka

                }
                else {

                   // mFloodAreaAdapter.swapCursor((Cursor) cursor);
                }
            }
        }
    }

    /**
     * function to load map If map is not created it will create it for you
     * */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initilizeMap() {
        if (googleMap == null) {

            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map1)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}
