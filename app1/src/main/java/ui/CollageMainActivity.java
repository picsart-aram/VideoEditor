package ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class CollageMainActivity extends Activity{

    public static int currentFrameId;
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private Button capture, switchCamera1, switchCamera2;
    private ImageButton vidLeft;
    private ImageButton vidRight;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private int firstSecond = 1;
    private TextView textView;
    private Thread myThread = null;
    private int count = 0;
    private int currentCapturedTime;
    int capturedTime;
    boolean isCaptured = false;
    MediaPlayer mediaPlayer;
    int gag = 0;
    ImageView frameImage;
    FrameLayout cameraPreviewFrame1;
    FrameLayout cameraPreviewFrame2;

    private LinearLayout cameraPreview2;


    OnFrameChangeListener frameChangeListener;

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/picsartVideo");
    static int timer = 0;
    TextView progress;
    Button playButton;
    SeekBar seekbar;
    boolean isplaying = false;
    private TextView musicTimeText;
    private TextView musicNameText;
    private Button pickMusicbtn;
    private long musictotalTime = 0;
    private static String musicPath = null;
    ImageView imagepreview;
    String newMusicPath;
    public static int secondsfromstarting = 0;

    public static int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    MediaController media_Controller;

    boolean frontCameraOpened = false;

    int width;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Utils.initImageLoader(CollageMainActivity.this);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        clearDir(myDir);

        Utils.craeteDir("/picsartVideo/sourcefirst/");
        Utils.craeteDir("/picsartVideo/sourcesecond/");
        Utils.craeteDir("/picsartVideo/mergedframes/");

        myContext = this;

        playButton = (Button) findViewById(R.id.playButtn);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        pickMusicbtn = (Button) findViewById(R.id.pickMusicbtn);
        musicNameText = (TextView) findViewById(R.id.musicNameText);
        musicTimeText = (TextView) findViewById(R.id.musicTimeText);

        myDir.mkdirs();

        initialize();

    }

    public static String getMusicPath() {
        return musicPath;
    }


    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {

                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera1.setVisibility(View.GONE);
                switchCamera2.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            mPreview.refreshCamera(mCamera);
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
        releaseCamera();
    }

    public void initialize() {


        capturedTime = 0;
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();  // deprecated
        int height = display.getHeight();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width / 2, (8 * width / 9));

        currentFrameId = R.drawable.picsintframe1;
        cameraPreviewFrame1 = (FrameLayout) findViewById(R.id.preview_frame1);
        cameraPreviewFrame2 = (FrameLayout) findViewById(R.id.preview_frame2);
        frameImage = (ImageView) findViewById(R.id.frame_image);
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
        cameraPreview2 = (LinearLayout) findViewById(R.id.camera_preview2);
        textView = (TextView) findViewById(R.id.text_view);
        vidLeft = (ImageButton) findViewById(R.id.vid_left);
        vidRight = (ImageButton) findViewById(R.id.vid_right);
        cameraPreview.setLayoutParams(layoutParams);
        cameraPreview2.setLayoutParams(layoutParams);

        mPreview = new CameraPreview(myContext, mCamera);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera1 = (Button) findViewById(R.id.switch_camera1);
        switchCamera2 = (Button) findViewById(R.id.switch_camera2);
        switchCamera1.setOnClickListener(switchCameraListener);
        switchCamera2.setOnClickListener(switchCameraListener);
        switchCamera1.getBackground().mutate().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        switchCamera2.getBackground().mutate().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);


        cameraPreview.addView(mPreview);
        cameraPreview2.removeAllViews();
