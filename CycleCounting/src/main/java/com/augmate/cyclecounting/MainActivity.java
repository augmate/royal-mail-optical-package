package com.augmate.cyclecounting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.augmate.sdk.scanner.ScannerPlaceholder;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, ScannerPlaceholder.class);
        startActivity(intent);
    }
}
