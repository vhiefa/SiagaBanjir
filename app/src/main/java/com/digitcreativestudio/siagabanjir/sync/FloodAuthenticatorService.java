package com.digitcreativestudio.siagabanjir.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by faqih_000 on 5/9/2015.
 */
public class FloodAuthenticatorService extends Service{
    private FloodAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new FloodAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
