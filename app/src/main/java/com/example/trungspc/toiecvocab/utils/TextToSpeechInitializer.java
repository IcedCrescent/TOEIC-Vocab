package com.example.trungspc.toiecvocab.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechInitializer{

    private Context context;
    private static TextToSpeech textToSpeech;
    public TextToSpeechIniListener textToSpeechIniListener;
    private Locale locale;

    public TextToSpeechInitializer(Context context , Locale locale , TextToSpeechIniListener textToSpeechIniListener) {
        this.context = context;
        if(textToSpeechIniListener != null) {
            this.textToSpeechIniListener = textToSpeechIniListener;
        }
        this.locale = locale;
        initialize();
    }


    private void initialize() {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(locale); //TODO: Check if locale is available before setting.
                    textToSpeechIniListener.onSuccess(textToSpeech);
                    Log.e("TTS","TextToSpeechInitializeSuccess");
                }else{
                    textToSpeechIniListener.onFailure(textToSpeech);
                    Log.e("TTS","TextToSpeechInitializeError");
                }
            }
        });
    }
    public interface TextToSpeechIniListener {

        public void onSuccess(TextToSpeech tts);

        public void onFailure(TextToSpeech tts);
    }
}

