package com.augmate.cycle.ups;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.ScannerFragmentBase;

public class BoxScannerActivity extends FragmentActivity implements ScannerFragmentBase.OnScannerResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        setContentView(R.layout.activity_scan_box);

        Log.debug("Created activity that uses barcode scanner");
    }

    @Override
    public void onBarcodeScanSuccess(String result) {
        Log.debug("Got scanning result: [%s]", result);
    }
}
