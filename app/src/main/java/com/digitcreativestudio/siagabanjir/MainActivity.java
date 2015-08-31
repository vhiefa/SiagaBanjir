package com.digitcreativestudio.siagabanjir;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.digitcreativestudio.siagabanjir.sync.FloodSyncAdapter;

/**
 * Created by Afifatul on 8/8/2015.
 */
public class MainActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C62828")));

        Button cek_kerawanan = (Button) findViewById(R.id.cek_kerawanan);
        Button lapor_banjir = (Button) findViewById(R.id.lapor_banjir);
        Button lokasi_rawan_banjir = (Button) findViewById(R.id.lihat);
        Button akun = (Button) findViewById(R.id.akun);
        Button atur = (Button) findViewById(R.id.atur);
        Button lihat = (Button) findViewById(R.id.berita);
        Button tentang = (Button) findViewById(R.id.tentang);

        akun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchReportTask(getApplicationContext()).execute("0", "110");
            }
        });

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

        tentang.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(i);
            }
        });

        FloodSyncAdapter.initializeSyncAdapter(this);
    }
}
