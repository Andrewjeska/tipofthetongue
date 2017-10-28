package com.example.michaelanderjaska.tott;

/**
 * Created by michaelanderjaska on 3/26/17.
 */

public class SpeechVar {
    private String speech = "";
    private ChangeListener listener;

    public SpeechVar(){
        super();
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String text) {
        this.speech = text;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
