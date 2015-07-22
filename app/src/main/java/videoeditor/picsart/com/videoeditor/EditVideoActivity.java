package videoeditor.picsart.com.videoeditor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialin.android.encoder.Encoder;

import java.io.File;
import java.util.ArrayList;

import videoeditor.picsart.com.videoeditor.decoder.VideoDecoder;
import videoeditor.picsart.com.videoeditor.effects.GrayScaleEffect;
import videoeditor.picsart.com.videoeditor.text_art.SimpleTextArt;
import videoeditor.picsart.com.videoeditor.text_art.TextArtObject;
import videoeditor.picsart.com.videoeditor.text_art.TextUtils;


public class EditVideoActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");

    private VideoView videoView;
    private Button playPauseButton;
    private ProgressDialog progressDialog;
    private Button grayScaleButton;
    private Button addTextButton;
    private Button rotateButton;
    private Button encodeButton;

    private EditTextDialod editTextDialod;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);

        context = this;
        init();
    }

    public void init() {

        videoView = (VideoView) findViewById(R.id.video_view);
        playPauseButton = (Button) findViewById(R.id.play_pause_button);
        recyclerView = (RecyclerView) findViewById(R.id.rec_view);
        grayScaleButton = (Button) findViewById(R.id.gray_scale_button);
        addTextButton = (Button) findViewById(R.id.add_text_button);
        rotateButton = (Button) findViewById(R.id.scale_button);
        encodeButton = (Button) findViewById(R.id.encode_button);


        progressDialog = new ProgressDialog(EditVideoActivity.this);
        progressDialog.setMessage("Please Wait...");
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

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });


        final VideoDecoder videoDecoder = new VideoDecoder(videoPath, VideoDecoder.FrameSize.SMALL, myDir.toString());
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

        grayScaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrayScaleEffect effect = new GrayScaleEffect(EditVideoActivity.this);
                effect.startAction(new File(Environment.getExternalStorageDirectory(), "test_images").getPath(), adapter);
            }
        });

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextUtils.addTextToBitmap("hakob",adapter);

                editTextDialod = new EditTextDialod(EditVideoActivity.this);
                editTextDialod.show();
                editTextDialod.setOnRadioGroupChangedListener(new EditTextDialod.OnRadioGroupChangedListener() {
                    @Override
                    public void onRadioGroupChanged(int shapeIndex, int colorIndex, String s) {

                        if (!s.equals("")) {

                            int color=0;
                            switch (shapeIndex) {
                                case 0:
                                    //textView.setTextSize(20);
                                    break;
                                case 1:
                                    //textView.setTextSize(30);
                                    break;
                                case 2:
                                    //textView.setTextSize(50);
                                    break;
                                default:
                                    break;
                            }
                            switch (colorIndex) {
                                case 0:
                                    color=Color.BLUE;
                                    break;
                                case 1:
                                    color=Color.RED;
                                    break;
                                case 2:
                                    color=Color.GREEN;
                                    break;
                                default:
                                    break;
                            }
                            SimpleTextArt addTextArt = new SimpleTextArt(EditVideoActivity.this);
                            TextArtObject obj = new TextArtObject(s,10,10,color);
                            addTextArt.startAction(new File(Environment.getExternalStorageDirectory(), "test_images").getPath(), adapter, obj);
                        }
                    }
                });
            }
        });

        encodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(EditVideoActivity.this);
                progressDialog.show();
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                Encoder encoder = new Encoder();
                encoder.init(ImageLoader.getInstance().loadImageSync("file://" + arrayList.get(0)).getWidth(), ImageLoader.getInstance().loadImageSync("file://" + arrayList.get(0)).getHeight(), 15, null);
                encoder.startVideoGeneration(new File(root + "/vid.mp4"));
                for (int i = 0; i < arrayList.size(); i++) {
                    encoder.addFrame(ImageLoader.getInstance().loadImageSync("file://" + arrayList.get(i)), 50);
                }
                progressDialog.dismiss();
                videoView.setVideoPath(root + "/vid.mp4");
                videoView.start();
            }
        });

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
