package com.augmate.sdk.scanner.decoder.zxing;

import com.augmate.sdk.logger.What;
import com.augmate.sdk.scanner.NativeUtils;
import com.augmate.sdk.scanner.decoder.DecodingJob;

public class ZXingNativeWrapper implements IBarcodeScannerWrapper {

    @Override
    public void process(DecodingJob job) {
        byte[] data = job.getLuminance();
        int width = job.getWidth();
        int height = job.getHeight();

        job.decodeStartedAt = What.timey();

        job.binarizationAt = What.timey();
        job.parsingAt = What.timey();

        NativeUtils.zxingNativeDecode(data, width, height);

        job.decodeCompletedAt = What.timey();
    }
}
