package com.augmate.sdk.scanner;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.decoder.DecodingJob;

public abstract class ScannerFragmentBase extends Fragment implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private FramebufferSettings frameBufferSettings = new FramebufferSettings(1024, 576);
    private CameraController cameraController = new CameraController();
    private boolean isProcessingCapturedFrames;
    private OnScannerResultListener mListener;
    private boolean readyForNextFrame = true;
    private ScannerVisualDebugger dbgVisualizer;
    private DecoderThread decoderThread;
    private SurfaceView surfaceView;
    private int framesSkipped = 0;

    /**
     * Must call this method from a place like onCreateView() for the scanner to work
     *
     * @param surfaceView           SurfaceView is required
     * @param scannerVisualDebugger ScannerVisualDebugger is optional
     */
    public void setupScannerActivity(SurfaceView surfaceView, ScannerVisualDebugger scannerVisualDebugger) {
        Log.debug("Configuring scanner fragment.");

        this.surfaceView = surfaceView;
        this.dbgVisualizer = scannerVisualDebugger;
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

        startDecodingThread();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.debug("Paused");
        isProcessingCapturedFrames = false;

        // stop camera frame-grab immediately, let go of preview-surface, and release camera
        cameraController.endFrameCapture();

        // stop decoding thread
        shutdownDecodingThread();
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

    private void startDecodingThread() {
        if (decoderThread == null) {
            // spawn a decoding thread and connect it to our message pump
            decoderThread = new DecoderThread(new ScannerFragmentMessages(this));
            decoderThread.start();
        }
    }

    private void shutdownDecodingThread() {

        if (decoderThread != null) {
            // shutdown

            Log.debug("Asking message pump to exit..");

            decoderThread.getMessagePump()
                    .obtainMessage(R.id.decodingThreadShutdown)
                    .sendToTarget();

            Log.debug("Waiting on decoding-thread to exit (timeout: 5s)");

            try {
                decoderThread.join(5000);
            } catch (InterruptedException e) {
                Log.exception(e, "Interrupted while waiting on decoding thread");
            }

            decoderThread = null;
            Log.debug("Decoding-thread has been shutdown.");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Log.debug("Surface has size of %d x %d", surfaceHolder.getSurfaceFrame().width(), surfaceHolder.getSurfaceFrame().height());

        // configure debugging render-target
        if (dbgVisualizer != null)
            dbgVisualizer.setFrameBufferSettings(frameBufferSettings.width, frameBufferSettings.height);

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
            DecodingJob job = new DecodingJob(frameBufferSettings.width, frameBufferSettings.height, bytes, dbgVisualizer != null ? dbgVisualizer.getNextDebugBuffer() : null);

            decoderThread.getMessagePump()
                    .obtainMessage(R.id.decodingThreadNewJob, job)
                    .sendToTarget();

            // change capture-buffer to prevent camera from modifying buffer sent to decoder
            // this avoids copying buffers on every frame
            cameraController.changeFrameBuffer();
            readyForNextFrame = false;
            framesSkipped = 0;
        } else {
            framesSkipped++;
        }

        // queue up next frame for capture
        cameraController.requestAnotherFrame();
    }

    private void onJobCompleted(DecodingJob job) {
        Log.debug("Job stats: skipped frames=%d, binarization=%d msec, total=%d msec", framesSkipped, job.binarizationDuration(), job.totalDuration());

        if (job.result != null && job.result.confidence > 0) {
            Point[] pts = job.result.corners;
            Log.info("  Result={%s} with confidence=%.2f", job.result.value, job.result.confidence);

            if (dbgVisualizer != null) {
                dbgVisualizer.setPoints(pts);
                dbgVisualizer.setBarcodeValue(job.result.value);
            }

            if (mListener != null) {
                mListener.onBarcodeScanSuccess(job.result.value);
            }
        }

        // tell debugger they can use the buffer we wrote decoding debug data to
        if (dbgVisualizer != null)
            dbgVisualizer.flipDebugBuffer();
        // tell frame-grabber we can push next (or most recently grabbed) frame to the decoder
        readyForNextFrame = true;

        // TODO: try pushing next frame (if we got one) from here
        // may reduce delays by length of one frame (ie 50ms at 20fps)
    }

    /**
     * Provides barcode decoding results to a parent Activity (on its own thread)
     * Must be implemented by parent Activity
     */
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

    /**
     * pushes messages into activity thread from the decoder thread
     */
    public final class ScannerFragmentMessages extends Handler {
        private ScannerFragmentBase fragment;

        ScannerFragmentMessages(ScannerFragmentBase fragment) {
            this.fragment = fragment;
            Log.debug("Msg Pump created on thread=%d", Thread.currentThread().getId());
        }

        @Override
        public void handleMessage(Message msg) {
            //Log.debug("On thread=%d got msg=%d", Thread.currentThread().getId(), msg.what);
            if (msg.what == R.id.scannerFragmentJobCompleted) {
                fragment.onJobCompleted((DecodingJob) msg.obj);
            }
        }
    }
}
