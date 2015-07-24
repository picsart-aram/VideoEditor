package videoeditor.picsart.com.videoeditor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialin.android.encoder.Encoder;

import java.io.File;
import java.util.ArrayList;

import videoeditor.picsart.com.videoeditor.clipart.ClipartActivity;
import videoeditor.picsart.com.videoeditor.clipart.EmbossEffect;
import videoeditor.picsart.com.videoeditor.decoder.VideoDecoder;
import videoeditor.picsart.com.videoeditor.effects.EngraveEffect;


public class EditVideoActivity extends Activity implements SeekBarWithTwoThumb.SeekBarChangeListener{

    private static final int REQUEST_ADD_TEXT = 300;
    private static final int REQUEST_ADD_CLIPART = 301;

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/" + Util.VIDEO_FILES_DIR);

    private VideoView videoView;
    private ProgressDialog progressDialog;

//    private EditTextDialod editTextDialod;
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
    private int framesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);

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
                //TODO
            }
        });

        final VideoDecoder videoDecoder = new VideoDecoder(videoPath, VideoDecoder.FrameSize.SMALL, myDir.toString());
        videoDecoder.extractVideoFrames();
        videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
            @Override
            public void onFinish(boolean isDone) {
                File[] files = myDir.listFiles();
                framesCount = files.length;
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

        findViewById(R.id.scale_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
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
        Log.d("MyLog", "thumb1 : " +Thumb1Value*framesCount/100 + " thumb2 " + Thumb2Value*framesCount/100);
    }
}
