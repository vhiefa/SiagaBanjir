package com.digitcreativestudio.siagabanjir.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by faqih_000 on 5/9/2015.
 */
public class FloodSyncService extends Service{
    private static final Object sSyncAdapterLock = new Object();
    private static FloodSyncAdapter sFloodSyncAdapter = null;
    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock){
            if(sFloodSyncAdapter == null){
                sFloodSyncAdapter = new FloodSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sFloodSyncAdapter.getSyncAdapterBinder();
    }
}
