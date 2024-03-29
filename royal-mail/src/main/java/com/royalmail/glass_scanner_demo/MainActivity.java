package com.royalmail.glass_scanner_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.augmate.scanner_demo.R;
import com.augmate.sdk.logger.Log;

public class MainActivity extends Activity {
    public static final int REQUEST_BOX_BARCODE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // turn screen on when application is deployed (makes testing easier)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BOX_BARCODE && resultCode == RESULT_OK) {
            String value = data.getStringExtra("barcodeString");
            Log.debug("Got barcode value=%s", value);

            // update result view
            ((TextView) findViewById(R.id.lastBarcode)).setText(value);
            findViewById(R.id.barcodeScanResultContainer).setVisibility(View.VISIBLE);
        } else {
            Log.debug("Got no barcode results");
            findViewById(R.id.barcodeScanResultContainer).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            Log.debug("Starting scanning activity..");

            Intent intent = new Intent(this, ScannerActivity.class);
            startActivityForResult(intent, REQUEST_BOX_BARCODE);
        }

        return super.onKeyDown(keyCode, event);
    }
}
