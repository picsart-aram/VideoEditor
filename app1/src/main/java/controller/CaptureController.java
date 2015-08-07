package controller;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.javacodegeeks.androidvideocaptureexample.R;

import java.io.File;
import java.io.IOException;

import ui.CollageMainActivity;

/**
 * Created by Ashot on 7/10/15.
 */
public class CaptureController {

    Activity activity;

    Thread myThread = null;
    boolean isCaptured = false;
    private MediaRecorder mediaRecorder;
    private int currentCapturedTime;
    private int capturedTime;
    private int capturedVideosCount;
    private CollageMainActivity.OnCaptureEndListener listener;
    private TextView timeTextView;

    private Camera mCamera;
    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/picsartVideo");
    private int index = 1;
    MediaController media_Controller;

    public CaptureController(Activity activity, CollageMainActivity.OnCaptureEndListener listener) {
        this.activity = activity;
        this.listener = listener;
        capturedVideosCount = 0;
        timeTextView = (TextView) activity.findViewById(R.id.text_view);
    }

    public void startCapture(){
        if (!prepareMediaRecorder()) {
            Toast.makeText(activity, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
            activity.finish();
        }

        currentCapturedTime = 0;

        Runnable myRunnableThread = new CountDownRunner();
        myThread = new Thread(myRunnableThread);
        myThread.start();

        // work on UiThread for better performance
        activity.runOnUiThread(new Runnable() {
            public void run() {
                // If there are stories, add them to the table

                try {
                    mediaRecorder.start();
                    isCaptured = true;


                } catch (final Exception ex) {
                    // Log.i("---","Exception in thread");
                }
            }
        });
    }

    public void stopCapturing(){
        myThread.interrupt();
        capturedTime = currentCapturedTime;

               /* vidLeft.setClickable(false);
                vidRight.setClickable(false);*/
        //Effects2.initConfig(currentCapturedTime);

        // stop recording and release camera
        mediaRecorder.stop(); // stop the recording


        if(capturedVideosCount==1){

            listener.onCaptureEnd();


        }

        if (capturedVideosCount < 2) {
            final VideoView video = new VideoView(activity);
            video.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
            media_Controller = new MediaController(activity);
            ///video.setMinimumWidth(width);
            //video.setMinimumHeight(height);
            video.setMediaController(media_Controller);
            media_Controller.setVisibility(View.GONE);

            if (index == 1 && new File(myDir + "/myvideo1.mp4").exists()) {
                video.setVideoPath(myDir + "/myvideo1.mp4");
                index = 2;
                video.start();
                listener.onVideoCaptureEnd(1, video);

                //vidLeft.setVisibility(View.VISIBLE);
            }
            if (index == 2 && new File(myDir + "/myvideo2.mp4").exists()) {
                video.setVideoPath(myDir + "/myvideo2.mp4");
                index = 1;
                video.start();

                listener.onVideoCaptureEnd(2, video);
                //vidRight.setVisibility(View.VISIBLE);
            }
            capturedVideosCount++;
        } else {

            Toast.makeText(activity, "jajajajaj", Toast.LENGTH_LONG).show();
        }
        releaseMediaRecorder(); // release the MediaRecorder object
        Toast.makeText(activity, "Video captured!", Toast.LENGTH_LONG).show();

    }

    public void doWork() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    timeTextView.setText("Time  " + capturedTime / 10.0 + "/" + currentCapturedTime / 10.0);

                    if (capturedVideosCount == 1)
                        if (currentCapturedTime == capturedTime) {
                            stopCapturing();
                            //Effects2.initConfig(capturedTime / 10.0);
                        }
                    currentCapturedTime++;

                } catch (Exception e) {
                }
            }
        });
    }


    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100); // Pause of 1 Second
                    doWork();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

        //mediaRecorder.setVideoFrameRate(25000);

        mediaRecorder.setOrientationHint(90);

        File file = new File(myDir, "myvideo" + index + ".mp4");
        mediaRecorder.setOutputFile(file.getAbsolutePath());

        mediaRecorder.setMaxDuration(90000); // Set max duration 90 sec.

        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setCapturedVideosCount(int capturedVideosCount) {
        this.capturedVideosCount = capturedVideosCount;
    }

    public void setmCamera(Camera mCamera) {
        this.mCamera = mCamera;
    }

    public void restore(){
        capturedTime = 0;
        capturedVideosCount = 0;
        index = 1;
        timeTextView.setText("Time  0/0");
    }

}
