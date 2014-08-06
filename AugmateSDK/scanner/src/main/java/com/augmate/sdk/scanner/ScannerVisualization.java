package com.augmate.sdk.scanner;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.*;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.decoding.DecodingJob;

public class ScannerVisualization extends Fragment implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private ScannerVisualDebugger debugger;
    private OnScannerResultListener mListener;
    private SurfaceView surfaceView;
    private CameraController cameraController = new CameraController();
    private boolean isProcessingCapturedFrames;
    private DecodeThread decodingThread;
    private boolean readyForNextFrame = true;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.debug("Activity created on thread=%d", Thread.currentThread().getId());

        // spawn a decoding thread and connect it to our message pump
        decodingThread = new DecodeThread(new MsgHandler(this));
        decodingThread.start();
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

    @Override
    public void onResume() {
        super.onResume();
        Log.debug("Resuming");

        SurfaceHolder holder = surfaceView.getHolder();
        holder.removeCallback(this);
        holder.addCallback(this);
        isProcessingCapturedFrames = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.debug("Paused");
        isProcessingCapturedFrames = false;
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

        // shutdown
        decodingThread.getMsgPump()
                .obtainMessage(R.id.stopDecodingThread)
                .sendToTarget();

        Log.debug("Joining decoding-thread..");

        try {
            decodingThread.join(5000);
        } catch (InterruptedException e) {
            Log.exception(e, "Interrupted while waiting on decoding thread");
        }

        Log.debug("Decoding-thread has been shutdown.");
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.debug("Surface has been created");
        Log.debug("Surface has size of %d x %d", surfaceHolder.getSurfaceFrame().width(), surfaceHolder.getSurfaceFrame().height());
        cameraController.beginFrameCapture(surfaceHolder, this, 640, 360);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Log.debug("Surface has changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.debug("Surface has been destroyed");
        cameraController.endFrameCapture();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (!isProcessingCapturedFrames) {
            Log.debug("Ignoring new frame because we are paused");
            return;
        }

        //Log.debug("New frame is available @ 0x%X", bytes.hashCode());

        if (readyForNextFrame) {
            // kick-off processing in a different thread
            decodingThread.getMsgPump()
                    .obtainMessage(R.id.newDecodeJob, new DecodingJob(640, 360, bytes))
                    .sendToTarget();

            // change capture-buffer to prevent camera from modifying buffer sent to decoder
            // this avoids copying buffers on every frame
            cameraController.changeFrameBuffer();
            readyForNextFrame = false;
        }

        // queue up next frame for capture
        cameraController.requestAnotherFrame();
    }

    private void onDecodeCompleted(DecodingJob obj) {
        Log.debug("Decoded; queue-overhead=%d msec, binarization=%d msec, total=%d msec", obj.queueDuration(), obj.binarizationDuration(), obj.totalDuration());
        readyForNextFrame = true;
    }

    public interface OnScannerResultListener {
        public void onBarcodeScanSuccess(String result);
    }

    // handles messages pushed into parent activity's thread
    public final class MsgHandler extends Handler {
        private ScannerVisualization parent;

        MsgHandler(ScannerVisualization parent) {
            this.parent = parent;
            Log.debug("Msg Pump created on thread=%d", Thread.currentThread().getId());
        }

        @Override
        public void handleMessage(Message msg) {
            //Log.debug("On thread=%d got msg=%d", Thread.currentThread().getId(), msg.what);
            if (msg.what == R.id.decodeJobCompleted) {
                parent.onDecodeCompleted((DecodingJob) msg.obj);
            }
        }

        // push decode message job into a different thread
        public void queueDecodeJob(DecodingJob job) {
            decodingThread.getMsgPump()
                    .obtainMessage(R.id.newDecodeJob, job)
                    .sendToTarget();
        }
    }
}
