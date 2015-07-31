package videoeditor.picsart.com.videoeditor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialin.android.encoder.Encoder;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import videoeditor.picsart.com.videoeditor.clipart.ClipartActivity;


public class EditVideoActivity extends ActionBarActivity implements SeekBarWithTwoThumb.SeekBarChangeListener {

    private static final int REQUEST_ADD_TEXT = 300;
    private static final int REQUEST_ADD_CLIPART = 301;

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/" + Util.VIDEO_FILES_DIR);

    private VideoView videoView;
    private ProgressDialog progressDialog;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private Adapter adapter;
    private boolean isPlaying = false;
    private String videoPath;

    private Intent intent;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> previewArrayList = new ArrayList<>();
    public static Context context;

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
            getSupportActionBar().setTitle("Illusion");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
        context = this;
        init();
    }

    public void init() {

        videoView = (VideoView) findViewById(R.id.video_view);
        recyclerView = (RecyclerView) findViewById(R.id.rec_view);

        final Button playPauseButton = (Button) findViewById(R.id.play_pause_button);
        seekBarWithTwoThumb = (SeekBarWithTwoThumb) findViewById(R.id.seek_bar_with_two_thumb);
        seekBarWithTwoThumb.setSeekBarChangeListener(this);

        progressDialog = new ProgressDialog(EditVideoActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.addItemDecoration(new SpacesItemDecoration(1));

        intent = getIntent();
        videoPath = intent.getStringExtra("video_path");
        playPauseButton.setBackgroundResource(android.R.drawable.ic_media_pause);

        final VideoDecoder videoDecoder = new VideoDecoder(MainActivity.getContext(), videoPath, VideoDecoder.FrameSize.NORMAL, myDir.toString());

        frameWidth = PhotoUtils.checkFrameWidth(videoPath, VideoDecoder.FrameSize.NORMAL);
        frameHeight = PhotoUtils.checkFrameHeight(videoPath, VideoDecoder.FrameSize.NORMAL);
        frameOrientation = PhotoUtils.checkFrameOrientation(videoPath);
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
                File[] files = myDir.listFiles();
                int x = (4 * files.length) / 25;
                for (int i = 0; i < files.length; i++) {
                    arrayList.add(files[i].getAbsolutePath());
                    if (i % x == 0) {
                        previewArrayList.add(files[i].getAbsolutePath());
                    }
                }

                adapter = new Adapter(previewArrayList, EditVideoActivity.this);
                recyclerView.setAdapter(adapter);
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

        findViewById(R.id.gray_scale_button).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.encode_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AsyncTask<Void, Integer, Void> doActionTask = new AsyncTask<Void, Integer, Void>() {
                    Encoder encoder;
                    ProgressDialog progressDialog1;

                    @Override
                    protected Void doInBackground(Void... params) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            //Bitmap bmp = Bitmap.createBitmap(360, 640, Bitmap.Config.ARGB_8888);
                            SharedPreferences sharedPreferences = context.getSharedPreferences("pics_art_video_editor", Context.MODE_PRIVATE);
                            int bufferSize = sharedPreferences.getInt("buffer_size", 0);
                            ByteBuffer buffer = PhotoUtils.readBufferFromFile(arrayList.get(i), bufferSize);
                            Bitmap bmp = PhotoUtils.fromBufferToBitmap(frameWidth, frameHeight, frameOrientation, buffer);
                            //buffer.rewind();
                            //bmp.copyPixelsFromBuffer(buffer);
                            //Matrix m = new Matrix();
                            //m.postRotate(180);
                            //m.preScale(-1, 1);
                            //bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
                            encoder.addFrame(bmp, 50);
                            onProgressUpdate(i, arrayList.size());
                            //encoder.addFrame(ImageLoader.getInstance().loadImageSync("file://" + arrayList.get(i)), 50);
                            //onProgressUpdate(i, arrayList.size());
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
                        //encoder.init(ImageLoader.getInstance().loadImageSync("file://" + arrayList.get(0)).getWidth(), ImageLoader.getInstance().loadImageSync("file://" + arrayList.get(0)).getHeight(), 15, null);
                        encoder.startVideoGeneration(new File(root + "/vid.mp4"));
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
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
                adapter.notifyDataSetChanged();
            }

            if (requestCode == REQUEST_ADD_CLIPART) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void SeekBarValueChanged(int Thumb1Value, int Thumb2Value) {
        //Log.d("MyLog", "thumb1 : " + Thumb1Value * framesCount / 100 + " thumb2 " + Thumb2Value * framesCount / 100);
    }

}
