package com.digitcreativestudio.siagabanjir;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ADIK on 08/09/2015.
 */
public class ConnectionDialogeFragment extends DialogFragment {
    Context mContext;

    public ConnectionDialogeFragment() {
        mContext = getActivity();}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.connection_dialog).setNegativeButton(R.string.dialog_con_true, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
    public static ConnectionDialogeFragment newInstance() {
        ConnectionDialogeFragment f = new ConnectionDialogeFragment();
        return f;
    }

}