//        cameraPreview2.addView(mPreview);

        /*renderBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // Starting Sevice here

                VideoRenderService vrs = new VideoRenderService();
                DownloadResultReceiver mReciever = new DownloadResultReceiver(new Handler());
                mReciever.setReceiver(CollageMainActivity.this);
                Intent intent = new Intent(Intent.ACTION_SYNC, null, CollageMainActivity.this, VideoRenderService.class);
                intent.putExtra("receiver", mReciever);
                startService(intent);

                //new FFGraber(CollageMainActivity.this).execute();
            }

        });*/

        vidLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                cameraPreview2.removeView(mPreview);
                cameraPreview.addView(mPreview);
                firstSecond = 1;
                vidRight.setVisibility(View.VISIBLE);
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

        vidRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                cameraPreview.removeView(mPreview);
                //cameraPreview.addView(video);
                //video.start();
                cameraPreview2.addView(mPreview);
                firstSecond = 2;
                vidLeft.setVisibility(View.VISIBLE);
                switchCamera1.setVisibility(View.GONE);
                switchCamera2.setVisibility(View.VISIBLE);
            }
        });
//        cameraPreview.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (firstSecond != 1) {
//                    cameraPreview2.removeView(mPreview);
//                    cameraPreview.addView(mPreview);
//                    firstSecond = 1;
//                }
//            }
//        });
//
//        cameraPreview2.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (firstSecond == 1) {
//                    cameraPreview.removeView(mPreview);
//                    cameraPreview2.addView(mPreview);
//                    firstSecond = 2;
//                }
//
//            }
//        });

//        ArrayList<Bitmap> bitmaps=new ArrayList<>();
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.picsintframe1));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.frame3));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.frame6));
//        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.frame7));

        final ArrayList<Integer> frames = new ArrayList<>();
        frames.add(R.drawable.picsintframe1);
        frames.add(R.drawable.frame3);
        frames.add(R.drawable.frame6);
        frames.add(R.drawable.frame7);

        final ArrayList<Integer> layoutFrames = new ArrayList<>();
        layoutFrames.add(R.layout.f_1);
        layoutFrames.add(R.layout.f_2);
        layoutFrames.add(R.drawable.frame6);
        layoutFrames.add(R.drawable.frame7);

        frameChangeListener = new OnFrameChangeListener() {
            FrameLayout.LayoutParams layoutParams;

            @Override
            public void onFrameChange(int position) {
                switch (position) {
                    case 0:
                        layoutParams = new FrameLayout.LayoutParams(360, 800, Gravity.BOTTOM);
                        cameraPreviewFrame1.setLayoutParams(layoutParams);
                        layoutParams = new FrameLayout.LayoutParams(360, 800, Gravity.RIGHT);
                        cameraPreviewFrame2.setLayoutParams(layoutParams);
                        frameImage.setBackgroundResource(R.drawable.picsintframe1);
                        currentFrameId = frames.get(0);
                        break;
                    case 1:

                        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
                        cameraPreviewFrame1.setLayoutParams(layoutParams);
                        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM);
                        cameraPreviewFrame2.setLayoutParams(layoutParams);
//                        ((FrameLayout.LayoutParams)cameraPreviewFrame1.getLayoutParams()).gravity= Gravity.BOTTOM|Gravity.LEFT;
//                        //((FrameLayout.LayoutParams)cameraPreviewFrame2.getLayoutParams()).gravity= Gravity.RIGHT|Gravity.BOTTOM;
//                       // cameraPreview2.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
//                        cameraPreviewFrame2.setLayoutParams(layoutParams);
                        frameImage.setBackgroundResource(R.drawable.frame3);
                        currentFrameId = frames.get(1);

                        break;
                    case 2:
                        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(10, 10, 0, 0);
                        cameraPreviewFrame1.setLayoutParams(layoutParams);
                        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.BOTTOM);
                        layoutParams.setMargins(0, 0, 0, 15);
                        cameraPreviewFrame2.setLayoutParams(layoutParams);
                        frameImage.setBackgroundResource(R.drawable.frame6);
                        currentFrameId = frames.get(2);

                        break;
                }
                //cameraPreview.removeAllViews();
                //FrameLayout view = (FrameLayout) mInflater.inflate(layoutFrames.get(position), collageFrame, true);

                //collageFrame.setLayoutResource(layoutFrames.get(position));
