package com.augmate.sdk.scanner.decoder.zxing;

import android.graphics.Point;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.logger.What;
import com.augmate.sdk.scanner.decoder.BarcodeResult;
import com.augmate.sdk.scanner.decoder.DecodingJob;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

/**
 * Wrapper around the original untouched ZXing java barcode detector
 * All benchmarks for ZXing hacks will be compared to this.
 * We are only doing QR code scanning here for speed and simplicity
 */
public class ZXingOriginalQrOnlyWrapper implements IBarcodeScannerWrapper {
    protected QRCodeReader qrCodeReader = new QRCodeReader();

    @Override
    public void process(DecodingJob job) {
        byte[] data = job.getLuminance();
        int width = job.getWidth();
        int height = job.getHeight();

        // zero confidence unless we detect and decode a qrcode
        job.result.confidence = 0;

        job.decodeStartedAt = What.timey();
        job.binarizationAt = What.timey();

        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {

            // force binarization now. result gets cached.
            // this way we can measure how long this process takes. (it's pretty slow)
            bitmap.getBlackMatrix();

            job.locatingAt = What.timey();
            job.parsingAt = What.timey();

            Result result = qrCodeReader.decode(bitmap);

            if (result != null) {

                // bottomLeft, topLeft, topRight
                ResultPoint[] pts = result.getResultPoints();

                Point pt1 = new Point((int) pts[0].getX(), (int) pts[0].getY());
                Point pt2 = new Point((int) pts[1].getX(), (int) pts[1].getY());
                Point pt3 = new Point((int) pts[2].getX(), (int) pts[2].getY());

                Point upVector = new Point(pt2.x - pt1.x, pt2.y - pt1.y);;
                Point rightVector = new Point(pt3.x - pt2.x, pt3.y - pt2.y);;

                Point pt4 = new Point(pt1.x + rightVector.x, pt1.y + rightVector.y); // bottom right

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
            Log.exception(err, "Error detecting QR code using ZXing");
        }

        job.decodeCompletedAt = What.timey();
    }
}
