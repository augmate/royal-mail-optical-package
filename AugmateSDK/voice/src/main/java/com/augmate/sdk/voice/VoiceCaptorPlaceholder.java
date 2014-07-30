package com.augmate.sdk.voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.augmate.sdk.logger.Log;

import java.util.ArrayList;

public class VoiceCaptorPlaceholder extends Activity implements IAudioDoneCallback {

    SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    private TextView promptText;
    private TextView resultsText;
    private ImageView image;
    private AnimThread animThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_capture);
        resultsText = (TextView) findViewById(R.id.results_field);
        promptText = (TextView) findViewById(R.id.prompt_field);
        image = (ImageView) findViewById(R.id.imageView);
        //animThread = new AnimThread(image,new AnimationUtils().loadAnimation(this, R.anim.pulse));

        Log.debug("Voice Captor Initiated.");
    }

    private void startListening() {
        AugmateRecognitionListener listener = new AugmateRecognitionListener(this);
        speechRecognizer.setRecognitionListener(listener);

        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName())
                .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        speechRecognizer.startListening(recognizerIntent);

        Log.debug("Commencing voice captoring.");
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {

        Log.debug("Caught key-down on key=" + KeyEvent.keyCodeToString(keycode));

        if (keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
            image.startAnimation(new AnimationUtils().loadAnimation(this, R.anim.shake));
            resultsText.setText("");
            Toast.makeText(getApplicationContext(),
                    "Listening" , Toast.LENGTH_LONG).show();
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
        Toast.makeText(getApplicationContext(),
                "Done" , Toast.LENGTH_LONG).show();
        resultsText.setText(TextUtils.join(", ", results) + "\n");
        promptText.setText("Standby");
        Log.debug("Much listening. Very success.");
        // TODO: add ArrayList/Dictionary dumper ala Log.dump()
    }

    @Override
    public void onEnd() {
    }

    @Override
    public void onError(int error) {
        promptText.setText("Error "+ error + "\nStandby");
    }

    @Override
    public void onDestroy(){
        if(speechRecognizer!=null)
            speechRecognizer.destroy();
        super.onDestroy();
    }
}
