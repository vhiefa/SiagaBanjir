package com.digitcreativestudio.siagabanjir;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by Afifatul on 8/23/2015.
 */
public class SettingActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener{

    Preference home_location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);


        Preference home_notification = (Preference) findPreference("home_notification");
        home_location = (Preference) findPreference("home_location");

        SharedPreferences mPrefs = getSharedPreferences("HomeLocPref", 0);
        String str = mPrefs.getString("home_location", ""); //str merupakan value atau korrdinat home

        if (!str.equals("")) {
            home_location.setSummary(str);
        }


        home_notification.setEnabled(true);
        if (home_location.getSummary().equals("")){
            home_notification.setEnabled(false);
        }

        home_location.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent i = null;
                i = new Intent(SettingActivity.this, SetHomeActivity.class);
                startActivity(i);
                return true;
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }


    @Override
    public void onResume(){
        super.onResume();

        SharedPreferences mPrefs = getSharedPreferences("HomeLocPref",0);
        String str = mPrefs.getString("home_location", "");
        if (!str.equals("")) {
            home_location.setSummary(str);
        }
    }

}



