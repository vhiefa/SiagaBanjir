package com.digitcreativestudio.siagabanjir;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ADIK on 08/09/2015.
 */
public class AboutDialogeFragment extends DialogFragment {
    Context mContext;

    public AboutDialogeFragment() {
        mContext = getActivity();}
    //membuat dialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.activity_about);

        // Create the AlertDialog object and return it
        return builder.create();
    }
    public static AboutDialogeFragment newInstance() {
        AboutDialogeFragment f = new AboutDialogeFragment();
        return f;
    }
}
