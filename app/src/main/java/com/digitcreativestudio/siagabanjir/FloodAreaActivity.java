package com.digitcreativestudio.siagabanjir;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloodAreaActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flood_area);
        if (savedInstanceState == null){
            new PlaceholderFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_area, new PlaceholderFragment()).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flood_area, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // placeholder fragment
    public static class PlaceholderFragment extends android.support.v4.app.Fragment{

        public PlaceholderFragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // create some dummy data
            String[] data = {
                    "Lokasi 1 - Jakarta Selatan",
                    "Lokasi 2 - Bogor",
                    "Lokasi 3 - Tangerang",
                    "Lokasi 4 - Monas",
                    "Lokasi 5 - Istana Presiden",
                    "Lokasi 6 - Depok",
                    "Lokasi 7 - Salemba",
                    "Lokasi 8 - DKI Jakarta",
                    "Lokasi 9 - Pantai Ancol"

            };
            List<String> floodArea = new ArrayList<String>(Arrays.asList(data));

            // Now that we have some dummy data, create an ArrayAdapter.
            // The ArrayAdapter will take data from a source (like our dummy flood location)
            // use it to populate the ListView it's attached to.
            ArrayAdapter<String> floodAreaAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_area, // The name of the layout ID.
                            R.id.list_item_area_textview, // The ID of the textview to populate.
                            floodArea
                    );

            View rootView = inflater.inflate(R.layout.fragment_flood_area, container, false);

            // Get a reference to the ListView, and attach this adapter to it.
            ListView listView = (ListView) rootView.findViewById(R.id.listview_flood_area);
            listView.setAdapter(floodAreaAdapter);

            return rootView;
        }
    }
}
