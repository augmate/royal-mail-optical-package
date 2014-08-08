package com.augmate.sdk.scanner.scandit_decoder;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import com.augmate.sdk.logger.Log;
import com.augmate.sdk.logger.What;
import com.augmate.sdk.scanner.decoding.DecodingJob;
import com.mirasense.scanditsdk.ScanditSDKBarcodeReader;

// all reader interaction reversed out of scandit sdk

public class ScanditWrapper {
    public static ScanditSDKBarcodeReader reader;

    public static void initializeScandit(Context ctx) {
        reader = new ScanditSDKBarcodeReader();
        String appKey = "r5Zv3MXCEeOFwbn5ZXY54sJhMiM33cd/9czhbX62fmA";

        String platformAppId = ctx.getPackageName();
        String deviceId = Settings.Secure.getString(ctx.getContentResolver(), "android_id");
        if ((deviceId == null) || (deviceId.length() < 16)) {
            deviceId = "0000000000000000";
        }
        reader.setLegacyDeviceId(SbSystemUtils.sha1Digest(deviceId));
        reader.setDeviceId(SbSystemUtils.sha1Digest(deviceId));
        reader.setDeviceModel(Build.MODEL);
        reader.setPlatform("android");
        reader.setPlatformVersion(Build.VERSION.RELEASE);
        reader.setUsedFramework(ScanditSDKGlobals.usedFramework);

        reader.initialize(ctx.getFilesDir());
        reader.setupLicenseInformation(appKey, platformAppId);
        reader.setEnable2dRecognition(true);
    }

    public static void process(DecodingJob job) {
        byte[] data = job.getLuminance();
        int width = job.getWidth();
        int height = job.getHeight();

        job.decodeStartedAt = What.timey();

        Log.debug("asking scandit to processImage()..");
        ScanditWrapper.reader.processImage(data, width * height, width, height);
        Log.debug("asking scandit to processImage().. Done");

        byte[][] results = ScanditWrapper.reader.fetchResults();

        if(results != null) {
            if(results.length > 0) {
                Log.debug("result set has %d items", results.length);

                for(byte[] fetchedBytes : results) {
                    Log.debug("  item bytes = " + fetchedBytes);
                    if(fetchedBytes == null)
                        continue;
                    String fetchedResult = new String(fetchedBytes);
                    String[] barcodeStr = getCodeAndSymbology(fetchedResult);

                    Log.debug("    -> fetchedResult: [%s]", fetchedResult);
                    if(barcodeStr != null && barcodeStr.length >= 2)
                        Log.debug("    -> barcodeStr: [%s] [%s]", barcodeStr[0], barcodeStr[1]);
                }
            }
        } else {
            Log.debug("got null results list");
        }

        if(ScanditWrapper.reader.getCodeCenter() != null) {
            int[] center = ScanditWrapper.reader.getCodeCenter();
            int[] locationSize = ScanditWrapper.reader.getCodeSize();

            int x = center[0];
            int y = center[1];

            int sizeX = locationSize[0];
            int sizeY = locationSize[1];

            int confidence = ScanditWrapper.reader.getCodeConfidence();

            Log.debug("found code center center: %d,%d  size: %d,%d  confidence: %d", x, y, sizeX, sizeY, confidence);
        }

        job.binarizationAt = What.timey();
        job.decodeCompletedAt = What.timey();
    }

    private static String[] getCodeAndSymbology(String concatString)
    {
        if ((concatString == null) || (concatString.trim().length() == 0)) {
            return null;
        }
        String[] result = new String[2];
        int i = concatString.indexOf(":symbology:");
        if (i < 0) {
            return null;
        }
        result[0] = concatString.substring(0, i).trim();
        result[1] = concatString.substring(i + 11).trim();

        return result;
    }
}
