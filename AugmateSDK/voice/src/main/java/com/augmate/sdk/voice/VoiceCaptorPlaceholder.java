package com.augmate.sdk.voice;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.augmate.sdk.logger.Log;

import java.util.ArrayList;

public class VoiceCaptorPlaceholder extends Activity implements IAudioDoneCallback {

    SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    private TextView promptText;
    private TextView resultsText;
    private ImageView logo;
    private ImageView pulse_ring;
    private Animation voiceAnim;

    private MediaPlayer start_sound, success_sound, error_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_capture);
        resultsText = (TextView) findViewById(R.id.results_field);
        promptText = (TextView) findViewById(R.id.prompt_field);
        logo = (ImageView) findViewById(R.id.imageView);
        pulse_ring = (ImageView) findViewById(R.id.imageView2);
        voiceAnim = new AnimationUtils().loadAnimation(this, R.anim.grow_then_fade);
        start_sound = MediaPlayer.create(this, R.raw.start_sound);
        success_sound = MediaPlayer.create(this, R.raw.correct_sound);
        error_sound = MediaPlayer.create(this, R.raw.wrong_sound);
    }

    private void startListening() {
        AugmateRecognitionListener listener = new AugmateRecognitionListener(this);
        speechRecognizer.setRecognitionListener(listener);

        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName())
                .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        speechRecognizer.startListening(recognizerIntent);
    }

    @Override
    public boolean onKeyDown(int keycode, @SuppressWarnings("NullableProblems") KeyEvent event) {

        Log.debug("Caught key-down on key=" + KeyEvent.keyCodeToString(keycode));

        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            //voiceAnim.setRepeatCount(Animation.INFINITE);
            //voiceAnim.setRepeatMode(Animation.REVERSE);
            start_sound.start();
            pulse_ring.startAnimation(voiceAnim);
            resultsText.setText("");
            promptText.setText("Listening");
            startListening();
        }
        super.onKeyDown(keycode, event);
        return true;
    }

    @Override
    public void onPartial(ArrayList<String> results) {
        resultsText.setText(TextUtils.join(", ", results) + "\n");
    }

    @Override
    public void onResults(ArrayList<String> results){
        success_sound.start();
        pulse_ring.clearAnimation();
        resultsText.setText(TextUtils.join(", ", results) + "\n");
        promptText.setText("Standby");
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onError(int error) {
        error_sound.start();
        pulse_ring.clearAnimation();
        switch (error) {
            case 6:
                promptText.setText("Error " + error + "\nStandby");
                break;
            default:
                promptText.setText("Error " + error + "\nStandby");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        finish();
    }

    @Override
    public void onDestroy(){
        if(speechRecognizer!=null)
            speechRecognizer.destroy();
        super.onDestroy();
    }
}
