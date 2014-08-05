package com.augmate.sdk.scanner.decoding;

import android.os.SystemClock;

public class DecodingJob {

    // filled out by job producer (android activity, java CLI, unit-test)
    private final int width;
    private final int height;
    private final byte[] luminance;
    private final long requestedAt;

    // filled out by decoder (consumer)
    public long decodedAt;
    // TODO: public Result result;


    public DecodingJob(int width, int height, byte[] luminance) {
        this.width = width;
        this.height = height;
        this.luminance = luminance;
        this.requestedAt = SystemClock.elapsedRealtime();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getLuminance() {
        return luminance;
    }
}
