package com.augmate.scanner_demo;

import com.augmate.sdk.logger.Log;

public class DemoApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.start(this);
        Log.debug("Application started");
    }

    // never called in production
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.debug("Application ended");
        Log.shutdown();
    }
}
