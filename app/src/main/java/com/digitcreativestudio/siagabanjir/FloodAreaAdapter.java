package com.digitcreativestudio.siagabanjir;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Eko.
 */
public class FloodAreaAdapter extends CursorAdapter {
    public static final String LOG_TAG = FloodAreaAdapter.class.getSimpleName();

    /**
     * Cache of the children views for a news list item.
     */
    public static class ViewHolder {
        public final TextView noView;
        public final TextView wilView;
        public final TextView kecView;
        public final TextView kelView;
        public final TextView rwView;

        public ViewHolder(View view) {
            noView = (TextView) view.findViewById(R.id.flood_area_number);
            wilView = (TextView) view.findViewById(R.id.list_item_wilayah_textview);
            kecView = (TextView) view.findViewById(R.id.list_item_kecamatan_textview);
            kelView = (TextView) view.findViewById(R.id.list_item_kelurahan_textview);
            rwView = (TextView) view.findViewById(R.id.list_item_rw_textview);
        }
    }

    public FloodAreaAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.list_item_area;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int viewType = getItemViewType(cursor.getPosition());

        String noString = cursor.getString(FloodAreaActivityFragment.COL_FLOOD_AREA_ID);
        viewHolder.noView.setText(noString);

        String wilString = cursor.getString(FloodAreaActivityFragment.COL_WIL);
        viewHolder.wilView.setText(wilString);

        String kecString = cursor.getString(FloodAreaActivityFragment.COL_KEC);
        viewHolder.kecView.setText(kecString);

        String kelString = cursor.getString(FloodAreaActivityFragment.COL_KEL);
        viewHolder.kelView.setText(kelString);

        String rwString = cursor.getString(FloodAreaActivityFragment.COL_RW);
        viewHolder.rwView.setText(rwString);

    }
}
