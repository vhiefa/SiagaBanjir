package com.digitcreativestudio.siagabanjir;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FloodAreaActivityFragment extends Fragment {

   private static final String LOG_TAG = FloodAreaActivityFragment.class.getSimpleName();

    public FloodAreaActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

    }

    private void updateFloodArea() {
        FetchFloodAreaTask fetchFloodAreaTask = new FetchFloodAreaTask(getActivity());
        fetchFloodAreaTask.execute();
        Log.v(LOG_TAG, "Update flood area :-)");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_flood_area, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            //FetchFloodAreaTask floodAreaTask = new FetchFloodAreaTask();
            //floodAreaTask.execute();
            updateFloodArea();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
