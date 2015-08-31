package com.digitcreativestudio.siagabanjir;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.utils.JSONParser;
import com.digitcreativestudio.siagabanjir.utils.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afifatul on 8/12/2015.
 */
public class LupaPasswordAcivity extends ActionBarActivity {
    String sEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        final EditText email = (EditText) findViewById(R.id.emailText);
        Button lupaPwd = (Button) findViewById(R.id.btnLupaPwd);

        lupaPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = email.getText().toString();

                if (sEmail.isEmpty()) {
                    Toast.makeText(LupaPasswordAcivity.this, "Masukan Alamat email Anda", Toast.LENGTH_LONG).show();
                }
                if (!Utility.isEmailValid(sEmail)) {
                    Toast.makeText(LupaPasswordAcivity.this, "Alamat email tidak valid", Toast.LENGTH_LONG).show();
                } else {
                    LupaPasswordTask m = (LupaPasswordTask) new LupaPasswordTask().execute();
                }
            }
        });
    }

    class LupaPasswordTask extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();
        JSONArray posts = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LupaPasswordAcivity.this);
            pDialog.setMessage("Loading..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sText) {
            String returnResult = sentNewPwd();
            return returnResult;

        }

        public String sentNewPwd()
        {

            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("email", sEmail));

            try {
               // String url_all_posts = "http://api.vhiefa.net76.net/whatson/lupa_password.php" ;
                String url_all_posts = "http://api.digitcreativestudio.com/siagabanjir/lupa_password.php" ;

                JSONObject json = jParser.makeHttpRequest(url_all_posts,"POST", parameter);

                int success = json.getInt("success");
                if (success == 1) {
                    return "OK";
                }
                else if (success == 2){
                    return "email not registered";
                }
                else {
                    return "fail";
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
                Toast.makeText(LupaPasswordAcivity.this, "Erorr! Cek koneksi internet Anda", Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("fail"))
            {
                Toast.makeText(LupaPasswordAcivity.this, "gagal, coba lagi!", Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("email not registered"))
            {
                Toast.makeText(LupaPasswordAcivity.this, "Email Anda belum terdaftar!", Toast.LENGTH_LONG).show();

            } else {
                //SUKSES
                //alert.showAlertDialog(LupaPasswordAcivity.this, "Pendaftaran berhasil", "Silahkan cek email untuk password baru Anda", true);
                Toast.makeText(LupaPasswordAcivity.this, "Password berhasil di-reset, silahkan cek email Anda untuk mengetahui password baru Anda", Toast.LENGTH_LONG).show();
            }
        }

    }
}
