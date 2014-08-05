package com.augmate.sdk.scanner.decoding;


public class DecoderQR {

    private byte[] binaryMatrix = new byte[0];

    public void process(DecodingJob job) {
        byte[] data = job.getLuminance();
        int width = job.getWidth();
        int height = job.getHeight();


        if(binaryMatrix.length < width * height)
            binaryMatrix = new byte[width * height];
    }
}
