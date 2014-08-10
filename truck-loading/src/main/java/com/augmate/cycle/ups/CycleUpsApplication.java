package com.augmate.cycle.ups;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.decoding.Decoder;
import com.augmate.sdk.scanner.scandit_decoder.Configuration;

public class CycleUpsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.start(this);
        Log.debug("Application started");

        Decoder.ScanditConfiguration = Configuration.createFromContext(getBaseContext());
    }

    // never called in production
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.debug("Application ended");
        Log.shutdown();
    }
}
