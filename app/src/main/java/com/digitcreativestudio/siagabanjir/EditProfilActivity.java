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
public class EditProfilActivity extends ActionBarActivity {

    Button update;
    EditText  nama, tgllahir, jenkel, nope, alamat;
    String sId,  sNama, sTglLahir, sJenKel, sNope, sAlamat, sPwd, sEmail;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        session = new SessionManager(getApplicationContext());

        update = (Button) findViewById(R.id.btnUpdateProfil);
        nama = (EditText) findViewById(R.id.txtNama);
        tgllahir = (EditText) findViewById(R.id.txtTglLahir);
        jenkel = (EditText) findViewById(R.id.txtJenKel);
        nope = (EditText) findViewById(R.id.txtNope);
        alamat=(EditText) findViewById(R.id.txtAlamat);


        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        sId = user.get(SessionManager.KEY_ID);
        sNama = user.get(SessionManager.KEY_NAME);
        sTglLahir= user.get(SessionManager.KEY_TGLLHR);
        sJenKel= user.get(SessionManager.KEY_JENKEL);
        sNope= user.get(SessionManager.KEY_NOPE);
        sAlamat = user.get(SessionManager.KEY_ALAMAT);
        sPwd = user.get(SessionManager.KEY_PWD);
        sEmail = user.get(SessionManager.KEY_EMAIL);

        nama.setText(sNama);
        tgllahir.setText(sTglLahir);
        jenkel.setText(sJenKel);
        nope.setText(sNope);
        alamat.setText(sAlamat);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sNama = nama.getText().toString();
                sTglLahir = tgllahir.getText().toString();
                sJenKel = jenkel.getText().toString();
                sNope = nope.getText().toString();
                sAlamat = alamat.getText().toString();
                if ( sNama.isEmpty() || sAlamat.isEmpty() || sNope.isEmpty())
                    alert.showAlertDialog(EditProfilActivity.this, "Gagal", "Harap lengkapi semua kolom", false);
                else {
                    
                    UpdateProfil m= (UpdateProfil) new UpdateProfil().execute();
                }

            }
        });
    }

    class UpdateProfil extends AsyncTask<String, Void, String>
    {
        ProgressDialog pDialog;
        JSONParser jParser = new JSONParser();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfilActivity.this);
            pDialog.setMessage("Loading..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sText) {
            String returnResult = UpdateProfil();
            return returnResult;

        }

        public String UpdateProfil()
        {

            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("id_user", sId));
            parameter.add(new BasicNameValuePair("nama", sNama));
            parameter.add(new BasicNameValuePair("tgl_lahir", sTglLahir));
            parameter.add(new BasicNameValuePair("jenis_kelamin", sJenKel));
            parameter.add(new BasicNameValuePair("nope", sNope));
            parameter.add(new BasicNameValuePair("alamat", sAlamat));

            try {
                String url_all_posts = "http://api.vhiefa.net76.net/whatson/update_profil.php" ;

                JSONObject json = jParser.makeHttpRequest(url_all_posts,"POST", parameter);

                int success = json.getInt("success");
                if (success == 1) {
                    session.createLoginSession(sNama, sEmail, sId, sTglLahir, sPwd ,sJenKel, sNope, sAlamat );
                    return "OK";
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
                Toast.makeText(EditProfilActivity.this, "Erorr! Cek koneksi internet Anda", Toast.LENGTH_LONG).show();
            }
            else if(result.equalsIgnoreCase("fail"))
            {
                Toast.makeText(EditProfilActivity.this, "Update gagal, coba lagi!", Toast.LENGTH_LONG).show();


            } else {
                //SUKSES
               Toast.makeText(EditProfilActivity.this, "Update berhasil", Toast.LENGTH_LONG).show();

            }

        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
