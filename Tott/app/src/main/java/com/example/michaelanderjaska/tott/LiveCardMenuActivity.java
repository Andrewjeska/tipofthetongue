package com.example.michaelanderjaska.tott;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;


/**
 * A transparent {@link Activity} displaying a "Stop" options menu to remove the {@link LiveCard}.
 */
public class LiveCardMenuActivity extends Activity {

    private final Handler mHandler = new Handler();


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Open the options menu right away.
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.live_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stop:
                // Stop the service which will unpublish the live card.
                post(new Runnable() {

                    @Override
                    public void run() {
                        stopService(new Intent(LiveCardMenuActivity.this, LiveCardService.class));
                    }
                });
                return true;

            case R.id.action_restart:

                post(new Runnable() {

                    @Override
                    public void run() {
                        stopService(new Intent(LiveCardMenuActivity.this, LiveCardService.class));
                        startService(new Intent(LiveCardMenuActivity.this, LiveCardService.class));
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        // Nothing else to do, finish the Activity.
        finish();
    }

    protected void post(Runnable runnable) {
        mHandler.post(runnable);
    }
}

