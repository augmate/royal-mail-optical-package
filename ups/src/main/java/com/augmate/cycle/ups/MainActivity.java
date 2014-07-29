package com.augmate.cycle.ups;

import android.app.Activity;
import android.os.Bundle;
import com.augmate.sdk.scanner.ScannerPlaceholder;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ScannerPlaceholder();
    }
}
