package com.augmate.scanner_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.ScannerFragmentBase;

public class ScannerActivity extends FragmentActivity implements ScannerFragmentBase.OnScannerResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_scan_activity);

        Log.debug("Created activity that uses barcode scanner");

        // default result if we don't find a value with the barcode scanner
        setResult(RESULT_CANCELED);
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
