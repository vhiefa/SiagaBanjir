package com.digitcreativestudio.siagabanjir;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afifatul on 8/10/2015.
 */
public class LoginActivity extends ActionBarActivity{

    // Email, password edittext
    EditText txtEmail, txtPassword;

    // login button
    Button btnLogin, btnCreateAccount;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    String email, password, status, nama, id_user;

     String TglLhr,  Pwd,  JenKel,  nope,  alamat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

       // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        if (session.isLoggedIn() == true){
            Intent i = new Intent(getApplicationContext(), AccountActivity.class);
            startActivity(i);
            finish();
        }

        // Email, Password input text
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCreateAccount= (Button) findViewById(R.id.btnCreateAccount);
        Button lupaPwd = (Button) findViewById( R.id.btnLupaPwd);

        lupaPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LupaPasswordAcivity.class);
                startActivity(i);
                finish();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get email, password from EditText
                 email = txtEmail.getText().toString();
                 password = txtPassword.getText().toString();

                // Check if email, password is filled
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // For testing puspose email, password is checked with sample data

                    //RETRIEVE FROM DATABASE SERVER
                    OtentikasiUser m= (OtentikasiUser) new OtentikasiUser().execute();

                } else {
                    // user didn't entered email or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter email and password", false);
                }

            }
        });
    }


    class OtentikasiUser extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();
        JSONArray posts = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Loading..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sText) {
            String returnResult = getUserAccount();
            return returnResult;

        }

        public String getUserAccount()
        {
            
            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("email", email));
            parameter.add(new BasicNameValuePair("password", password));

            try {
                String url_all_posts = "http://api.vhiefa.net76.net/whatson/get_user.php" ;

                JSONObject json = jParser.makeHttpRequest(url_all_posts,"POST", parameter);

                int success = json.getInt("success");
                if (success == 1) {
                    //Ada record Data (SUCCESS = 1)
                    //Getting Array of daftar_post
                    posts = json.getJSONArray("user");
                    
                    for (int i = 0; i< posts.length(); i++){
                        JSONObject c = posts.getJSONObject(i);

                        status = c.getString("status");
                        nama = c.getString("nama");
                        id_user = c.getString("id_user");
                        TglLhr= c.getString("tanggal_lahir");
                        Pwd= c.getString("password");
                        JenKel= c.getString("jenis_kelamin");
                        nope= c.getString("no_handphone");
                        alamat= c.getString("alamat");

                    }
                    return "OK";
                }
                else {
                    //Tidak Ada Record Data (SUCCESS = 0)
                    //EMAIL ATAU PASSWORD SALAH
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
                Toast.makeText(LoginActivity.this, "Unable to connect to server,please check your internet connection!", Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("no results"))
            {
                alert.showAlertDialog(LoginActivity.this, "Login failed..", "email/Password is incorrect", false);
            }
            else{
                //SUKSES
                if (status.equals("1")) {
                    // Creating user login session
                    session.createLoginSession(nama, email, id_user, TglLhr, Pwd,JenKel, nope, alamat ); //name n email to display

                    // Staring MainActivity
                    Intent i = new Intent(getApplicationContext(), AccountActivity.class);
                    startActivity(i);
                    finish();
                }
                else {
                    alert.showAlertDialog(LoginActivity.this, "Login failed..","You have to verify your registration before log in. Please check your email!", false);
                }
            }
        }

    }
    
}