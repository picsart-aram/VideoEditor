package ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.generatingmain.AbsolutePathActivity;
import com.javacodegeeks.androidvideocaptureexample.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import controller.CameraController;
import controller.CaptureController;


public class CollageMainActivity extends Activity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/picsartVideo");

    private Camera camera;
    private CameraPreview cameraPreview;
    private MediaRecorder mediaRecorder;
    private Button capture, switchCamera1, switchCamera2;
    private ImageButton cameraLeft;
    private ImageButton cameraRight;
    private Context context;
    private LinearLayout firstCameraPreviewLayout;
    private LinearLayout secondCameraPreviewLayout;
    private TextView timeTextView;
    private Button playButton;
    private SeekBar seekbar;
    private TextView musicTimeText;
    private TextView musicNameText;
    private Button pickMusicbtn;

//    private boolean cameraFront = false;
//    private int firstSecondCameraIndex = 1;
//    private Thread myThread = null;
//
//    private int currentCapturedTime;
    private int capturedTime;
//    private int cupturedCount = 0;

    FrameLayout cameraPreviewFrame1;
    FrameLayout cameraPreviewFrame2;

    private static String musicPath = null;
    String newMusicPath;

    MediaController media_Controller;
    CameraController cameraController;
    CaptureController captureController;

    boolean frontCameraOpened = false;

    int width;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d("ASHOTLOG", "onCreate called");

        Utils.initImageLoader(CollageMainActivity.this);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        Utils.createDir(root + "/picsartVideo");
        Utils.createDir(root + "/picsartVideo/sourcefirst/");
        Utils.createDir(root + "/picsartVideo/sourcesecond/");
        Utils.createDir(root + "/picsartVideo/mergedframes/");

        context = this;

        init();

    }

    @Override
    public void onResume() {
        Log.d("ASHOTLOG", "onResume called");
        super.onResume();
        Log.d("ASHOTLOG", "onResume called");
        if (!CameraController.hasCamera(context)) {
            Toast toast = Toast.makeText(context, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (camera == null) {
            // if the front facing camera does not exist
            if (cameraController.findFrontFacingCamera() < 0) {

                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera1.setVisibility(View.GONE);
                switchCamera2.setVisibility(View.GONE);
            }
            camera = Camera.open(cameraController.findBackFacingCamera());
            cameraPreview.refreshCamera(camera);
            captureController.setmCamera(camera);
        }

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        camera = cameraController.releaseCamera(camera);
    }

    public void init() {

        cameraController = new CameraController();
        captureController = new CaptureController(this, new OnCaptureEndListener() {
            @Override
            public void onVideoCaptureEnd(int index, VideoView video) {
                switch (index){
                    case 1:
                        cameraRight.setVisibility(View.GONE);
                        firstCameraPreviewLayout.removeView(cameraPreview);
                        secondCameraPreviewLayout.addView(cameraPreview);
                        firstCameraPreviewLayout.addView(video);
                        video.start();
                        break;
                    case 2:
                        cameraLeft.setVisibility(View.GONE);
                        secondCameraPreviewLayout.removeView(cameraPreview);
                        firstCameraPreviewLayout.addView(cameraPreview);
                        secondCameraPreviewLayout.addView(video);
                        video.start();
                }
            }

            @Override
            public void onCaptureEnd() {

                capture.setText("Capture");
                showRenderSaveDialog();
            }
        });
        capturedTime = 0;
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        int height = display.getHeight();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width / 2, (8 * width / 9));

        cameraPreviewFrame1 = (FrameLayout) findViewById(R.id.preview_frame1);
        cameraPreviewFrame2 = (FrameLayout) findViewById(R.id.preview_frame2);
        firstCameraPreviewLayout = (LinearLayout) findViewById(R.id.camera_preview);
        secondCameraPreviewLayout = (LinearLayout) findViewById(R.id.camera_preview2);
        timeTextView = (TextView) findViewById(R.id.text_view);
        cameraLeft = (ImageButton) findViewById(R.id.vid_left);
        cameraRight = (ImageButton) findViewById(R.id.vid_right);
        playButton = (Button) findViewById(R.id.playButtn);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        pickMusicbtn = (Button) findViewById(R.id.pickMusicbtn);
        musicNameText = (TextView) findViewById(R.id.musicNameText);
        musicTimeText = (TextView) findViewById(R.id.musicTimeText);

        firstCameraPreviewLayout.setLayoutParams(layoutParams);
        secondCameraPreviewLayout.setLayoutParams(layoutParams);

        cameraPreview = new CameraPreview(context, camera);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captureListener);

        switchCamera1 = (Button) findViewById(R.id.switch_camera1);
        switchCamera2 = (Button) findViewById(R.id.switch_camera2);
        switchCamera1.setOnClickListener(switchCameraListener);
        switchCamera2.setOnClickListener(switchCameraListener);

        firstCameraPreviewLayout.addView(cameraPreview);
        secondCameraPreviewLayout.removeAllViews();

        cameraLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                secondCameraPreviewLayout.removeView(cameraPreview);
                firstCameraPreviewLayout.addView(cameraPreview);
                captureController.setIndex(1);
                cameraRight.setVisibility(View.VISIBLE);
                switchCamera2.setVisibility(View.GONE);
                switchCamera1.setVisibility(View.VISIBLE);
            }
        });

        final VideoView video = new VideoView(getApplicationContext());
        media_Controller = new MediaController(this);
        video.setMinimumWidth(50);
        video.setMinimumHeight(50);
        video.setMediaController(media_Controller);
        media_Controller.setVisibility(View.GONE);
        video.setVideoPath(myDir + "/myvideo1.mp4");

        cameraRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                firstCameraPreviewLayout.removeView(cameraPreview);
                //firstCameraPreviewLayout.addView(video);
                //video.start();
                secondCameraPreviewLayout.addView(cameraPreview);
                captureController.setIndex(2);
                cameraLeft.setVisibility(View.VISIBLE);
                switchCamera1.setVisibility(View.GONE);
                switchCamera2.setVisibility(View.VISIBLE);
            }
        });

    }

    public void switchCamera() {
        camera = cameraController.switchCamera();
        cameraPreview.refreshCamera(camera);
    }

