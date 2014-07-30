package com.augmate.sdk.voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;
import com.augmate.sdk.logger.Log;

import java.util.ArrayList;

public class VoiceCaptorPlaceholder extends Activity implements IAudioDoneCallback {

    SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
    private TextView promptText;
    private TextView resultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_capture);
        resultsText = (TextView) findViewById(R.id.results_field);
        promptText = (TextView) findViewById(R.id.prompt_field);

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
            Toast.makeText(getApplicationContext(),
                    "Listening" , Toast.LENGTH_LONG).show();
            promptText.setText("Listening");
            startListening();
        }
        super.onKeyDown(keycode, event);
        return true;
    }

    @Override
    public void onSuccess(ArrayList<String> results){
        Toast.makeText(getApplicationContext(),
                "Done" , Toast.LENGTH_LONG).show();
        resultsText.append(TextUtils.join(", ", results) + "\n");
        promptText.setText("Listening");
        Log.debug("Much listening. Very success.");
        // TODO: add ArrayList/Dictionary dumper ala Log.dump()
    }

    @Override
    public void onDestroy(){
        if(speechRecognizer!=null)
            speechRecognizer.destroy();
        super.onDestroy();
    }

}
