package com.generatingmain;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Arman on 4/22/15.
 */
public class FfmpegService extends IntentService {


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FfmpegService(String name) {
        super(name);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.


        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
