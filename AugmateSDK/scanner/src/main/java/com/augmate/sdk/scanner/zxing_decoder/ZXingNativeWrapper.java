package com.augmate.sdk.scanner.zxing_decoder;

import com.augmate.sdk.logger.What;
import com.augmate.sdk.scanner.NativeUtils;
import com.augmate.sdk.scanner.decoding.DecodingJob;

public class ZXingNativeWrapper implements IBarcodeScannerWrapper {

    @Override
    public void process(DecodingJob job) {
        byte[] data = job.getLuminance();
        int width = job.getWidth();
        int height = job.getHeight();

        job.decodeStartedAt = What.timey();

        job.binarizationAt = What.timey();
        job.locatingAt = What.timey();
        job.parsingAt = What.timey();

        NativeUtils.zxingNativeDecode(data, width, height);

        job.decodeCompletedAt = What.timey();
    }
}
