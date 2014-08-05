package com.augmate.sdk.scanner;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import com.augmate.sdk.logger.Log;

public class ScannerVisualization extends Fragment implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private ScannerVisualDebugger debugger;
    private OnScannerResultListener mListener;
    private SurfaceView surfaceView;
    private CameraController cameraController = new CameraController();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.debug("Fragment created?");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scanner_viz_fragment, container, false);
        //debugger = (ScannerVisualDebugger) view.findViewById(R.id.scanner_visual_debugger);
        surfaceView = (SurfaceView) view.findViewById(R.id.camera_preview);

        ViewGroup.LayoutParams layout = surfaceView.getLayoutParams();
        layout.width = 640;
        layout.height = 360;
        surfaceView.setLayoutParams(layout);
        //debugger.setLayoutParams(layout);

        Log.debug("View created?");

        return view;
    }

    private void layoutViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.debug("Resuming");

        SurfaceHolder holder = surfaceView.getHolder();
        holder.removeCallback(this);
        holder.addCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.debug("Paused");
    }

    public void onScannerSuccess(String data) {
        if (mListener != null) {
            mListener.onBarcodeScanSuccess(data);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        Log.debug("Attached");
        super.onAttach(activity);
        try {
            mListener = (OnScannerResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnScannerResultListener");
        }
    }

    @Override
    public void onDetach() {
        Log.debug("Detached");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.debug("Surface has been created");
        //cameraController.startFrameCapture(surfaceHolder, this, 640, 360);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Log.debug("Surface has changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.debug("Surface has been destroyed");
        //cameraController.stopFrameCapture();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Log.debug("New frame is available @ 0x%X", bytes.hashCode());
    }

    public interface OnScannerResultListener {
        public void onBarcodeScanSuccess(String result);
    }

}
