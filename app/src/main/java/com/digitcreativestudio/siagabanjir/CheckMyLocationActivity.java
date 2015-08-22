package com.digitcreativestudio.siagabanjir;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.utils.MyLocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Afifatul on 8/8/2015.
 */
public class CheckMyLocationActivity extends ActionBarActivity {
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    Location location;
    TextView lokasisaya;
    private GoogleMap googleMap;

    Context context;
    Double latitude = -6.166894, longitude = 106.861803; //hanya untuk default (tes), nanti ini akan keganti dengan current lat long si user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_my_location);

        try {
            // Loading map
            initilizeMap();

            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

            // Showing / hiding your current location
            googleMap.setMyLocationEnabled(true);

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

			/*
			double latitude = 17.385044;
			double longitude = 78.486671;

			// lets place some 10 random markers
			for (int i = 0; i < 10; i++) {
				// random latitude and logitude
				double[] randomLocation = createRandLocation(latitude,
						longitude);

				// Adding a marker
				MarkerOptions marker = new MarkerOptions().position(
						new LatLng(randomLocation[0], randomLocation[1]))
						.title("Hello Maps " + i);

				Log.e("Random", "> " + randomLocation[0] + ", "
						+ randomLocation[1]);

				// changing marker color
				if (i == 0)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				if (i == 1)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				if (i == 2)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
				if (i == 3)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				if (i == 4)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
				if (i == 5)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
				if (i == 6)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				if (i == 7)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
				if (i == 8)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
				if (i == 9)
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

				googleMap.addMarker(marker);

				// Move the camera to last position with a zoom level
				if (i == 9) {
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(new LatLng(randomLocation[0],
									randomLocation[1])).zoom(15).build();

					googleMap.animateCamera(CameraUpdateFactory
							.newCameraPosition(cameraPosition));
				}
			}*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button checkMyLocation = (Button) findViewById(R.id.checkmylocation);
        lokasisaya = (TextView) findViewById(R.id.lokasisaya);
        context = this;

        checkMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCurrentAddressAsyncTask currentadd = new GetCurrentAddressAsyncTask();
                currentadd.execute();

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

                    String kelurahan = address.getSubLocality();
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
                        "No Address returned! Try Again",
                        Toast.LENGTH_LONG).show();
            }
            else if (flag==2){
                Toast.makeText(
                        getApplicationContext(),
                        "Canont get Address! Could not get Geocoder data. Try Again",
                        Toast.LENGTH_LONG).show();
            }
            else if (flag==0){
                lokasisaya.setText(address);
            }
        }
    }

    /**
     * function to load map If map is not created it will create it for you
     * */
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

    //untuk memanggil googleMap.setOnMyLocationChangeListener(myLocationChangeListener);
    //tapi nanti marker redundan jd harus dihapus

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
           Marker mMarker = googleMap.addMarker(new MarkerOptions().position(loc));
            if(googleMap != null){
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };
}
