package videoeditor.picsart.com.videoeditor;

import android.content.Intent;
import android.graphics.Bitmap;
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
import videoeditor.picsart.com.videoeditor.decoder.MediaMuxerTest;


public class MainActivity extends AppCompatActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/" + Util.VIDEO_FILES_DIR);

    private static final int REQUEST_SELECT_VIDEO = 100;
    private static final int REQUEST_SELECT_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Util.initImageLoader(MainActivity.this);
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        Util.createDir(Util.VIDEO_FILES_DIR);

        Log.d("gagagaga", "" + Util.isTablet(MainActivity.this));
        init();
    }

    private void init() {

        findViewById(R.id.select_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
                mediaChooser.setType("video/*");
                startActivityForResult(mediaChooser, REQUEST_SELECT_VIDEO);*/
                foo();
            }
        });

        findViewById(R.id.select_image).setOnClickListener(new View.OnClickListener() {
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

    public void foo() {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(root + "/myvideo1.mp4");

        String METADATA_KEY_DURATION = mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        Bitmap bmpOriginal = mediaMetadataRetriever.getFrameAtTime(0);
        int bmpVideoHeight = bmpOriginal.getHeight();
        int bmpVideoWidth = bmpOriginal.getWidth();

        Log.d("gagagagagag", "bmpVideoWidth:'" + bmpVideoWidth + "'  bmpVideoHeight:'" + bmpVideoHeight + "'");

        byte[] lastSavedByteArray = new byte[0];

        float factor = 0.20f;
        int scaleWidth = (int) ((float) bmpVideoWidth * factor);
        int scaleHeight = (int) ((float) bmpVideoHeight * factor);
        int max = (int) Long.parseLong(METADATA_KEY_DURATION);
        for (int index = 0; index < max; index++) {

            bmpOriginal = mediaMetadataRetriever.getFrameAtTime(index * 1000000, MediaMetadataRetriever.OPTION_CLOSEST);

            bmpVideoHeight = bmpOriginal == null ? -1 : bmpOriginal.getHeight();

            bmpVideoWidth = bmpOriginal == null ? -1 : bmpOriginal.getWidth();

            int byteCount = bmpOriginal.getWidth() * bmpOriginal.getHeight() * 4;
            ByteBuffer tmpByteBuffer = ByteBuffer.allocate(byteCount);
            bmpOriginal.copyPixelsToBuffer(tmpByteBuffer);
            byte[] tmpByteArray = tmpByteBuffer.array();

            if (!Arrays.equals(tmpByteArray, lastSavedByteArray)) {
                int quality = 100;

                File outputFile = new File(myDir, "IMG_" + (index + 1)
                        + "_" + max + "_quality_" + quality + "_w" + scaleWidth + "_h" + scaleHeight + ".png");

                OutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(outputFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bmpScaledSize = Bitmap.createScaledBitmap(bmpOriginal, scaleWidth, scaleHeight, false);

                bmpScaledSize.compress(Bitmap.CompressFormat.PNG, quality, outputStream);

                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                lastSavedByteArray = tmpByteArray;
            }
        }

        mediaMetadataRetriever.release();
    }

}
