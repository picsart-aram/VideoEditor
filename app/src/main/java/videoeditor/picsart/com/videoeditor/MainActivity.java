package videoeditor.picsart.com.videoeditor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.utils.WavReader;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import videoeditor.picsart.com.videoeditor.clipart.ClipartActivity;


public class MainActivity extends AppCompatActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/" + Util.VIDEO_FILES_DIR);

    private static final int REQUEST_SELECT_VIDEO = 100;
    private static final int REQUEST_SELECT_IMAGE = 101;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        context = this;
        Util.initImageLoader(MainActivity.this);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        SharedPreferences sharedPreferences = this.getSharedPreferences("pics_art_video_editor", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        Util.createDir(Util.VIDEO_FILES_DIR);

        init();
    }

    private void init() {

        findViewById(R.id.select_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
                mediaChooser.setType("video/*");
                startActivityForResult(mediaChooser, REQUEST_SELECT_VIDEO);
            }
        });

        findViewById(R.id.select_slider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditVideoActivity.class);
                intent.putExtra("video_path", root + "/myvideo1.mp4");
                startActivity(intent);
                /*Intent imgChooser = new Intent(Intent.ACTION_GET_CONTENT);
                imgChooser.setType("image/*");
                startActivityForResult(Intent.createChooser(imgChooser, "Select Picture"), REQUEST_SELECT_IMAGE);*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_VIDEO) {

                Intent intent = new Intent(MainActivity.this, EditVideoActivity.class);
                intent.putExtra("video_path", Util.getRealPathFromURI(getApplicationContext(), data.getData()));
                startActivity(intent);

            } else if (requestCode == REQUEST_SELECT_IMAGE) {
                String imgPath = Util.getRealPathFromGallery(this, data);
                System.out.println("Video::  imgPath= " + imgPath);
                Intent intent = new Intent(this, ClipartActivity.class);
                intent.putExtra("path", imgPath);
                startActivity(intent);
            }
        }
    }

    private ArrayList<String> getImages() {

        ArrayList<String> test = new ArrayList<>();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + Util.VIDEO_FILES_DIR);
        for (int i = 0; i < myDir.list().length; i++) {
            test.add(myDir.list()[i]);
        }
        return test;
    }

    private ArrayList<String> filterPaths(ArrayList<String> paths) {
        int duration = paths.size() / 5;
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            if (i % duration == 0) {
                result.add(paths.get(i));
            }
        }
        return result;
    }

}
