package com.augmate.sdk.scanner.decoding;

import com.augmate.sdk.scanner.scandit_decoder.Configuration;
import com.augmate.sdk.scanner.scandit_decoder.ScanditWrapper;
import com.augmate.sdk.scanner.zxing_decoder.IBarcodeScannerWrapper;
import com.augmate.sdk.scanner.zxing_decoder.ZXingWrapper;

public class Decoder {
    IBarcodeScannerWrapper barcodeScanner;

    public static Configuration ScanditConfiguration;

    public Decoder() {
        barcodeScanner = new ScanditWrapper(ScanditConfiguration);
        //barcodeScanner = new ZXingWrapper();

        // this is me typing out a comment and its causing the cpu utilization of the application to shoot up to over 100%
        // which is kind of crazy because im not doing all that much at all..
    }

    public void process(DecodingJob job) {
        barcodeScanner.process(job);
    }
}
