package com.example.michaelanderjaska.tott;


import com.google.android.glass.touchpad.GestureDetector;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;


import java.util.List;



public class ListeningActivity extends Activity  {

    //this activity listens for speech

    private final Handler mHandler = new Handler();


    //this handles gestures





    LiveCardService mService;
    boolean mBound = false;

    private int state;

    private static final int SPEECH_REQUEST = 0;


    private void displaySpeechRecognizer(String prompt) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        startActivityForResult(intent, SPEECH_REQUEST);
    }

    static int errF = 0;
    static int errU = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Debug", "intent: " + data);

        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK && data != null) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String spokenText = results.get(0);
            Log.d("Debug", spokenText);


            if(state == 0){
                errF = 0;
                mService.friend.setSpeech(spokenText);

            } else if (state == 2){

                errU = 0;
                mService.user.setSpeech(spokenText);
            }


        } else {
            Log.d("error", "Could not parse voice");//or something nicer
            if(state == 0){

                Log.d("Debug", Integer.toString(errF));
                if(errF >= 1) finishAndCallMenu();
                else {
                    errF++;
                    mService.forceUpdate();

                }

            } else if (state == 2){

                if(errU >= 1) finishAndCallMenu();
                else {
                    errU++;
                    mService.setState(0);

                }

            }


        }
        //super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    //private boolean callMenu = false;

    public void finishAndCallMenu(){
        errU = 0;
        errF = 0;
        mService.callMenu();
        finish();
    }



    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);


    }

    final String listenPrompt = "Listening to your friend";
    final String resPrompt = "Responding...";

    protected void onStart() {
        super.onStart();
        // Bind to LiveCardService
        Intent intent = new Intent(getBaseContext(), LiveCardService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        state = getIntent().getIntExtra("state", -1);
        displaySpeechRecognizer(state == 0 ? listenPrompt : resPrompt);


    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    @Override
    protected void onPause() {

        super.onPause();
        /*if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();



        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }


    }



    @Override
    protected void onDestroy(){


        if (state == 2){
            mService.setState(0);

        }

        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LiveCardService.ListenerBinder binder = (LiveCardService.ListenerBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



}
