package com.royalmail.glass_scanner_demo;

import com.augmate.sdk.logger.Log;

public class RoyalMailApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.start(this);
    }
}
