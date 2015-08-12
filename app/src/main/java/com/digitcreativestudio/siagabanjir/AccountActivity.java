package com.digitcreativestudio.siagabanjir;

/**
 * Created by Afifatul on 8/10/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class AccountActivity extends ActionBarActivity {

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    // Button Logout
    Button btnLogout, btnupdate, ubahPwd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        TextView lblName = (TextView) findViewById(R.id.lblName);
        TextView lblEmail = (TextView) findViewById(R.id.lblEmail);

        // Button logout
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnupdate = (Button) findViewById(R.id.btnEditProfil);
        ubahPwd = (Button) findViewById(R.id.btnUbahPassword);

       // Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String name = user.get(SessionManager.KEY_NAME);

        // email
        String email = user.get(SessionManager.KEY_EMAIL);

        // displaying user data
        lblName.setText(Html.fromHtml("Name: <b>" + name + "</b>"));
        lblEmail.setText(Html.fromHtml("Email: <b>" + email + "</b>"));


    /*    update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountActivity.this, EditProfilActivity.class);
                startActivity(i);
            }
        });
        ubahPwd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UbahPasswordActivity.class);
                startActivity(i);
            }
        }); */
        /**
         * Logout button click event
         * */
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data and
                // redirect user to LoginActivity
                session.logoutUser();
                finish();
            }
        });

        ubahPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                i = new Intent(AccountActivity.this, UbahPasswordActivity.class);
                startActivity(i);
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountActivity.this, EditProfilActivity.class);
                startActivity(i);

            }
        });

    }

}