//                LayoutInflater li = LayoutInflater.from(CollageMainActivity.this);
//                FrameLayout view = (FrameLayout) li.inflate(layoutFrames.get(position), collageFrame, false);
//                view.setOnInflateListener(new ViewStub.OnInflateListener() {
//                    @Override
//                    public void onInflate(ViewStub viewStub, View view) {
//
//                    }
//                });


//                cameraPreview = (LinearLayout) collageFrame.findViewById(R.id.camera_preview);
//                cameraPreview2 = (LinearLayout) collageFrame.findViewById(R.id.camera_preview2);
//
//                vidLeft = (ImageButton) collageFrame.findViewById(R.id.vid_left);
//                vidRight = (ImageButton) collageFrame.findViewById(R.id.vid_right);
//
//
//                cameraPreview.addView(mPreview);
//
//                vidLeft.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        view.setVisibility(View.GONE);
//                        cameraPreview2.removeView(mPreview);
//                        cameraPreview.addView(mPreview);
//                        firstSecond = 1;
//                        vidRight.setVisibility(View.VISIBLE);
//                    }
//                });
//
//                vidRight.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        view.setVisibility(View.GONE);
//                        cameraPreview.removeView(mPreview);
//                        //cameraPreview.addView(video);
//                        //video.start();
//                        cameraPreview2.addView(mPreview);
//                        firstSecond = 2;
//                        vidLeft.setVisibility(View.VISIBLE);
//                    }
//                });

                //collageFrame.addView(view);
            }
        };

    }


    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                frontCameraOpened = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        File file = new File(myDir, "myvideo" + firstSecond + ".mp4");

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));

      /*  if (cameraFront) {
            mediaRecorder.setOrientationHint(270);
        } else {*/
            mediaRecorder.setOrientationHint(90);
       // }

        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setMaxDuration(90000); // Set max duration 90 sec.
        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M
        count++;

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


    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mPreview.setOrientation(90);
                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mPreview.setOrientation(90);
                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    OnClickListener switchCameraListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa


                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        }
    };


    boolean recording = false;

    OnClickListener captrureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                myThread.interrupt();
                capturedTime = currentCapturedTime;

               /* vidLeft.setClickable(false);
                vidRight.setClickable(false);*/


                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording
                switchCamera1.setClickable(true);
                switchCamera2.setClickable(true);

                /*DisplayMetrics dm = new DisplayMetrics();
                CollageMainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                int height = dm.heightPixels;
                int width = dm.widthPixels;*/
                if (gag == 1) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(v.getRootView().getContext());
                    adb.setTitle("Render/Save");
                    adb.setMessage("Render/Save or Capture Again");
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.setPositiveButton("Render/Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            /*FFGraber ffGraber = new FFGraber(CollageMainActivity.this);
                            final ProgressDialog progressDialog=new ProgressDialog(myContext);
                            progressDialog.setMessage("Please wait while rendering...  0/4");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            ffGraber.setListener(new IThreadCompleteListener() {
                                @Override
                                public void notifyOfThreadComplete(int id) {
                                    if (id==1)
                                        progressDialog.setMessage("Please wait while rendering...  1/4");
                                    if (id==2)
                                        progressDialog.setMessage("Please wait while rendering...  2/4");
                                    if (id==3)
                                        progressDialog.setMessage("Please wait while rendering...  3/4");
                                    if (id==4){
                                        progressDialog.setMessage("Please wait while rendering...  4/4");
                                        progressDialog.dismiss();
                                        cameraPreview.removeAllViews();
                                        cameraPreview2.removeAllViews();
                                        vidRight.setVisibility(View.VISIBLE);
                                        cameraPreview.addView(mPreview);
                                        clearDir(myDir);
                                        gag = 0;
                                        firstSecond = 1;
                                        capturedTime = 0;
                                        switchCamera1.setVisibility(View.VISIBLE);
                                        switchCamera2.setVisibility(View.GONE);

                                        recording = false;
                                    }
                                }
                            });
                            ffGraber.execute();*/
                            Render render=new Render(CollageMainActivity.this);
                            render.decodeVideos();
                        }
                    });
                    adb.setNegativeButton("Capture Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cameraPreview.removeAllViews();
                            cameraPreview2.removeAllViews();
                            vidRight.setVisibility(View.VISIBLE);
                            cameraPreview.addView(mPreview);
                            clearDir(myDir);
                            gag = 0;
                            firstSecond = 1;
                            capturedTime = 0;
                            recording = false;
                            switchCamera1.setVisibility(View.VISIBLE);
                            switchCamera2.setVisibility(View.GONE);
                        }
                    });
                    adb.setCancelable(false);
                    adb.show();

                }

                if (gag < 2) {
                    final VideoView video = new VideoView(CollageMainActivity.this);
                    video.setLayoutParams(new LinearLayout.LayoutParams(width / 2, (width * 8) / 9));
                    media_Controller = new MediaController(CollageMainActivity.this);
                    ///video.setMinimumWidth(width);
                    //video.setMinimumHeight(height);
                    video.setMediaController(media_Controller);
                    media_Controller.setVisibility(View.GONE);

                    if (firstSecond == 1 && new File(myDir + "/myvideo1.mp4").exists()) {
                        vidRight.setVisibility(View.GONE);
                        video.setVideoPath(myDir + "/myvideo1.mp4");
                        cameraPreview.removeView(mPreview);
                        cameraPreview2.addView(mPreview);
                        cameraPreview.addView(video);
                        switchCamera1.setVisibility(View.GONE);
                        switchCamera2.setVisibility(View.VISIBLE);
                        firstSecond = 2;
                        video.start();

                        //vidLeft.setVisibility(View.VISIBLE);
                    }
                    if (firstSecond == 2 && new File(myDir + "/myvideo2.mp4").exists()) {
                        vidLeft.setVisibility(View.GONE);
                        video.setVideoPath(myDir + "/myvideo2.mp4");
                        cameraPreview2.removeView(mPreview);
                        cameraPreview.addView(mPreview);
                        cameraPreview2.addView(video);
                        firstSecond = 1;
                        switchCamera2.setVisibility(View.GONE);
                        switchCamera1.setVisibility(View.VISIBLE);
                        video.start();

                        //vidRight.setVisibility(View.VISIBLE);
                    }
                    gag++;
                } else {

                    Toast.makeText(CollageMainActivity.this, "jajajajaj", Toast.LENGTH_LONG).show();
                }
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(CollageMainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                ((TextView) v).setText("Capture");
                recording = false;

            } else {
                if (!prepareMediaRecorder()) {
                    Toast.makeText(CollageMainActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }

                currentCapturedTime = 0;

                Runnable myRunnableThread = new CountDownRunner();
                myThread = new Thread(myRunnableThread);
                myThread.start();

                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table

                        try {
                            mediaRecorder.start();
                            isCaptured = true;
                            switchCamera1.setClickable(false);
                            switchCamera2.setClickable(false);


                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });
                ((TextView) v).setText("Stop");

                recording = true;
            }
        }
    };

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }


    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    textView.setText("Time  " + capturedTime / 10.0 + "/" + currentCapturedTime / 10.0);
                    if (gag >= 1)
                        if (currentCapturedTime == capturedTime) {
                            captrureListener.onClick(capture);
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
                    doWork();
                    Thread.sleep(100); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    //Recieve Result


    public void onPickMusicClick(View v) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Complate action using"), 5);

    }




    public void onDeleteMusicPathClick(View v) {
        musicPath = null;
        musictotalTime = 0;
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


    public static interface OnFrameChangeListener {
        public void onFrameChange(int position);
    }

    public static void clearDir(File dir) {
        try {
            File[] files = dir.listFiles();
            if (files != null)
                for (File f : files) {
                    if (f.isDirectory())
                        clearDir(f);
                    f.delete();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