//    private int findFrontFacingCamera() {
//        int cameraId = -1;
//        // Search for the front facing camera
//        int numberOfCameras = Camera.getNumberOfCameras();
//        for (int i = 0; i < numberOfCameras; i++) {
//            CameraInfo info = new CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
//                cameraId = i;
//                cameraFront = true;
//                frontCameraOpened = true;
//                break;
//            }
//        }
//        return cameraId;
//    }

//    private int findBackFacingCamera() {
//        int cameraId = -1;
//        // Search for the back facing camera
//        // get the number of cameras
//        int numberOfCameras = Camera.getNumberOfCameras();
//        // for every camera check
//        for (int i = 0; i < numberOfCameras; i++) {
//            CameraInfo info = new CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
//                cameraId = i;
//                cameraFront = false;
//                break;
//            }
//        }
//        return cameraId;
//    }
//
//
//    private void releaseMediaRecorder() {
//        if (mediaRecorder != null) {
//            mediaRecorder.reset(); // clear recorder configuration
//            mediaRecorder.release(); // release the recorder object
//            mediaRecorder = null;
//            camera.lock(); // lock camera for later use
//        }
//    }

//    private boolean prepareMediaRecorder() {
//
//        File file = new File(myDir, "myvideo" + firstSecondCameraIndex + ".mp4");
//
//        mediaRecorder = new MediaRecorder();
//
//        camera.unlock();
//        mediaRecorder.setCamera(camera);
//
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//
//        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
//
//        if (cameraFront) {
//            mediaRecorder.setOrientationHint(270);
//        } else {
//            mediaRecorder.setOrientationHint(90);
//            // }
//
//            mediaRecorder.setOutputFile(file.getAbsolutePath());
//            mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
//            mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M
//
//            try {
//                mediaRecorder.prepare();
//            } catch (IllegalStateException e) {
//                releaseMediaRecorder();
//                return false;
//            } catch (IOException e) {
//                releaseMediaRecorder();
//                return false;
//            }
//        }
//        return true;
//
//    }

