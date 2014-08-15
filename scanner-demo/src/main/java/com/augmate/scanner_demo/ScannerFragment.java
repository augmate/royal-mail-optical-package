package com.augmate.scanner_demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.augmate.sdk.scanner.ScannerFragmentBase;

/**
 * Note about extending ScannerFragmentBase:
 * When overriding methods, make sure to call their base versions
 * Specifically: onPause, onResume, onAttach, onDetach
 */
public class ScannerFragment extends ScannerFragmentBase {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.box_scan_fragment, container, false);

        //ScannerVisualDebugger dbg = (ScannerVisualDebugger) view.findViewById(R.id.scanner_visual_debugger);
        SurfaceView sv = (SurfaceView) view.findViewById(R.id.camera_preview);

        setupScannerActivity(sv, null);

        return view;
    }
}
