package com.augmate.sdk.scanner.zxing_decoder;

import android.graphics.Point;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.logger.What;
import com.augmate.sdk.scanner.decoding.BarcodeResult;
import com.augmate.sdk.scanner.decoding.DecodingJob;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

/**
 * Wrapper around the original untouched ZXing java barcode detector
 * All benchmarks for ZXing hacks will be vs this.
 * We are only doing QR code scanning here for simplicity
 */
public class ZXingOriginalQrOnlyWrapper implements IBarcodeScannerWrapper {
    protected QRCodeReader qrCodeReader = new QRCodeReader();

    @Override
    public void process(DecodingJob job) {
        byte[] data = job.getLuminance();
        int width = job.getWidth();
        int height = job.getHeight();

        job.decodeStartedAt = What.timey();
        job.binarizationAt = What.timey();

        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            bitmap.getBlackMatrix(); // force binarization now (don't worry it caches the result)
        } catch (Exception err) {
        }

        job.locatingAt = What.timey();
        job.parsingAt = What.timey();

        // zero confidence unless we detect and decode a qrcode
        job.result.confidence = 0;

        try {
            Result result = qrCodeReader.decode(bitmap);

            if (result != null) {

                // bottomLeft, topLeft, topRight
                ResultPoint[] pts = result.getResultPoints();

                Point pt1 = new Point((int) pts[0].getX(), (int) pts[0].getY());
                Point pt2 = new Point((int) pts[0].getX(), (int) pts[0].getY());
                Point pt3 = new Point((int) pts[0].getX(), (int) pts[0].getY());
                Point pt4 = new Point(pt3.x, pt1.y); // bottom right

                job.result.setDirectly(pt1, pt2, pt3, pt4);

                // confidence of 5 = barcode decoded
                // values of 1 and 2 = sees something that might be a barcode
                // value of 0 or -1 = nothing in the image worth looking at
                job.result.confidence = 1;
                job.result.value = result.getText();
                job.result.format = BarcodeResult.Format.QRCode;
                job.result.timestamp = What.timey();
            }

        } catch (NotFoundException err) {
            // expected failure case when zxing-lib doesn't locate a qr-code. don't log.
        } catch (Exception err) {
            // unexpected failure
            Log.exception(err, "Error while detecting QR code in image");
        }

        job.decodeCompletedAt = What.timey();
    }
}
