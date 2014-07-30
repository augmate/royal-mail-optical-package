package com.augmate.cycle.ups;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.augmate.sdk.scanner.ScannerPlaceholder;
import com.augmate.sdk.voice.VoiceCaptorPlaceholder;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, VoiceCaptorPlaceholder.class);
//        startActivity(intent);

        new ScannerPlaceholder();
        new VoiceCaptorPlaceholder();
        finish();
    }
}
