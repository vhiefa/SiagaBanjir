package com.digitcreativestudio.siagabanjir;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.data.FloodContract.FloodAreaEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class FloodAreaActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks{


    private static final String LOG_TAG = FloodAreaActivityFragment.class.getSimpleName();
    private FloodAreaAdapter mFloodAreaAdapter;
    private static final int FLOOD_AREA_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";
    int mPosition;


    private static final String[] FLOOD_AREA_COLUMNS = {

            FloodAreaEntry.TABLE_NAME + "." + FloodAreaEntry._ID,
            FloodAreaEntry.COLUMN_FLOOD_AREA_ID,
            FloodAreaEntry.COLUMN_WIL,
            FloodAreaEntry.COLUMN_KEC,
            FloodAreaEntry.COLUMN_KEL,
            FloodAreaEntry.COLUMN_RW
    };

    public static final int COL_FLOOD_AREA_ID = 1;
    public static final int COL_WIL = 2;
    public static final int COL_KEC = 3;
    public static final int COL_KEL = 4;
    public static final int COL_RW = 5;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    public FloodAreaActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FLOOD_AREA_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

    }

    public void updateFloodArea() {
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


        mFloodAreaAdapter = new FloodAreaAdapter(getActivity(), null, 0);

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
        final View rootView = inflater.inflate(R.layout.fragment_flood_area, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_flood_area);
        listView.setAdapter(mFloodAreaAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String floodArea = (String) mFloodAreaAdapter.getItem(position);
                Toast.makeText(getActivity(), floodArea, Toast.LENGTH_SHORT).show();
                Cursor cursor = mFloodAreaAdapter.getCursor();
                if(cursor != null && cursor.moveToPosition(position)){
                    ((Callback)getActivity())
                            .onItemSelected(cursor.getString(COL_FLOOD_AREA_ID));
                }
                mPosition = position;
                Log.v(LOG_TAG,"position :" + position);

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    public void onResume() {
        super.onResume();
            getLoaderManager().restartLoader(FLOOD_AREA_LOADER, null, this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Sort order:  Ascending, by id
        String sortOrder = FloodAreaEntry._ID + " ASC";
        Uri floodAreaFromUri = FloodAreaEntry.buildFloodArea();
        Log.v(LOG_TAG, "URI: " + floodAreaFromUri);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                floodAreaFromUri,
                FLOOD_AREA_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mFloodAreaAdapter.swapCursor((Cursor) data);

    }

    @Override
    public void onLoaderReset(Loader loader) {
        mFloodAreaAdapter.swapCursor(null);

    }

}
