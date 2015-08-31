package com.digitcreativestudio.siagabanjir;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
 * Created by Afifatul on 8/10/2015.
 */
public class CreateAccountActivity extends ActionBarActivity {

    Button create;
    TextView emailwarning, pwdwarning;
    EditText email, pwd1, pwd2, nama, tgllahir, jenkel, nope, alamat;
    String sEmail, sPwd1, sPwd2, sNama, sTglLahir, sJenKel, sNope, sAlamat;

    AlertDialogManager alert = new AlertDialogManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        create = (Button) findViewById(R.id.btnCreateAccount);
        emailwarning = (TextView) findViewById(R.id.emailwarning);
        pwdwarning = (TextView) findViewById(R.id.pwdwarning);
        email = (EditText) findViewById(R.id.txtEmail);
        pwd1 = (EditText) findViewById(R.id.txtPassword);
        pwd2 = (EditText) findViewById(R.id.txtPassword2);
        nama = (EditText) findViewById(R.id.txtNama);
        tgllahir = (EditText) findViewById(R.id.txtTglLahir);
        jenkel = (EditText) findViewById(R.id.txtJenKel);
        nope = (EditText) findViewById(R.id.txtNope);
        alamat=(EditText) findViewById(R.id.txtAlamat);


        

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sEmail = email.getText().toString();
                sPwd1 = pwd1.getText().toString();
                sPwd2= pwd2.getText().toString();
                sNama = nama.getText().toString();
                sTglLahir = tgllahir.getText().toString();
                sJenKel = jenkel.getText().toString();
                sNope = nope.getText().toString();
                sAlamat = alamat.getText().toString();
                if (sEmail.isEmpty() || sPwd1.isEmpty() || sPwd2.isEmpty() || sNama.isEmpty() || sAlamat.isEmpty() || sNope.isEmpty())
                    alert.showAlertDialog(CreateAccountActivity.this, "Gagal", "Harap lengkapi semua kolom", false);
                if (!Utility.isEmailValid(sEmail)){
                    emailwarning.setText("Email tidak valid");
                }else
                if (!sPwd1.equals(sPwd2)){
                    emailwarning.setText("");
                    pwdwarning.setText("Password tidak sama");
                }
                else {
                    pwdwarning.setText("");
                    CreateAccount m= (CreateAccount) new CreateAccount().execute();
                }

            }
        });
    }

    class CreateAccount extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();
        JSONArray posts = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateAccountActivity.this);
            pDialog.setMessage("Loading..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sText) {
            String returnResult = createAccount();
            return returnResult;

        }

        public String createAccount()
        {

            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("email", sEmail));
            parameter.add(new BasicNameValuePair("password", sPwd1));
            parameter.add(new BasicNameValuePair("nama", sNama));
            parameter.add(new BasicNameValuePair("tgl_lahir", sTglLahir));
            parameter.add(new BasicNameValuePair("jenis_kelamin", sJenKel));
            parameter.add(new BasicNameValuePair("nope", sNope));
            parameter.add(new BasicNameValuePair("alamat", sAlamat));

            try {
               // String url_all_posts = "http://api.vhiefa.net76.net/whatson/create_account.php" ;
                String url_all_posts = "http://api.digitcreativestudio.com/siagabanjir/create_account.php" ;

                JSONObject json = jParser.makeHttpRequest(url_all_posts,"POST", parameter);

                int success = json.getInt("success");
                if (success == 1) {
                    return "OK";
                }
                else if (success == 2){
                    return "email registered";
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
                Toast.makeText(CreateAccountActivity.this, "Erorr! Cek koneksi internet Anda", Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("fail"))
            {
                Toast.makeText(CreateAccountActivity.this, "Registrasi gagal, coba lagi!", Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("email registered"))
            {
              //  Toast.makeText(CreateAccountActivity.this, "Email Anda telah terdaftar!", Toast.LENGTH_LONG).show();
                alert.showAlertDialog(CreateAccountActivity.this, "Pendaftaran Gagal", "Email Anda telah terdaftar", true);

            } else {
                //SUKSES
                alert.showAlertDialog(CreateAccountActivity.this, "Pendaftaran berhasil", "Silahkan cek email untuk verifikasi", true);

            }
        }

    }
}
