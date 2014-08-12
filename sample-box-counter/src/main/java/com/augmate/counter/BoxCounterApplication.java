package com.augmate.counter;

import android.app.Application;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.decoder.Decoder;
import com.augmate.sdk.scanner.decoder.scandit.Configuration;

public class BoxCounterApplication extends Application {
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
