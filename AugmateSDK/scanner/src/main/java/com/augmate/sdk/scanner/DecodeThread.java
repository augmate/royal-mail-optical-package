package com.augmate.sdk.scanner;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.decoding.Decoder;
import com.augmate.sdk.scanner.decoding.DecodingJob;

import java.util.concurrent.CountDownLatch;

final class DecodeThread extends Thread {
    private DecodeThreadHandler msgPump;
    private CountDownLatch isMsgPumpReady;
    private Handler producerThreadHandler;

    DecodeThread(Handler producerThreadHandler) {
        super();
        this.producerThreadHandler = producerThreadHandler;
        isMsgPumpReady = new CountDownLatch(1);
        Log.debug("Decoding thread spun up with id=%d", Thread.currentThread().getId());
    }

    // blocks and waits until entry-point has been hit
    // and the decode-thread handler has been initialized in the new thread
    // this call can come from any other thread
    Handler getMsgPump() {
        try {
            isMsgPumpReady.await();
        } catch (InterruptedException e) {
            Log.exception(e, "Failed to wait for initialization latch to unlock for decoding thread");
        }
        return msgPump;
    }

    // entry-point into new thread
    // spin up a message handler to listen to the message loop
    @Override
    public void run() {
        Thread.currentThread().setName("decoder");
        Log.debug("Running thread with id=%d vs %d", Thread.currentThread().getId());
        Looper.prepare();
        msgPump = new DecodeThreadHandler();
        isMsgPumpReady.countDown();
        Looper.loop();
        Log.debug("Exiting thread.");
    }

    private void shutdown() {
        Log.debug("Shutting down message pump..");
        msgPump.getLooper().quit();
    }

    private final class DecodeThreadHandler extends Handler {
        private Decoder decoder = new Decoder();

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == R.id.newDecodeJob) {
                processDecodeJobRequest((DecodingJob) msg.obj);
            } else if (msg.what == R.id.stopDecodingThread) {
                shutdown();
            }
        }

        private void processDecodeJobRequest(DecodingJob job) {
            //Log.debug("Got decoding job with buffer @ 0x%X", job.getLuminance().hashCode());

            // the good stuff begins here
            decoder.process(job);

            producerThreadHandler
                    .obtainMessage(R.id.decodeJobCompleted, job)
                    .sendToTarget();
        }
    }
}