//    public void chooseCamera() {
//        // if the camera preview is the front
//        if (cameraFront) {
//            int cameraId = findBackFacingCamera();
//            if (cameraId >= 0) {
//                // open the backFacingCamera
//                // set a picture callback
//                // refresh the preview
//
//                cameraPreview.setOrientation(90);
//                camera = Camera.open(cameraId);
//                // mPicture = getPictureCallback();
//                cameraPreview.refreshCamera(camera);
//            }
//        } else {
//            int cameraId = findFrontFacingCamera();
//            if (cameraId >= 0) {
//                // open the backFacingCamera
//                // set a picture callback
//                // refresh the preview
//
//                cameraPreview.setOrientation(90);
//                camera = Camera.open(cameraId);
//                // mPicture = getPictureCallback();
//                cameraPreview.refreshCamera(camera);
//            }
//        }
//    }

//    private void releaseCamera() {
//        // stop and release camera
//        if (camera != null) {
//            camera.release();
//            camera = null;
//        }
//    }


    OnClickListener switchCameraListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa
                    camera = cameraController.releaseCamera(camera);
                    switchCamera();
                } else {
                    Toast toast = Toast.makeText(context, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        }
    };


    boolean recording = false;

    OnClickListener captureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                captureController.stopCapturing();
                ((TextView) v).setText("Capture");
                recording = false;

            } else {
                captureController.startCapture();
                ((TextView) v).setText("Stop");

                recording = true;
            }
        }
    };

