package com.augmate.counter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import com.augmate.sdk.logger.Log;

public class MainActivity extends Activity {
    PowerHelper powerHelper = new PowerHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.debug("Build.MODEL = " + Build.MODEL);

        // spawn voice capture
//        Intent intent = new Intent(this, VoiceCaptorPlaceholder.class);
//        startActivity(intent);

        // spawn optical scanner
        Intent intent = new Intent(this, BoxScannerActivity.class);
        startActivityForResult(intent, 0x01);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0x01) {
            String value = data.getStringExtra("barcodeString");
            Log.debug("Got barcode value=%s", value);
            ((TextView)findViewById(R.id.lastBarcode)).setText(value);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //powerHelper.wake();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //powerHelper.release();
    }

}
