package com.example.michaelanderjaska.tott;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;




public class LiveCardService extends Service {


    //app state (ghetto version)

    int state = 0;

    public void setState(int s) {
        Log.d("Debug", "state:" + Integer.toString(s));
        state = s;
        mHandler.post(mUpdateLiveCardRunnable);
    }
    public int getState() {return state;}

    public void forceUpdate(){
        mHandler.post(mUpdateLiveCardRunnable);
    }




    private static final String LIVE_CARD_TAG = "ListenerService";

    private LiveCard mLiveCard;
    private RemoteViews mLiveCardView;

    private final UpdateLiveCardRunnable mUpdateLiveCardRunnable = new UpdateLiveCardRunnable();


    private final Handler mHandler = new Handler();

    //private static final long DELAY_MILLIS = 30000;


    private final IBinder mBinder = new ListenerBinder();

    public class ListenerBinder extends Binder {
        LiveCardService getService() {
            return LiveCardService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.



    SpeechVar friend = new SpeechVar();
    SpeechVar recc = new SpeechVar();
    SpeechVar user = new SpeechVar();




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            mLiveCardView = new RemoteViews(getPackageName(), R.layout.live_card);
            mLiveCard.setViews(mLiveCardView);

            // Display the options menu when the live card is tapped.
            Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));


            mLiveCard.publish(PublishMode.REVEAL);
            //android.os.Debug.waitForDebugger();

            mHandler.post(mUpdateLiveCardRunnable);
        } else {
            mLiveCard.navigate();
        }
        return START_STICKY;
    }

    //calls menu from speech places
    public void callMenu(){
        Log.d("Debug", "callMenu");
        Intent menuIntent = new Intent(this, LiveCardMenuActivity.class);
        menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(menuIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();




        //listeners
        recc.setListener(new SpeechVar.ChangeListener() {
            @Override
            public void onChange() {
                Log.d("debug", "recc changed");
                if (state == 0)
                    setState(1);
            }
        });

        friend.setListener(new SpeechVar.ChangeListener() {
            @Override
            public void onChange() {
                Log.d("debug", "friend changed");

                mUpdateLiveCardRunnable.sendSpeech(friend.getSpeech(), false);
            }

        });

        user.setListener(new SpeechVar.ChangeListener() {
            @Override
            public void onChange() {
                Log.d("debug", "user changed");

                mUpdateLiveCardRunnable.sendSpeech(user.getSpeech(), true);
                setState(0);
            }

        });
        /*

        load.setListener(new LoadingSingleton.ChangeListener() {
            @Override
            public void onChange() {
                Log.d("debug", "load changes");
                if (state == 0)
                    setState(1);
            }
        });

        */


    }


    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }

        state = 0;
        super.onDestroy();
    }



    private class UpdateLiveCardRunnable implements Runnable{

        private boolean mIsStopped = false;

/*
          Updates the card with a fake score every 30 seconds as a demonstration.
          You also probably want to display something useful in your live card.

          If you are executing a long running task to get data to update a
          live card(e.g, making a web call), do this in another thread or
          AsyncTask.
*/

        URL url;
        HttpURLConnection urlConnection = null;





        //private static final int SPEECH_REQUEST = 0;

        public void run(){
            if(!isStopped()){
                // listen to what is said


                Log.d("debug", "state: " + state);


                if(state == 0){
                    Intent listenIntent = new Intent(getBaseContext(), ListeningActivity.class);
                    listenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    listenIntent.putExtra("state", state);
                    startActivity(listenIntent);





                }

                /*if(state == 1){
                    //between api call and result

                    load = new LoadingSingleton();
                    //load is at state 1

                    //calling activity
                    Intent loadIntent = new Intent(getBaseContext(), LoadingBar.class);
                    loadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loadIntent);



                }*/

                if(state == 1){
                    mLiveCardView.setTextViewText(R.id.text3, recc.getSpeech());
                    mLiveCard.setViews(mLiveCardView);

                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            setState(2);

                        }
                    }, 2500);

                }





                //add the response button, arrow thing
                //how to make a countdown?

                if(state == 2){
                    Intent listenIntent = new Intent(getBaseContext(), ListeningActivity.class);
                    listenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    listenIntent.putExtra("state", state);
                    startActivity(listenIntent);

                    //sendSpeech(user.getSpeech(), true);


                }

                if(state == -1){
                   setStop(true);
                }




                /*
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening to your friend...");
                //startActivityForResult(intent, SPEECH_REQUEST);
                PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
                */


                //mHandler.postDelayed(mUpdateLiveCardRunnable, DELAY_MILLIS);

            }
        }




        public void updateReccomendation(String r){
            recc.setSpeech(r);

        }

        public boolean isStopped() {
            return mIsStopped;
        }

        public void setStop(boolean isStopped) {
            this.mIsStopped = isStopped;
        }

        public void sendSpeech(final String text, final boolean isUser){
            //it's own service? prolly not

            //updateReccomendation("\"Hi I'm Michael\"");

             //loading state

            Thread thread = new Thread(new Runnable(){

                @Override
                public void run(){
                    String url = "https://tipofthetongue.herokuapp.com/speech";
                    RequestQueue queue = MySingleton.getInstance(getBaseContext()).getRequestQueue();



                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("speech", text);
                        jsonBody.put("isUser", Boolean.toString(isUser));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String requestBody = jsonBody.toString();

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                            url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("Debug", response.toString());

                                if(!isUser) updateReccomendation(response.getString("recc"));


                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    },

                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("That didn't work!");
                                    Log.d("Error", error.toString());
                                }
                            })
                    {
                        @Override
                        public byte[] getBody() {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                        requestBody, "utf-8");
                                return null;
                            }
                        }
                    };

                    queue.add(jsonObjReq);

                 }
                });
                    thread.start();
            }


        }
    }






