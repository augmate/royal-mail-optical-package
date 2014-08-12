package com.augmate.counter;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import com.augmate.sdk.logger.Log;

/**
 * Turn on glass display programmatically
 */
class PowerHelper {
    private Activity mainActivity;
    private PowerManager.WakeLock lock;

    public PowerHelper(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void wake() {
        if (lock == null) {
            PowerManager powerManager = (PowerManager) mainActivity.getSystemService(Context.POWER_SERVICE);
            if (!powerManager.isScreenOn()) {
                Log.debug("Screen was off when wake was requested.");
                Log.debug("Acquiring wake-lock.");
                lock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, MainActivity.class.getName());
                powerManager.wakeUp(1000);
            }
        }
    }

    public void release() {
        if (lock != null) {
            if(lock.isHeld()) {
                Log.debug("Releasing wake-lock.");
                lock.release();
            } else {
                Log.debug("Lock found but isn't held.");
            }
            lock = null;
        }
    }
}
