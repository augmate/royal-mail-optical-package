package com.augmate.sdk.scanner.zxing_decoder;

import com.augmate.sdk.scanner.decoding.DecodingJob;

public interface IBarcodeScannerWrapper {
    void process(DecodingJob job);
}
