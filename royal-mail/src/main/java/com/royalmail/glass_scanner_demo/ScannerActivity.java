package com.royalmail.glass_scanner_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.augmate.scanner_demo.R;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.ScannerFragmentBase;

public class ScannerActivity extends Activity implements ScannerFragmentBase.OnScannerResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_scan_activity);

        Log.debug("Created activity that uses barcode scanner");
    }

    @Override
    public void onBarcodeScanSuccess(String result) {
        Log.debug("Got scanning result: [%s]", result);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("barcodeString", result);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
