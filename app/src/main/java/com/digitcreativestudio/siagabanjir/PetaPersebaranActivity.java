package com.digitcreativestudio.siagabanjir;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.data.FloodContract;
import com.digitcreativestudio.siagabanjir.utils.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Afifatul on 9/2/2015.
 */
public class PetaPersebaranActivity extends ActionBarActivity {
    private String mPath = null;
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

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        try {
            // Loading map
            initilizeMap();

            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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

                String pesan = "Peta perseberan kosong karena Anda belum pernah mendapatkan notifikasi banjir";
                Toast.makeText(
                        getApplicationContext(),
                        pesan,
                        Toast.LENGTH_LONG).show();

			}

			else {

                cursor.moveToFirst();

                while(!cursor.isAfterLast()){
                    LatLng point = new LatLng(new Double(cursor.getDouble(INDEX_LAT)), new Double(cursor.getDouble(INDEX_LONG)));
                    googleMap.addMarker(new MarkerOptions().position(point).title(Utility.getShorterString(cursor.getString(INDEX_CAPTION))));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(point).zoom(13).build();
                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                    cursor.moveToNext();
                }
                cursor.close();


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_petapersebaran, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_share){
            CaptureMapScreen();
            //createShareNilaiIntent();
            /*if(mPath != null){
                File file = new File(mPath);
                mPath = null;
            }*/
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareNilaiIntent(){

        try {
            ShareActionProvider mShareActionProvider = null;
            View theView = findViewById(R.id.mapPersebaran);
            theView.setDrawingCacheEnabled(true);
            theView.buildDrawingCache(true);
            Bitmap b = Bitmap.createBitmap(theView.getDrawingCache());
            theView.setDrawingCacheEnabled(false);

            //Save bitmap
            //String mPath = null;
            String extr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() +   File.separator + "SiagaBanjir";
            String fileName = new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".jpg";
            File directory = new File(Environment.getExternalStorageDirectory(), "SiagaBanjir");

            if(!directory.exists()){
                directory.mkdir();
            }

            File file = new File(directory.getPath(), fileName);


            FileOutputStream fos = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            //MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            //Log.e("------ PATH IMAGE -----", file.getAbsolutePath());

            Uri uri = Uri.parse(file.getAbsolutePath());
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sharingIntent.setType("image/png");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(sharingIntent,
                    "Share image using"));
            if(mShareActionProvider != null){
                mShareActionProvider.setShareIntent(createShareNilaiIntent());
            }

            return sharingIntent;
        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

        return null;

    }

    public void CaptureMapScreen(){
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;


            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                bitmap = snapshot;
                try {
                    ShareActionProvider shareActionProvider = null;
                    String fileName = new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".jpg";
                    File directory = new File(Environment.getExternalStorageDirectory(), "SiagaBanjir");

                    if(!directory.exists()){
                        directory.mkdir();
                    }

                    File file = new File(directory.getPath(), fileName);


                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

                    Uri uri = Uri.parse(file.getAbsolutePath());
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    sharingIntent.setType("image/png");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(sharingIntent,
                            "Share image using"));
                    if(shareActionProvider != null){
                        shareActionProvider.setShareIntent(createShareNilaiIntent());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        googleMap.snapshot(callback);
    }

}

