package com.augmate.sdk.scanner.decoding;


import com.augmate.sdk.logger.What;
import com.augmate.sdk.scanner.NativeUtils;

public class DecoderQR {

    private byte[] binaryMatrix = new byte[0];

    public void process(DecodingJob job) {
        byte[] data = job.getLuminance();
        int width = job.getWidth();
        int height = job.getHeight();

        job.decodeStartedAt = What.timey();

        if(binaryMatrix.length < width * height)
            binaryMatrix = new byte[width * height];

        job.binarizationAt = What.timey();

        NativeUtils.binarize(data, binaryMatrix, width, height);

        job.decodeCompletedAt = What.timey();
    }
}
