package com.augmate.cycle.ups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.augmate.sdk.scanner.ScannerFragmentBase;
import com.augmate.sdk.scanner.ScannerVisualDebugger;

public class CustomScannerFragment extends ScannerFragmentBase {
    @Override
    public View configureFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        ScannerVisualDebugger dbg = (ScannerVisualDebugger) view.findViewById(R.id.scanner_visual_debugger);
        SurfaceView sv = (SurfaceView) view.findViewById(R.id.camera_preview);

        setupScannerActivity(sv, dbg);

        return view;
    }
}
