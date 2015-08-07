package videoeditor.picsart.com.videoeditor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialin.android.encoder.Encoder;
import com.socialin.android.photo.imgop.ImageOp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import videoeditor.picsart.com.videoeditor.clipart.ClipartActivity;
import videoeditor.picsart.com.videoeditor.effects.GreenScreenAction;


public class EditVideoActivity extends ActionBarActivity implements SeekBarWithTwoThumb.SeekBarChangeListener {

    private static final int REQUEST_ADD_TEXT = 300;
    private static final int REQUEST_ADD_CLIPART = 301;

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/" + Util.VIDEO_FILES_DIR);

    private VideoView videoView;
    private ProgressDialog progressDialog;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private boolean isPlaying = false;
    private String videoPath;

    private Intent intent;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> previewArrayList = new ArrayList<>();
    public static Context context;

    private int REQUEST_SELECT_BG = 302;
    ImageAdapter imageAdapter;
    LinearLayout framesContainer;
    private SeekBarWithTwoThumb seekBarWithTwoThumb;
    int frameWidth;
    int frameHeight;
    int frameOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        context = this;
        init();
    }

    public void init() {

        videoView = (VideoView) findViewById(R.id.video_view);
        framesContainer= (LinearLayout) findViewById(R.id.frames_container);
        final Button playPauseButton = (Button) findViewById(R.id.play_pause_button);
        seekBarWithTwoThumb = (SeekBarWithTwoThumb) findViewById(R.id.seek_bar_with_two_thumb);
        seekBarWithTwoThumb.setSeekBarChangeListener(this);

        progressDialog = new ProgressDialog(EditVideoActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        intent = getIntent();
        videoPath = intent.getStringExtra("video_path");
        playPauseButton.setBackgroundResource(android.R.drawable.ic_media_pause);

        final VideoDecoder videoDecoder = new VideoDecoder(MainActivity.getContext(), videoPath, VideoDecoder.FrameSize.NORMAL, myDir.toString());

        frameWidth = PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL);
        frameHeight = PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL);
        frameOrientation = PhotoUtils.checkFrameOrientation(videoPath);
        Log.d("gagagagag", frameHeight + "");
        Log.d("gagagagag", frameWidth + "");
        Log.d("gagagagag", frameOrientation + "");


        SharedPreferences sharedPreferences = this.getSharedPreferences("pics_art_video_editor", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("buffer_size", PhotoUtils.checkBufferSize(videoPath, VideoDecoder.FrameSize.NORMAL));
        editor.putInt("frame_width", frameWidth);
        editor.putInt("frame_height", frameHeight);
        editor.putInt("frame_orientation", frameOrientation);
        editor.commit();

        videoDecoder.extractVideoFrames();
        videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
            @Override
            public void onFinish(boolean isDone) {


                int framesCount = 8;
                File[] files = myDir.listFiles();
                int duration= files.length/framesCount;
                for (int i = 0; i <files.length ; i++) {
                    arrayList.add(files[i].getAbsolutePath());
                    if(i%duration==0){
                        previewArrayList.add(files[i].getAbsolutePath());
                    }
                }

                imageAdapter = new ImageAdapter(EditVideoActivity.this, 0);
                imageAdapter.addAll(previewArrayList);
                for (int i = 0; i <previewArrayList.size() ; i++) {
                    framesContainer.addView(imageAdapter.getView(i, null, framesContainer));
                }


                videoView.setVideoPath(intent.getStringExtra("video_path"));
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
                videoView.start();
                isPlaying = true;
                progressDialog.dismiss();
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    videoView.pause();
                    isPlaying = false;
                    playPauseButton.setBackgroundResource(android.R.drawable.ic_media_play);
                } else {
                    videoView.start();
                    isPlaying = true;
                    playPauseButton.setBackgroundResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        findViewById(R.id.effects_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EmbossEffect effect = new EmbossEffect(EditVideoActivity.this);
//                effect.startAction(new File(Environment.getExternalStorageDirectory(), "test_images").getPath());
                Intent intent = new Intent(EditVideoActivity.this, VideoEffectsActivity.class);
                intent.putExtra("image_path", arrayList.get(0));
                intent.putExtra("folder_path", new File(Environment.getExternalStorageDirectory(), "test_images").getPath());
                startActivity(intent);

            }
        });

        findViewById(R.id.add_text_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditVideoActivity.this, TextArtActivity.class);
                intent.putExtra("image_path", arrayList.get(0));
                startActivityForResult(intent, REQUEST_ADD_TEXT);
            }
        });

        findViewById(R.id.add_clipart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditVideoActivity.this, ClipartActivity.class);
                intent.putExtra("image_path", arrayList.get(0));
                startActivityForResult(intent, REQUEST_ADD_CLIPART);
            }
        });

        findViewById(R.id.green_screen_blending_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGreenScreenBlending();
            }
        });

        findViewById(R.id.encode_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncTask<Void, Integer, Void> doActionTask = new AsyncTask<Void, Integer, Void>() {
                    Encoder encoder;
                    ProgressDialog progressDialog1;

                    @Override
                    protected Void doInBackground(Void... params) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            SharedPreferences sharedPreferences = context.getSharedPreferences("pics_art_video_editor", Context.MODE_PRIVATE);
                            int bufferSize = sharedPreferences.getInt("buffer_size", 0);
                            ByteBuffer buffer = PhotoUtils.readBufferFromFile(arrayList.get(i), bufferSize);
                            Bitmap bmp = PhotoUtils.fromBufferToBitmap(frameWidth, frameHeight, buffer);
                            ImageOp.freeNativeBuffer(buffer);
                            encoder.addFrame(bmp, 50);
                            onProgressUpdate(i, arrayList.size());
                        }
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                        ImageLoader.getInstance().clearDiskCache();
                        ImageLoader.getInstance().clearMemoryCache();
                        progressDialog1 = new ProgressDialog(EditVideoActivity.this);
                        progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog1.show();
                        encoder = new Encoder();
                        encoder.init(360, 640, 15, null);
                        encoder.startVideoGeneration(new File(root + "/vid.mp4"));
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        encoder.endVideoGeneration();
                        videoView.setVideoPath(root + "/vid.mp4");
                        videoView.start();
                        progressDialog1.dismiss();
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                        if (progressDialog1 != null) {
                            progressDialog1.setProgress(values[0]);
                            progressDialog1.setMax(values[1]);
                        }
                    }
                };
                doActionTask.execute();

            }
        });

        findViewById(R.id.scale_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_TEXT) {
                imageAdapter.notifyDataSetChanged();
            }

            if (requestCode == REQUEST_ADD_CLIPART) {
                imageAdapter.notifyDataSetChanged();
            }

            if (requestCode == REQUEST_SELECT_BG) {
                handleGalleryResult(data);
            }
        }
    }

    @Override
    public void SeekBarValueChanged(int Thumb1Value, int Thumb2Value) {
//        Log.d("MyLog", "thumb1 : " + Thumb1Value * framesCount / 100 + " thumb2 " + Thumb2Value * framesCount / 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void startGreenScreenBlending() {
        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Choose an image"), REQUEST_SELECT_BG);
    }

    private void handleGalleryResult(Intent data) {
        Uri selectedImage = data.getData();
        String mTmpGalleryPicturePath = getPath(selectedImage);
        if (mTmpGalleryPicturePath != null) {
            blendGreenScreenVideo(mTmpGalleryPicturePath);
        } else {
            try {
                InputStream is = getContentResolver().openInputStream(selectedImage);
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "avatar_pic.jpg");
                    OutputStream output = new FileOutputStream(file);
                    try {
                        try {
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;

                            while ((read = is.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }
                            output.flush();
                        } finally {
                            output.close();
                        }
                        blendGreenScreenVideo(file.getPath());
                    } catch (Exception e) {
                        e.printStackTrace(); // handle exception, define IOException and others
                    }
                } finally {
                    is.close();
                }
//                mImageView.setImageBitmap(BitmapFactory.decodeStream(is));
                mTmpGalleryPicturePath = selectedImage.getPath();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    private String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }

        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor;
        if (Build.VERSION.SDK_INT > 19) {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, sel, new String[]{id}, null);
        } else {
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        } catch (NullPointerException e) {

        }
        return path;
    }

    private void blendGreenScreenVideo(String bgPath) {
        Bitmap bgBitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(bgPath, opts);
        int sampleSize = Math.max(opts.outWidth / frameWidth, opts.outHeight / frameHeight);
        opts.inSampleSize = Math.max(0, sampleSize);
        opts.inJustDecodeBounds = false;
        bgBitmap = BitmapFactory.decodeFile(bgPath, opts);
        GreenScreenAction greenScreenAction = new GreenScreenAction(EditVideoActivity.this, bgBitmap);
        greenScreenAction.startAction(new File(Environment.getExternalStorageDirectory(), "test_images").getPath(), bgBitmap);

    }
}