//    OnClickListener captrureListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (recording) {
//                myThread.interrupt();
//                capturedTime = currentCapturedTime;
//
//                // stop recording and release camera
//                mediaRecorder.stop(); // stop the recording
//                switchCamera1.setClickable(true);
//                switchCamera2.setClickable(true);
//
//                if (cupturedCount == 1) {
//
//                    AlertDialog.Builder adb = new AlertDialog.Builder(v.getRootView().getContext());
//                    adb.setTitle("Render/Save");
//                    adb.setMessage("Render/Save or Capture Again");
//                    adb.setIcon(android.R.drawable.ic_dialog_info);
//                    adb.setPositiveButton("Render/Save", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                            final ProgressDialog progressDialog = new ProgressDialog(context);
//                            progressDialog.setMessage("Please wait while rendering...  0/3");
//                            progressDialog.setCancelable(false);
//                            progressDialog.show();
//                            Render render = new Render(CollageMainActivity.this);
//                            render.decodeVideos();
//                            render.setOnRenderFinishedListener(new Render.OnRenderFinishedListener() {
//                                @Override
//                                public void onFinish(int progress) {
//                                    if (progress == 1)
//                                        progressDialog.setMessage("Please wait while rendering...  1/3");
//                                    if (progress == 2)
//                                        progressDialog.setMessage("Please wait while rendering...  2/3");
//                                    if (progress == 3) {
//                                        progressDialog.setMessage("Please wait while rendering...  3/3");
//                                        progressDialog.dismiss();
//                                        firstCameraPreviewLayout.removeAllViews();
//                                        secondCameraPreviewLayout.removeAllViews();
//                                        cameraRight.setVisibility(View.VISIBLE);
//                                        firstCameraPreviewLayout.addView(cameraPreview);
//                                        Utils.clearDir(myDir);
//                                        cupturedCount = 0;
//                                        firstSecondCameraIndex = 1;
//                                        capturedTime = 0;
//                                        switchCamera1.setVisibility(View.VISIBLE);
//                                        switchCamera2.setVisibility(View.GONE);
//
//                                        recording = false;
//                                        /*Intent intent = new Intent(CollageMainActivity.this, VideoCollageActivity.class);
//                                        intent.putExtra("video_path", root + "/vid.mp4");
//                                        startActivity(intent);*/
//                                        Intent tostart = new Intent(Intent.ACTION_VIEW);
//                                        tostart.setDataAndType(Uri.parse(root + "/vid.mp4"), "video/*");
//                                        startActivity(tostart);
//                                    }
//                                }
//                            });
//                        }
//                    });
//                    adb.setNegativeButton("Capture Again", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            firstCameraPreviewLayout.removeAllViews();
//                            secondCameraPreviewLayout.removeAllViews();
//                            cameraRight.setVisibility(View.VISIBLE);
//                            firstCameraPreviewLayout.addView(cameraPreview);
//                            Utils.clearDir(myDir);
//                            cupturedCount = 0;
//                            firstSecondCameraIndex = 1;
//                            capturedTime = 0;
//                            recording = false;
//                            switchCamera1.setVisibility(View.VISIBLE);
//                            switchCamera2.setVisibility(View.GONE);
//                        }
//                    });
//                    adb.setCancelable(false);
//                    adb.show();
//
//                }
//
//                if (cupturedCount < 2) {
//                    final VideoView video = new VideoView(CollageMainActivity.this);
//                    video.setLayoutParams(new LinearLayout.LayoutParams(width / 2, (width * 8) / 9));
//                    media_Controller = new MediaController(CollageMainActivity.this);
//                    ///video.setMinimumWidth(width);
//                    //video.setMinimumHeight(height);
//                    video.setMediaController(media_Controller);
//                    media_Controller.setVisibility(View.GONE);
//
//                    if (firstSecondCameraIndex == 1 && new File(myDir + "/myvideo1.mp4").exists()) {
//                        cameraRight.setVisibility(View.GONE);
//                        video.setVideoPath(myDir + "/myvideo1.mp4");
//                        firstCameraPreviewLayout.removeView(cameraPreview);
//                        secondCameraPreviewLayout.addView(cameraPreview);
//                        firstCameraPreviewLayout.addView(video);
//                        switchCamera1.setVisibility(View.GONE);
//                        switchCamera2.setVisibility(View.VISIBLE);
//                        firstSecondCameraIndex = 2;
//                        video.start();
//
//                        //cameraLeft.setVisibility(View.VISIBLE);
//                    }
//                    if (firstSecondCameraIndex == 2 && new File(myDir + "/myvideo2.mp4").exists()) {
//                        cameraLeft.setVisibility(View.GONE);
//                        video.setVideoPath(myDir + "/myvideo2.mp4");
//                        secondCameraPreviewLayout.removeView(cameraPreview);
//                        firstCameraPreviewLayout.addView(cameraPreview);
//                        secondCameraPreviewLayout.addView(video);
//                        firstSecondCameraIndex = 1;
//                        switchCamera2.setVisibility(View.GONE);
//                        switchCamera1.setVisibility(View.VISIBLE);
//                        video.start();
//
//                        //cameraRight.setVisibility(View.VISIBLE);
//                    }
//                    cupturedCount++;
//                } else {
//
//                    Toast.makeText(CollageMainActivity.this, "jajajajaj", Toast.LENGTH_LONG).show();
//                }
//                releaseMediaRecorder(); // release the MediaRecorder object
//                Toast.makeText(CollageMainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
//                ((TextView) v).setText("Capture");
//                recording = false;
//
//            } else {
//                if (!prepareMediaRecorder()) {
//                    Toast.makeText(CollageMainActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//
//                currentCapturedTime = 0;
//
//                Runnable myRunnableThread = new CountDownRunner();
//                myThread = new Thread(myRunnableThread);
//                myThread.start();
//
//                // work on UiThread for better performance
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        // If there are stories, add them to the table
//
//                        try {
//                            mediaRecorder.start();
//                            switchCamera1.setClickable(false);
//                            switchCamera2.setClickable(false);
//
//
//                        } catch (final Exception ex) {
//                            // Log.i("---","Exception in thread");
//                        }
//                    }
//                });
//                ((TextView) v).setText("Stop");
//
//                recording = true;
//            }
//        }
//    };
//
//    public void doWork() {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                try {
//                    timeTextView.setText("Time  " + capturedTime / 10.0 + "/" + currentCapturedTime / 10.0);
//                    if (cupturedCount >= 1)
//                        if (currentCapturedTime == capturedTime) {
//                            captrureListener.onClick(capture);
//                        }
//                    currentCapturedTime++;
//
//
//                } catch (Exception e) {
//                }
//            }
//        });
//    }
//
//    class CountDownRunner implements Runnable {
//        // @Override
//        public void run() {
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    doWork();
//                    Thread.sleep(100); // Pause of 1 Second
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                } catch (Exception e) {
//                }
//            }
//        }
//    }

    public void onPickMusicClick(View v) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Complate action using"), 5);

    }

    public void onDeleteMusicPathClick(View v) {

        musicPath = null;
        musicNameText.setText("No music Selected");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5 && data != null) {

            musicPath = AbsolutePathActivity.getPath(CollageMainActivity.this, data.getData());
            newMusicPath = musicPath;
            if (musicPath != null)
                musicNameText.setText(musicPath);

        }
    }

    public interface OnCaptureEndListener{
        public void onVideoCaptureEnd(int index, VideoView video);
        public void onCaptureEnd();
    }

    public void showRenderSaveDialog(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Render/Save");
        adb.setMessage("Render/Save or Capture Again");
        adb.setIcon(android.R.drawable.ic_dialog_info);
        adb.setPositiveButton("Render/Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait while rendering...  0/3");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Render render = new Render(CollageMainActivity.this);
                render.decodeVideos();
                render.setOnRenderFinishedListener(new Render.OnRenderFinishedListener() {
                    @Override
                    public void onFinish(int progress) {
                        if (progress == 1)
                            progressDialog.setMessage("Please wait while rendering...  1/3");
                        if (progress == 2)
                            progressDialog.setMessage("Please wait while rendering...  2/3");
                        if (progress == 3) {
                            progressDialog.setMessage("Please wait while rendering...  3/3");
                            progressDialog.dismiss();
                            firstCameraPreviewLayout.removeAllViews();
                            secondCameraPreviewLayout.removeAllViews();
                            cameraRight.setVisibility(View.VISIBLE);
                            firstCameraPreviewLayout.addView(cameraPreview);
                            Utils.clearDir(myDir);
                            Utils.createDir(root + "/picsartVideo/sourcefirst/");
                            Utils.createDir(root + "/picsartVideo/sourcesecond/");
                            Utils.createDir(root + "/picsartVideo/mergedframes/");
                            captureController.restore();
                            switchCamera1.setVisibility(View.VISIBLE);
                            switchCamera2.setVisibility(View.GONE);

                            recording = false;
                                        /*Intent intent = new Intent(CollageMainActivity.this, VideoCollageActivity.class);
                                        intent.putExtra("video_path", root + "/vid.mp4");
                                        startActivity(intent);*/
                            Intent tostart = new Intent(Intent.ACTION_VIEW);
                            tostart.setDataAndType(Uri.parse(root + "/vid.mp4"), "video/*");
                            startActivity(tostart);
                        }
                    }
                });
            }
        });
        adb.setNegativeButton("Capture Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firstCameraPreviewLayout.removeAllViews();
                secondCameraPreviewLayout.removeAllViews();
                cameraRight.setVisibility(View.VISIBLE);
                firstCameraPreviewLayout.addView(cameraPreview);
                Utils.clearDir(myDir);
                Utils.createDir(root + "/picsartVideo/sourcefirst/");
                Utils.createDir(root + "/picsartVideo/sourcesecond/");
                Utils.createDir(root + "/picsartVideo/mergedframes/");
                captureController.restore();
                recording = false;
                switchCamera1.setVisibility(View.VISIBLE);
                switchCamera2.setVisibility(View.GONE);
            }
        });
        adb.setCancelable(false);
        adb.show();
    }

}
