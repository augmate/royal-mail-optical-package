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
import com.google.zxing.ResultPoint;

public abstract class ScannerFragmentBase extends Fragment implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private ScannerVisualDebugger debugger;
    private OnScannerResultListener mListener;
    private SurfaceView surfaceView;
    private CameraController cameraController = new CameraController();
    private boolean isProcessingCapturedFrames;
    private DecodeThread decodingThread;
    private boolean readyForNextFrame = true;
    private FramebufferSettings frameBufferSettings = new FramebufferSettings(1280, 720);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.debug("Activity created on thread=%d", Thread.currentThread().getId());

        // spawn a decoding thread and connect it to our message pump
        decodingThread = new DecodeThread(new MsgHandler(this));
        decodingThread.start();
    }

    /**
     * Override this method in your own fragment, just don't forget to call setupScannerActivity
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scanner_viz_fragment, container, false);

        ScannerVisualDebugger dbg = (ScannerVisualDebugger) view.findViewById(R.id.scanner_visual_debugger);
        SurfaceView sv = (SurfaceView) view.findViewById(R.id.camera_preview);

        setupScannerActivity(sv, dbg);

        Log.debug("Default Scanner Activity created.");

        return view;
    }

    public abstract View configureFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void setupScannerActivity(SurfaceView surfaceView, ScannerVisualDebugger scannerVisualDebugger) {
        this.surfaceView = surfaceView;
        this.debugger = scannerVisualDebugger;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.debug("Resuming");

        if (surfaceView == null) {
            Log.error("surfaceView is null. Must setupScannerActivity() with a valid SurfaceView in onCreateView().");
            return;
        }

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
        Log.debug("  Surface has size of %d x %d", surfaceHolder.getSurfaceFrame().width(), surfaceHolder.getSurfaceFrame().height());
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Log.debug("Surface has changed");
        Log.debug("  Surface has size of %d x %d", surfaceHolder.getSurfaceFrame().width(), surfaceHolder.getSurfaceFrame().height());

        // configure debugging render-target
        debugger.setFrameBufferSettings(frameBufferSettings.width, frameBufferSettings.height);

        // configure camera frame-grabbing
        cameraController.endFrameCapture();
        cameraController.beginFrameCapture(surfaceHolder, this, frameBufferSettings.width, frameBufferSettings.height);
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
            // kick-off frame decoding in a dedicated thread
            DecodingJob job = new DecodingJob(frameBufferSettings.width, frameBufferSettings.height, bytes, debugger.getNextDebugBuffer());

            decodingThread.getMsgPump()
                    .obtainMessage(R.id.newDecodeJob, job)
                    .sendToTarget();

            // change capture-buffer to prevent camera from modifying buffer sent to decoder
            // this avoids copying buffers on every frame
            cameraController.changeFrameBuffer();
            readyForNextFrame = false;
        }

        // queue up next frame for capture
        cameraController.requestAnotherFrame();
    }

    private void onDecodeCompleted(DecodingJob job) {
        Log.debug("Decoded; queue-overhead=%d msec, binarization=%d msec, total=%d msec", job.queueDuration(), job.binarizationDuration(), job.totalDuration());

        if (job.result != null) {
            ResultPoint[] pts = job.result.getResultPoints();
            Log.info("Found qr code points: (%.1f,%.1f) (%.1f,%.1f) (%.1f,%.1f)", pts[0].getX(), pts[0].getY(), pts[1].getX(), pts[1].getY(), pts[2].getX(), pts[2].getY());

            //job.result.getText()
        }

        // tell debugger they can use the buffer we wrote decoding debug data to
        debugger.flipDebugBuffer();
        // tell frame-grabber we can push next (or most recently grabbed) frame to the decoder
        readyForNextFrame = true;

        // TODO: try pushing next frame (if we got one) from here
        // may reduce delays by length of one frame (ie 50ms at 20fps)
    }

    public interface OnScannerResultListener {
        public void onBarcodeScanSuccess(String result);
    }

    private class FramebufferSettings {
        public final int width;
        public final int height;

        private FramebufferSettings(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    // handles messages pushed into parent activity's thread
    public final class MsgHandler extends Handler {
        private ScannerFragmentBase parent;

        MsgHandler(ScannerFragmentBase parent) {
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
