package com.digitcreativestudio.siagabanjir;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Afifatul on 9/2/2015.
 */
public class NotifDetailActivity extends ActionBarActivity {

    public ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notification);

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

        caption.setText(desc);
        waktu.setText(time);
        pelapor.setText(photo);
        //imageLoader.DisplayImage(photo, imgV);
    }
}
