package com.augmate.sdk.scanner;

import android.os.SystemClock;
import com.augmate.sdk.logger.Log;

public class ScannerPlaceholder {
    public ScannerPlaceholder() {
        Log.debug("I'm a little scanner short and stout.");

        byte[] src = new byte[1920 * 1200 * 4];
        int[] dst = new int[1920 * 1200];

        long start = SystemClock.elapsedRealtime();

        NativeUtils.binarizeToIntBuffer(src, dst, 1920, 1200);

        long span = SystemClock.elapsedRealtime() - start;
        Log.debug("Binarizer took: %d msec", span);
    }
}
