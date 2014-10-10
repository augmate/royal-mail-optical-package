package com.augmate.sdk.scanner.decoder.zxing;

import com.augmate.sdk.logger.Log;
import com.augmate.sdk.scanner.NativeUtils;
import com.google.zxing.Binarizer;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SimpleBinarizer extends Binarizer {

    private static final int LUMINANCE_BITS = 5;
    private static final int LUMINANCE_SHIFT = 8 - LUMINANCE_BITS;
    private static final int LUMINANCE_BUCKETS = 1 << LUMINANCE_BITS;
    private static final byte[] EMPTY = new byte[0];

    private byte[] getBlackRowBuffer = new byte[1024];
    private BitMatrix packedBitMatrix;
    private int[] packedBitMatrixBuffer = new int[0];
    private int[] blackRowBucketsBuffer = new int[LUMINANCE_BUCKETS];

    public SimpleBinarizer(LuminanceSource source, BitMatrix matrix) {
        super(source);
        getBlackRowBuffer = EMPTY;
        packedBitMatrix = matrix;

        try {
            Field bitsField = packedBitMatrix.getClass().getDeclaredField("bits");
            bitsField.setAccessible(true);
            packedBitMatrixBuffer = (int[]) bitsField.get(packedBitMatrix);
        } catch (Exception e) {
            Log.exception(e, "Failed to extract bits array out of BitMatrix");
        }
    }

    // modified to use a native estimator
    // Applies simple sharpening to the row data to improve performance of the 1D Readers.
    @Override
    public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        if (row == null || row.getSize() < width) {
            row = new BitArray(width);
        } else {
            row.clear();
        }

        if (getBlackRowBuffer.length != width) {
            getBlackRowBuffer = new byte[width];
        }

        byte[] localLuminances = source.getRow(y, getBlackRowBuffer);

        for(int x = 0; x < LUMINANCE_BUCKETS; x ++) {
            blackRowBucketsBuffer[x] = 0;
        }
        for (int x = 0; x < width; x++) {
            int pixel = localLuminances[x] & 0xff;
            blackRowBucketsBuffer[pixel >> LUMINANCE_SHIFT]++;
        }

        int blackPoint = NativeUtils.estimateBlackPoint(blackRowBucketsBuffer, blackRowBucketsBuffer.length);

        int left = localLuminances[0] & 0xff;
        int center = localLuminances[1] & 0xff;
        for (int x = 1; x < width - 1; x++) {
            int right = localLuminances[x + 1] & 0xff;
            // A simple -1 4 -1 box filter with a weight of 2.
            int luminance = ((center << 2) - left - right) >> 1;
            if (luminance < blackPoint) {
                row.set(x);
            }
            left = center;
            center = right;
        }
        return row;
    }


    // modified to run entirely native (in c)
    @Override
    public BitMatrix getBlackMatrix() throws NotFoundException {
        LuminanceSource source = getLuminanceSource();
        int width = source.getWidth();
        int height = source.getHeight();

        byte[] luminance = source.getMatrix();

        if(packedBitMatrixBuffer == null) {
            Log.error("Sanity failure: packed bit matrix is NULL. Cannot perform binarization.");
            return packedBitMatrix;
        }

        Arrays.fill(packedBitMatrixBuffer, 0);

        NativeUtils.globalHistogramBinarizeToIntBuffer(luminance, packedBitMatrixBuffer, width, height);

        return packedBitMatrix;
    }

    @Override
    public Binarizer createBinarizer(LuminanceSource source) {
        return new GlobalHistogramBinarizer(source);
    }
}
