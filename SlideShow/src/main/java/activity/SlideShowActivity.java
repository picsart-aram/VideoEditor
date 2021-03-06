package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.decoder.MediaMuxerTest;
import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.socialin.android.encoder.Encoder;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import adapter.ImagePagerAdapter;
import adapter.SlideShowAdapter;
import item.SlideShowItem;
import utils.FileUtils;
import utils.SlideShowConst;
import utils.Utils;


public class SlideShowActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private Button openGalleryButton;
    private Button openPicsArtButton;
    private Button playButton;
    private TextView textView;
    private SeekBar seekBar;

    private ImagePagerAdapter imagePagerAdapter;
    private SlideShowAdapter slideShowAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;

    private SlideShow slideShow;
    private Boolean playButtonIsSelected = false;

    private int frameDuration = 600;

    private ArrayList<SlideShowItem> selectedImagesPathList = new ArrayList<>();

    public static final int REQUEST_CODE_FOR_EDIT = 300;
    public static final int REQUEST_CODE_FOR_CUSTOM_GALLERY = 200;
    public static final int REQUEST_CODE_FOR_PICS_ART = 100;

    public static final String INDEX = "index";
    public static final String EDITED_IMAGE_PATH = "edited_image_path";

    private static final String root = Environment.getExternalStorageDirectory().toString();
    double halfWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);

        Intent intent = getIntent();
        ArrayList<CharSequence> charSequences;
        charSequences = intent.getCharSequenceArrayListExtra(SlideShowConst.IMAGE_PATHS);
        for (int i = 0; i < charSequences.size(); i++) {
            SlideShowItem slideShowItem = new SlideShowItem(charSequences.get(i).toString(), false, false);
            if (intent.getBooleanExtra(SlideShowConst.IS_FILE, false)) {
                slideShowItem.setIsFromFileSystem(true);
            }
            selectedImagesPathList.add(slideShowItem);
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        halfWidth = metrics.widthPixels;

        init();

    }

    private void init() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_slide_show);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        openGalleryButton = (Button) findViewById(R.id.open_gallery_button);
        openPicsArtButton = (Button) findViewById(R.id.open_pics_art_button);
        playButton = (Button) findViewById(R.id.play_button);
        textView = (TextView) findViewById(R.id.duration_text);
        seekBar = (SeekBar) findViewById(R.id.duration_seek_bar);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        viewPager.setLayoutParams(layoutParams);
        imagePagerAdapter = new ImagePagerAdapter(selectedImagesPathList, SlideShowActivity.this, recyclerView);
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setOffscreenPageLimit(100);

        slideShowAdapter = new SlideShowAdapter(selectedImagesPathList, this, viewPager, imagePagerAdapter);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(slideShowAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlideShowActivity.this, CustomGalleryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_CUSTOM_GALLERY);
            }
        });

        openPicsArtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlideShowActivity.this, PicsArtGalleryActvity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_PICS_ART);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playButtonIsSelected == false) {
                    playButtonIsSelected = true;
                    viewPager.scrollTo(0, 0);
                    slideShow = new SlideShow();
                    slideShow.execute();

                } else {
                    playButtonIsSelected = false;
                    slideShow.cancel(true);
                    Toast.makeText(getApplicationContext(), "stoped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("dur : " + progress * 100 + " mls");
                frameDuration = progress * 100 + 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_PICS_ART && resultCode == Activity.RESULT_OK) {
            ArrayList<CharSequence> all_path = data.getCharSequenceArrayListExtra(SlideShowConst.IMAGE_PATHS);
            if (all_path.size() > 0) {
                for (int i = 0; i < all_path.size(); i++) {
                    SlideShowItem slideShowItem = new SlideShowItem(all_path.get(i).toString(), false, false);
                    selectedImagesPathList.add(slideShowItem);
                }
                imagePagerAdapter.notifyDataSetChanged();
                slideShowAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            }
        }
        if (requestCode == REQUEST_CODE_FOR_CUSTOM_GALLERY && resultCode == Activity.RESULT_OK) {
            ArrayList<CharSequence> all_path = data.getCharSequenceArrayListExtra(SlideShowConst.IMAGE_PATHS);
            if (all_path.size() > 0) {
                for (int i = 0; i < all_path.size(); i++) {
                    SlideShowItem slideShowItem = new SlideShowItem(all_path.get(i).toString(), false, true);
                    selectedImagesPathList.add(slideShowItem);
                }
                imagePagerAdapter.notifyDataSetChanged();
                slideShowAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            }
        }
        if (requestCode == REQUEST_CODE_FOR_EDIT && resultCode == Activity.RESULT_OK) {
            if (data.getStringExtra(EDITED_IMAGE_PATH) != "") {
                SlideShowItem slideShowItem = new SlideShowItem(data.getStringExtra(EDITED_IMAGE_PATH), true, true);
                selectedImagesPathList.set(data.getIntExtra(INDEX, -1), slideShowItem);
                slideShowAdapter.notifyItemChanged(data.getIntExtra(INDEX, -1));
                imagePagerAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_slide_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (selectedImagesPathList.size() > 0) {
                new EncodeFrames().execute(selectedImagesPathList);
                //EncodeByteRgbFrameExample.encodeByteRgbFrameExample(root + "/vid1.mp4", array, LibVpxEnc.FOURCC_ABGR, 720, 720, 10, 1, 50, new StringBuilder());

            } else {
                Toast.makeText(getApplicationContext(), "you have no image", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SlideShowConst.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        super.onDestroy();
    }

    private class SlideShow extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void... path) {

            for (int i = 0; i < selectedImagesPathList.size(); i++) {
                for (int j = 80; j > 0; j--) {
                    if (isCancelled()) return null;

                    final int finalI = i;
                    final int finalJ = j;
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            staggeredGridLayoutManager.scrollToPositionWithOffset(finalI + 1, finalJ * 2);
                            /*if (finalI < selectedImagesPathList.size() - 1) {
                                viewPager.scrollBy((int) (halfWidth/80), 0);
                            }*/
                            viewPager.setCurrentItem(finalI);
                        }
                    });
                    try {
                        TimeUnit.MILLISECONDS.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            playButtonIsSelected = false;
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            playButtonIsSelected = false;
            super.onPostExecute(aVoid);
        }
    }

    private class EncodeFrames extends AsyncTask<ArrayList<SlideShowItem>, Integer, Void> {

        ProgressDialog progressDialog;
        Encoder encoder;

        protected Void doInBackground(ArrayList<SlideShowItem>... path) {

            Bitmap bitmap = null;
            for (int i = 0; i < path[0].size() + 1; i++) {
                try {
                    if (i < path[0].size()) {
                        if (!path[0].get(i).isFromFileSystem()) {
                            bitmap = ImageLoader.getInstance().loadImageSync(path[0].get(i).getPath() + SlideShowConst.WEB_IMAGE_SIZE_1024);
                            bitmap = Utils.scaleCenterCrop(bitmap, SlideShowConst.FRAME_SIZE_720, SlideShowConst.FRAME_SIZE_720);
                        } else {
                            bitmap = ImageLoader.getInstance().loadImageSync(SlideShowConst.FILE_PREFIX + path[0].get(i).getPath());
                            bitmap = Utils.scaleCenterCrop(bitmap, SlideShowConst.FRAME_SIZE_720, SlideShowConst.FRAME_SIZE_720);
                        }
                    } else {
                        bitmap = Bitmap.createBitmap(SlideShowConst.FRAME_SIZE_720, SlideShowConst.FRAME_SIZE_720, Bitmap.Config.ARGB_8888);
                    }
                    encoder.addFrame(bitmap, frameDuration);
                    onProgressUpdate(i);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error while SaveToMemory", Toast.LENGTH_SHORT).show();
                }
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            if (progressDialog != null) {
                progressDialog.setProgress(progress[0]);
                progressDialog.setMax(selectedImagesPathList.size());
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            encoder.endVideoGeneration();
            progressDialog.dismiss();
            Intent tostart = new Intent(Intent.ACTION_VIEW);
            tostart.setDataAndType(Uri.parse(SlideShowConst.OUTPUT_VIDEO_DIR), "video/*");
            startActivity(tostart);
            FileUtils.clearDir(new File(SlideShowConst.ROOT_DIR, SlideShowConst.MY_DIR_NAME));
            finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SlideShowActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            encoder = new Encoder();
            encoder.init(SlideShowConst.FRAME_SIZE_720, SlideShowConst.FRAME_SIZE_720, SlideShowConst.OUTPUT_FPS, null);
            encoder.startVideoGeneration(new File(SlideShowConst.OUTPUT_VIDEO_DIR));
        }
    }

}

