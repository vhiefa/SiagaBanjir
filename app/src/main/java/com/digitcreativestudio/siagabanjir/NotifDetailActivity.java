package com.digitcreativestudio.siagabanjir;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Afifatul on 9/2/2015.
 */
public class NotifDetailActivity extends ActionBarActivity {

    public ImageLoader imageLoader;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notification);


        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        imageLoader = new ImageLoader(getApplicationContext());

        Bundle b = getIntent().getExtras();
        String floodId = b.getString("floodId");
        String longi= b.getString("longi");
        String lati= b.getString("lati");
        String time = b.getString("time");
        String desc= b.getString("desc");
        String photo= b.getString("photo");

        ImageView imgV = (ImageView) findViewById(R.id.imageViewNotif);
        TextView caption = (TextView) findViewById(R.id.textViewCaption);
        TextView pelapor = (TextView) findViewById(R.id.textViewPelapor);
        TextView waktu = (TextView) findViewById(R.id.textViewTime);

        caption.setText("Deskripsi : \n"+desc);
        waktu.setText("Waktu pelaporan :" + time);
       // pelapor.setText(photo);
        imageLoader.DisplayImage(photo, imgV);

        final LatLng point = new LatLng(new Double(lati), new Double(longi));

        try {
            // Loading map
            initilizeMap();
            googleMap.setMyLocationEnabled(true);
            googleMap.addMarker(new MarkerOptions().position(point).title(desc));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(point).zoom(13).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * function to load map If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {

            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapNotif)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
