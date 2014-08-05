package com.augmate.cycle.ups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.augmate.sdk.voice.VoiceCaptorPlaceholder;

public class MainActivity extends Activity {
    PowerHelper powerHelper = new PowerHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // spawn voice capture
        Intent intent = new Intent(this, VoiceCaptorPlaceholder.class);
        startActivity(intent);

        // or spawn optical scanner
//        Intent intent = new Intent(this, ScannerPlaceholder.class);
//        startActivity(intent);

        finish();
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
