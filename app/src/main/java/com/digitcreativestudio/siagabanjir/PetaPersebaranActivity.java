package com.digitcreativestudio.siagabanjir;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.data.FloodContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Afifatul on 9/2/2015.
 */
public class PetaPersebaranActivity extends Activity {

    // Google Map
    private GoogleMap googleMap;

	private static final String[] NOTIFY_FLOOD_PROJECTION = new String[] {
			FloodContract.FloodEntry._ID,
			FloodContract.FloodEntry.COLUMN_FLOOD_ID,
			FloodContract.FloodEntry.COLUMN_TIME,
			FloodContract.FloodEntry.COLUMN_CAPTION,
			FloodContract.FloodEntry.COLUMN_LATITUDE,
			FloodContract.FloodEntry.COLUMN_LONGITUDE,
			FloodContract.FloodEntry.COLUMN_PHOTO

	};

	// these indices must match the projection
	private static final int INDEX_ID = 0;
	private static final int INDEX_FLOOD_ID = 1;
	private static final int INDEX_TIME = 2;
	private static final int INDEX_CAPTION = 3;
	private static final int INDEX_LAT = 4;
	private static final int INDEX_LONG = 5;
	private static final int INDEX_PHOTO = 6;


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peta_persebaran_notification);

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


			Uri floodUri = FloodContract.FloodEntry.CONTENT_URI; //dapatkan semua notifikasi laporan

			// we'll query our contentProvider, as always
			Cursor cursor = getApplicationContext().getContentResolver().query(floodUri, NOTIFY_FLOOD_PROJECTION, null, null, null);

			if  (cursor.getCount() <= 0) { //jika belum pernah mendapat notifikasi sama sekali

			}

			else {


/*
				for (int i = 0; i < cursor.getCount(); i++) {
					if (cursor.moveToFirst()) {
						LatLng point = new LatLng(new Double(INDEX_LAT), new Double(INDEX_LONG));
						googleMap.addMarker(new MarkerOptions().position(point).title(INDEX_CAPTION));
						CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(point).zoom(13).build();
						googleMap.animateCamera(CameraUpdateFactory
								.newCameraPosition(cameraPosition));

					}
				}*/

			}


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    /**
     * function to load map If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapPersebaran)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
     * creating random postion around a location for testing purpose only
     */
    private double[] createRandLocation(double latitude, double longitude) {

        return new double[] { latitude + ((Math.random() - 0.5) / 500),
                longitude + ((Math.random() - 0.5) / 500),
                150 + ((Math.random() - 0.5) * 10) };
    }
}