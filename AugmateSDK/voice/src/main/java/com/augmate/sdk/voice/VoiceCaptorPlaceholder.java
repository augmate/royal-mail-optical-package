package com.augmate.sdk.voice;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.augmate.sdk.logger.Log;

import java.util.ArrayList;

public class VoiceCaptorPlaceholder extends Activity implements IAudioDoneCallback {
    AugmateRecognitionListener listener;
    SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    private TextView promptText;
    private TextView resultsText;
    private ImageView logo;
    private ImageView pulse_ring;
    private Animation voiceAnim;

    private MediaPlayer start_sound, success_sound, error_sound;
    private SliderView mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.voice_capture);
        listener = new AugmateRecognitionListener(this);
        resultsText = (TextView) findViewById(R.id.results_field);
        promptText = (TextView) findViewById(R.id.prompt_field);
        logo = (ImageView) findViewById(R.id.imageView);
        pulse_ring = (ImageView) findViewById(R.id.imageView2);
        voiceAnim = new AnimationUtils().loadAnimation(this, R.anim.grow_then_fade);
        start_sound = MediaPlayer.create(this, R.raw.start_sound);
        success_sound = MediaPlayer.create(this, R.raw.correct_sound);
        error_sound = MediaPlayer.create(this, R.raw.wrong_sound);
        mProgress = (SliderView) findViewById(R.id.indeterm_slider);
        mProgress.startIndeterminate();
    }

    private void startListening() {
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

        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER && !listener.isListening()) {
            start_sound.start();
            pulse_ring.startAnimation(voiceAnim);
            resultsText.setText("");
            promptText.setText("Listening");
            logo.setImageResource(R.drawable.augmate_logo_blue);
            startListening();
        }
        super.onKeyDown(keycode, event);
        return true;
    }

    final Runnable showLoadingBar = new Runnable()
    {
        public void run()
        {
            if(!listener.hasResults()) mProgress.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onPartial(ArrayList<String> results) {
        resultsText.setText(TextUtils.join(", ", results));
    }

    @Override
    public void onResults(ArrayList<String> results){
        success_sound.start();
        pulse_ring.clearAnimation();
        logo.setImageResource(R.drawable.augmate_logo);
        mProgress.setVisibility(View.INVISIBLE);
        resultsText.setText(TextUtils.join(", ", results));
        promptText.setText("Ready");
    }

    @Override
    public void onEnd() {
        Handler h = new Handler();
       /* Added a tenth second delay for displaying loading bar. If the connection is strong, there is not need to have the bar show up*/
        h.postDelayed(showLoadingBar, 100);
    }

    @Override
    public void onError(int error) {
        error_sound.start();
        pulse_ring.clearAnimation();
        logo.setImageResource(R.drawable.augmate_logo_red);
        mProgress.setVisibility(View.INVISIBLE);
        promptText.setText("Error " + error + "\nTry again?");
        switch (error) {
            case 1:
                resultsText.setText("*No network connection available*");
                break;
            case 2:
                resultsText.setText("*No network connection available*");
                break;
            case 3:
                resultsText.setText("*Audio Recording error*");
                break;
            case 4:
                resultsText.setText("*Server error*");
                break;
            case 5:
                resultsText.setText("*Client error*");
                break;
            case 6:
                resultsText.setText("*I didn't catch that.\n Please try again.*");
                break;
            case 7:
                resultsText.setText("*No recognition result matched. Please try again.*");
                break;
            case 8:
                resultsText.setText("*Speech recognizer is busy. Please wait then try again.*");
                break;
            case 9:
                resultsText.setText("*Insufficient permissions. You do not have access to this device's audio recorder.*");
                break;
            default:
                promptText.setText("Error " + error + "\nStandby");
        }
    }

    @Override
    public void onPause(){
        start_sound.release();
        error_sound.release();
        success_sound.release();
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
