package com.augmate.cycle.ups;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.ScannerPlaceholder;
import com.augmate.sdk.voice.VoiceCaptorPlaceholder;

public class MainActivity extends Activity {

    PowerManager.WakeLock lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, VoiceCaptorPlaceholder.class);
//        startActivity(intent);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            Log.debug("Screen was off when application launched. Good thing we can spawn a wake-lock :)");
        }

        lock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, MainActivity.class.getName());

        //new ScannerPlaceholder();
        //new VoiceCaptorPlaceholder();
        //finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (lock != null) {
            Log.debug("Acquiring wake-lock..");
            lock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (lock != null) {
            Log.debug("Releasing wake-lock..");
            lock.release();
        }
    }
}
