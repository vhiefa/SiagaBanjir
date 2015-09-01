package com.digitcreativestudio.siagabanjir;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.utils.JSONParser;
import com.digitcreativestudio.siagabanjir.utils.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Afifatul on 8/23/2015.
 */
public class MyFloodReportActivity extends ActionBarActivity{

  
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> postsList;
    private String url_all_posts, id_user ;

    // JSON Node names
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_POSTS = "posts";
    public static final String TAG_ID = "id";
    public static final String TAG_DESC = "description";
    public static final String TAG_DATE = "created_at";
    public static final String TAG_PHOTO_URL = "photo";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";

    JSONArray posts = null;
    ListView list;
ListAdapter adapter;
    // Session Manager Class
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_flood_report);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        postsList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> user = session.getUserDetails();
        list = (ListView) findViewById(R.id.submited_list);
        id_user = user.get(SessionManager.KEY_ID);
        LoadMySentReport m = (LoadMySentReport) new LoadMySentReport().execute();

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C62828")));


    }


    class LoadMySentReport extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MyFloodReportActivity.this);
            pDialog.setMessage("Mohon Tunggu..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sText) {
            String returnResult = getFloodList();
            return returnResult;

        }

        public String getFloodList()
        {

            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("id_user", id_user));
            try {
              //  url_all_posts = "http://api.vhiefa.net76.net/siagabanjir/lihat_laporan_terkirim.php";
                url_all_posts = "http://demo.edusarana.com/sis/ws/lihat_laporan_terkirim.php";
                JSONObject json = jParser.makeHttpRequest(url_all_posts,"POST", parameter);
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    posts = json.getJSONArray(TAG_POSTS);
                    for (int i = 0; i < posts.length() ; i++){
                        JSONObject c = posts.getJSONObject(i);
                        HashMap<String, String> tempMap = new HashMap<String, String>();
                        tempMap.put(TAG_ID, c.getString(TAG_ID));
                        tempMap.put(TAG_DESC, c.getString(TAG_DESC));
                        tempMap.put(TAG_DATE, c.getString(TAG_DATE));
                        postsList.add(tempMap);
                        Log.v("isi postList",c.getString(TAG_DESC) );
                    }

                    return "OK";
                }
                else {
                    return "no results";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception Caught";
            }
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            pDialog.dismiss();
            if(result.equalsIgnoreCase("Exception Caught"))
            {
                Toast.makeText(MyFloodReportActivity.this, "Unable to connect to server,please check your internet connection!", Toast.LENGTH_LONG).show();
            }

            else if(result.equalsIgnoreCase("no results"))
            {
                Toast.makeText(MyFloodReportActivity.this, "Anda belum pernah melaporkan banjir", Toast.LENGTH_LONG).show();
            }
            else
            {
               ListAdapter adapter = new SimpleAdapter(
                        MyFloodReportActivity.this, postsList,
                        R.layout.flood_list_item, new String[]{TAG_ID, TAG_DATE, TAG_DESC},
                        new int[]{R.id.id, R.id.date, R.id.desc});
                list.setAdapter(adapter);
            }

        }

    }
}
