package com.digitcreativestudio.siagabanjir;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Afifatul on 8/8/2015.
 */
public class MainActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cek_kerawanan = (Button) findViewById(R.id.cek_kerawanan);
        Button lapor_banjir = (Button) findViewById(R.id.lapor_banjir);
        Button lokasi_rawan_banjir = (Button) findViewById(R.id.lihat);

        cek_kerawanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(MainActivity.this, CheckMyLocationActivity.class);
                startActivity(i);
            }
        });

        lapor_banjir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(MainActivity.this, ReportFloodActivity.class);
                startActivity(i);
            }
        });

        lokasi_rawan_banjir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(MainActivity.this, FloodAreaActivity.class);
                startActivity(i);
            }
        });

    }
}
