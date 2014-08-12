package com.augmate.sdk.scanner.decoder;

import com.augmate.sdk.scanner.decoder.scandit.Configuration;
import com.augmate.sdk.scanner.decoder.zxing.IBarcodeScannerWrapper;
import com.augmate.sdk.scanner.decoder.zxing.ZXingOriginalQrOnlyWrapper;

/**
 * Barcode decoding critical paths start here
 * must work outside of android in a standalone java CLI app
 */
public class Decoder {
    IBarcodeScannerWrapper barcodeScanner;

    public static Configuration ScanditConfiguration;

    public Decoder() {
        //barcodeScanner = new ScanditWrapper(ScanditConfiguration);
        //barcodeScanner = new ZXingHackWrapper();
        barcodeScanner = new ZXingOriginalQrOnlyWrapper();
        //barcodeScanner = new ZXingNativeWrapper();
    }

    public void process(DecodingJob job) {
        barcodeScanner.process(job);
    }
}
