package com.augmate.cycle.ups;

import android.app.Activity;
import android.os.Bundle;
import com.augmate.sdk.scanner.ScannerPlaceholder;

public class MainActivity extends Activity {
    PowerHelper powerHelper = new PowerHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, VoiceCaptorPlaceholder.class);
//        startActivity(intent);

        new ScannerPlaceholder();
        //new VoiceCaptorPlaceholder();
        //finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        powerHelper.wake();
    }

    @Override
    protected void onPause() {
        super.onPause();
        powerHelper.release();
    }

}
