package com.augmate.sdk.scanner.zxing_decoder;


import com.augmate.sdk.logger.Log;
import com.augmate.sdk.logger.What;
import com.augmate.sdk.scanner.NativeUtils;
import com.augmate.sdk.scanner.decoding.DecodingJob;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

public class ZXingWrapper {

    public static byte[] binaryMatrix = new byte[0];

    public static void process(DecodingJob job) {
        byte[] data = job.getLuminance();
        int width = job.getWidth();
        int height = job.getHeight();

        job.decodeStartedAt = What.timey();

        if(binaryMatrix.length < width * height)
            binaryMatrix = new byte[width * height];

        job.binarizationAt = What.timey();

        NativeUtils.binarize(data, binaryMatrix, width, height);
        NativeUtils.binarizeToIntBuffer(data, job.getDebugOutputBuffer(), width, height);

        // give ZXing a try
        PatternFinder finder = new PatternFinder(binaryMatrix, width, height);

        try {
            FinderPatternInfo info = finder.find(null);
            job.result = new Result("Detect Only", null, new ResultPoint[]{info.getBottomLeft(), info.getTopLeft(), info.getTopRight()}, BarcodeFormat.QR_CODE);
        }
        catch (NotFoundException err) {
            // expected failure case when zxing-lib doesn't locate a qr-code. don't log.
        } catch (Exception err) {
            // unexpected failure
            Log.exception(err, "Error while detecting QR code in image");
        }

        job.decodeCompletedAt = What.timey();
    }
}
