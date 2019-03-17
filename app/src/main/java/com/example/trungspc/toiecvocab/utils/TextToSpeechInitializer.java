package com.example.trungspc.toiecvocab.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechInitializer {

    private Context context;
    private static TextToSpeech talk;
    private TextToSpeechIniListener textToSpeechIniListener;
    private Locale locale;

    public TextToSpeechInitializer(Context context, Locale locale, TextToSpeechIniListener textToSpeechIniListener) {
        this.context = context;
        if (textToSpeechIniListener != null) {
            this.textToSpeechIniListener = textToSpeechIniListener;
        }
        this.locale = locale;
        initialize();
    }


    private void initialize() {
        talk = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    talk.setLanguage(locale); //TODO: Check if locale is available before setting.
                    textToSpeechIniListener.onSuccess(talk);
                } else {
                    textToSpeechIniListener.onFailure(talk);
                    Log.e("TTS", "TextToSpeechInitializeError");
                }
            }
        });
    }

    public interface TextToSpeechIniListener {

        void onSuccess(TextToSpeech tts);

        void onFailure(TextToSpeech tts);
    }
}

