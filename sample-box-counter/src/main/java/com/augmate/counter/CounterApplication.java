package com.augmate.counter;

import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.DecoderManager;
import com.augmate.sdk.scanner.decoder.scandit.Configuration;

public class CounterApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.start(this);
        Log.debug("Application started");

        // if using the scandit scanner, configure it here
        DecoderManager.ScanditConfiguration = Configuration.createFromContext(getBaseContext());
    }

    // never called in production
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.debug("Application ended");
        Log.shutdown();
    }
}
