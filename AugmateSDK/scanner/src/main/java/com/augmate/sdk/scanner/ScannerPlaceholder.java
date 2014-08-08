package com.augmate.sdk.scanner;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.scandit_decoder.ScanditWrapper;

public class ScannerPlaceholder extends FragmentActivity implements ScannerVisualization.OnScannerResultListener {

    public void testNativePath() {
        Log.debug("I'm a little scanner short and stout.");

        byte[] src = new byte[1920 * 1200 * 4];
        int[] dst = new int[1920 * 1200];

        long start = SystemClock.elapsedRealtime();

        NativeUtils.binarizeToIntBuffer(src, dst, 1920, 1200);

        long span = SystemClock.elapsedRealtime() - start;
        Log.debug("Binarizer took: %d msec", span);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        setContentView(R.layout.scanner_activity);

        Log.debug("Created scanner activity");

        ScanditWrapper.initializeScandit(getBaseContext());
    }

    @Override
    public void onBarcodeScanSuccess(String result) {
        Log.debug("Got scanning result: [%s]", result);
    }
}
