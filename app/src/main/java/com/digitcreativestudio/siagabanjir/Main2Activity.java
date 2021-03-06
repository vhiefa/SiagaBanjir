package com.digitcreativestudio.siagabanjir;

import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.digitcreativestudio.siagabanjir.sync.FloodSyncAdapter;
import com.digitcreativestudio.siagabanjir.utils.Utility;

public class Main2Activity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private  WebView webView;
    private  ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_color)));
        Boolean notif = Utility.getPreferredNotification(this);

        if(notif){
            ContentResolver.setMasterSyncAutomatically(notif); // Turn on auto-sync master setting depend on notification setting
        }

        FloodSyncAdapter.initializeSyncAdapter(this);
        mTitle = getString(R.string.app_name);

        if(isNetworkConnected()){
            webView = (WebView) findViewById(R.id.news_feed);
            webView.loadUrl("http://www.demo.edusarana.com/sis/ws/twitter_feed.php");
            webView.setWebViewClient(new MyWebViewClient());
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            //menampilkan progress bar
            progress = (ProgressBar) findViewById(R.id.progressBar);
            progress.setVisibility(View.GONE);
        }else{
            //menampilkan dialoge
            //ConnectionDialogeFragment newFragment = ConnectionDialogeFragment.newInstance();// call the static method
            //newFragment.show(getFragmentManager(), "dialog");

            //menampilkan pesan koneksi
            TextView text_info = (TextView) findViewById(R.id.connection_message);
            text_info.setText("Anda tidak terhubung internet !");
        }

        ;
        findViewById(R.id.berita_but).setOnClickListener(new View.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(View view) {
                                                                         Intent i1 = new Intent(Main2Activity.this, Main2Activity.class);
                                                                         startActivity(i1);
                                                                     }
                                                                 }

                );
        findViewById(R.id.wilayah_rawan_but).setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View view) {
                                                                     Intent i1 = new Intent(Main2Activity.this, FloodAreaActivity.class);
                                                                     startActivity(i1);
                                                                 }
                                                             }

        );
        findViewById(R.id.cek_lokasi_but).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                Intent i1 = new Intent(Main2Activity.this, CheckMyLocationActivity.class);
                                                                startActivity(i1);
                                                            }
                                                        }

       );
        findViewById(R.id.lapor_but).setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View view) {
                                                                 Intent i1 = new Intent(Main2Activity.this, ReportFloodActivity.class);
                                                                 startActivity(i1);
                                                             }
                                                         }

        );
        findViewById(R.id.telp_but).setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View view) {
                                                                 Intent i1 = new Intent(Main2Activity.this, TelephoneActivity.class);
                                                                 startActivity(i1);
                                                             }
                                                         }

        );
        findViewById(R.id.tanggap_but).setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View view) {
                                                                 Intent i1 = new Intent(Main2Activity.this, InfoTanggapActivity.class);
                                                                 startActivity(i1);
                                                             }
                                                         }

        );
        findViewById(R.id.persebaran_but).setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View view) {
                                                                 Intent i1 = new Intent(Main2Activity.this, PetaPersebaranActivity.class);
                                                                 startActivity(i1);
                                                             }
                                                         }

        );
        findViewById(R.id.akun_but).setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View view) {
                                                                 Intent i1 = new Intent(Main2Activity.this, LoginActivity.class);
                                                                 startActivity(i1);
                                                             }
                                                         }

        );
        findViewById(R.id.pengaturan_but).setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View view) {
                                                                 Intent i1 = new Intent(Main2Activity.this, SettingActivity.class);
                                                                 startActivity(i1);
                                                             }
                                                         }

        );
        findViewById(R.id.tentang_but).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                Intent i1 = new Intent(Main2Activity.this, AboutActivity.class);
                                                                startActivity(i1);
                                                            }
                                                        }

       );
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            Main2Activity.this.progress.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.setVisibility(View.VISIBLE);
            Main2Activity.this.progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main2, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            if(isNetworkConnected()){
                webView = (WebView) findViewById(R.id.news_feed);
                webView.loadUrl("http://www.demo.edusarana.com/sis/ws/twitter_feed.php");
                webView.setWebViewClient(new MyWebViewClient());
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                //menampilkan progress bar
                progress = (ProgressBar) findViewById(R.id.progressBar);
                progress.setVisibility(View.GONE);


                //menampilkan pesan koneksi
                TextView text_info = (TextView) findViewById(R.id.connection_message);
                text_info.setText(" Memuat...");
            }else{
                //menampilkan dialoge
                ConnectionDialogeFragment newFragment = ConnectionDialogeFragment.newInstance();// call the static method
                newFragment.show(getFragmentManager(), "dialog");

                //menampilkan pesan koneksi
                //TextView text_info = (TextView) findViewById(R.id.connection_message);
                //text_info.setText("Anda tidak terhubung internet !");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }



        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);
            //munculin berita

            return rootView;
        }
    }

    private class Context {
    }
}
