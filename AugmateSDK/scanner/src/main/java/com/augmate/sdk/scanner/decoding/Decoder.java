package com.augmate.sdk.scanner.decoding;

import com.augmate.sdk.scanner.NativeUtils;
import com.augmate.sdk.scanner.scandit_decoder.Configuration;
import com.augmate.sdk.scanner.zxing_decoder.IBarcodeScannerWrapper;
import com.augmate.sdk.scanner.zxing_decoder.ZXingHackWrapper;
import com.augmate.sdk.scanner.zxing_decoder.ZXingNativeWrapper;
import com.augmate.sdk.scanner.zxing_decoder.ZXingOriginalQrOnlyWrapper;

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
