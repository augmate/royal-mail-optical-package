package com.augmate.sdk.scanner.decoding;

import com.augmate.sdk.scanner.scandit_decoder.ScanditWrapper;
import com.augmate.sdk.scanner.zxing_decoder.ZXingWrapper;

public class DecoderQR {

    private void processUsingScandit(DecodingJob job) {
        ScanditWrapper.process(job);
    }

    private void processusingZXing(DecodingJob job) {
        ZXingWrapper.process(job);
    }

    public void process(DecodingJob job) {
        processUsingScandit(job);
    }
}
