package com.digitcreativestudio.siagabanjir;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
 * Created by Afifatul on 8/11/2015.
 */
public class UbahPasswordActivity extends ActionBarActivity {

    Button ubahPwd;
    TextView  pwdwarning;
    EditText pwd0, pwd1, pwd2;
    String sPwdlama, sPwd1, sPwd2, sId;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        session = new SessionManager(getApplicationContext());
        
        ubahPwd = (Button) findViewById(R.id.btnUbahPassword);
        pwdwarning = (TextView) findViewById(R.id.pwdwarning);
        pwd0 = (EditText) findViewById(R.id.txtPasswordLama);
        pwd1 = (EditText) findViewById(R.id.txtPassword);
        pwd2 = (EditText) findViewById(R.id.txtPassword2);

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        sId = user.get(SessionManager.KEY_ID);

        ubahPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPwdlama = pwd0.getText().toString();
                sPwd1 = pwd1.getText().toString();
                sPwd2= pwd2.getText().toString();

                if (sPwdlama.isEmpty() || sPwd1.isEmpty() || sPwd2.isEmpty() )
                    alert.showAlertDialog(UbahPasswordActivity.this, "Gagal", "Harap lengkapi semua kolom", false);
                else
                if (!sPwd1.equals(sPwd2)){
                    pwdwarning.setText("Password tidak sama");
                }
                else {
                    pwdwarning.setText("");
                    UbahPassword m= (UbahPassword) new UbahPassword().execute();
                }

            }
        });

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C62828")));
    
    }


    class UbahPassword extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();
        JSONArray posts = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UbahPasswordActivity.this);
            pDialog.setMessage("Loading..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sText) {
            String returnResult = UbahPassword();
            return returnResult;

        }

        public String UbahPassword() {

            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("password", sPwdlama));
            parameter.add(new BasicNameValuePair("passwordBaru", sPwd1));
            parameter.add(new BasicNameValuePair("id_user", sId));

            try {
                //String url_all_posts = "http://api.vhiefa.net76.net/whatson/ubah_password.php";
                String url_all_posts = "http://api.digitcreativestudio.com/siagabanjir/ubah_password.php";

                JSONObject json = jParser.makeHttpRequest(url_all_posts, "POST", parameter);

                int success = json.getInt("success");
                if (success == 1) {
                    return "OK";}
                else if (success == 2) {
                    return "password incorrect";
                } else {
                        return "fail";
                    }

                }catch(Exception e){
                    e.printStackTrace();
                    return "Exception Caught";
                }
            }

            @Override
            protected void onPostExecute (String result){
                super.onPostExecute(result);
                pDialog.dismiss();
                if (result.equalsIgnoreCase("Exception Caught")) {
                    Toast.makeText(UbahPasswordActivity.this, "Erorr! Cek koneksi internet Anda", Toast.LENGTH_LONG).show();
                } else if (result.equalsIgnoreCase("fail")) {
                    Toast.makeText(UbahPasswordActivity.this, "Ubah password gagal, coba lagi!", Toast.LENGTH_LONG).show();
                } else if (result.equalsIgnoreCase("password incorrect")) {
                    alert.showAlertDialog(UbahPasswordActivity.this, "Gagal", "Password yang Anda masukan salah", false);

                } else {
                    //SUKSES
                    alert.showAlertDialog(UbahPasswordActivity.this, "Berhasil", "Ubah password berhasil", true);

                }
            }
        
    }
}
