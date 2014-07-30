package com.augmate.sdk.voice;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class AugmateRecognitionListener implements RecognitionListener{

    private static final String TAG = "com.augmate.ups.cycle.voice";
    private final IAudioDoneCallback myCallback;

    AugmateRecognitionListener(IAudioDoneCallback iAudioDoneCallback){
        myCallback = iAudioDoneCallback;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "on ready for speech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "on beginning of speech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {}

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "on buffer received");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "on end of speech");
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "on error: " + error);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "on results");
        ArrayList<String> stringArrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for (String str : stringArrayList) {
            Log.d(TAG, "result=" + str);
        }
        myCallback.onSuccess(stringArrayList);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "on partial results");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "on event");
    }

}
