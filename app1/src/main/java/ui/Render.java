package ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialin.android.encoder.Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import decoder.PhotoUtils;
import decoder.VideoDecoder;


/**
 * Created by Tigran on 7/23/15.
 */
public class Render {

    private Context context;
    private ArrayList<String> arrayList = new ArrayList<>();
    private static final String root = Environment.getExternalStorageDirectory().toString();

    private String inputVideo1 = Environment.getExternalStorageDirectory().getPath() + "/picsartVideo/myvideo1.mp4";
    private String inputVideo2 = Environment.getExternalStorageDirectory().getPath() + "/picsartVideo/myvideo2.mp4";
    private String outputImagesDir1 = Environment.getExternalStorageDirectory().getPath() + "/picsartVideo/sourcefirst/";
    private String outputImagesDir2 = Environment.getExternalStorageDirectory().getPath() + "/picsartVideo/sourcesecond/";
    private String mergedframes = Environment.getExternalStorageDirectory().getPath() + "/picsartVideo/mergedframes/";

    public Render(Context context) {
        this.context = context;
    }

    public void decodeVideos() {

        final VideoDecoder videoDecoder = new VideoDecoder(context, inputVideo1, VideoDecoder.FrameSize.ORIGINAL, outputImagesDir1);
        videoDecoder.extractVideoFrames();
        videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
            @Override
            public void onFinish(boolean isDone) {
                if (isDone) {
                    VideoDecoder videoDecoder1 = new VideoDecoder(context, inputVideo2, VideoDecoder.FrameSize.ORIGINAL, outputImagesDir2);
                    videoDecoder1.extractVideoFrames();
                    videoDecoder1.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                        @Override
                        public void onFinish(boolean isDone) {
                            Log.d("gagagagaga", "exav ashkt lus");
                            new MergeFrames().execute();
                        }
                    });
                }
            }
        });
    }

    /**
     * Merging two images with asynctask
     */
    private class MergeFrames extends AsyncTask<Void, Void, Void> {

        File file1 = new File(outputImagesDir1);
        File[] files1 = file1.listFiles();
        File file2 = new File(outputImagesDir2);
        File[] files2 = file2.listFiles();

        ArrayList<Bitmap> bitmaps=new ArrayList<>();

        int x = Math.min(files1.length, files2.length);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < x; i++) {
                bitmaps.add(merge(files1[i].getAbsolutePath(), files2[i].getAbsolutePath()));
                //saveBitmapToFile(mergedframes + String.format("frame-%03d.png", i), merge(files1[i].getAbsolutePath(), files2[i].getAbsolutePath()));
                //arrayList.add(mergedframes + String.format("frame-%03d.png", i));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Encoder encoder = new Encoder();
            encoder.init(bitmaps.get(0).getWidth(),bitmaps.get(0).getHeight(), 15, null);
            encoder.startVideoGeneration(new File(root + "/vid.mp4"));
            for (int i = 0; i < bitmaps.size(); i++) {
                encoder.addFrame(bitmaps.get(i), 50);
            }

        }
    }

    /**
     * Merging two images
     *
     * @param path1 first image path
     * @param path2 second image path
     * @return
     */
    public Bitmap merge(String path1, String path2) {

        ByteBuffer buffer1 = PhotoUtils.readBufferFromFile(path1, PhotoUtils.checkBufferSize(inputVideo1, VideoDecoder.FrameSize.ORIGINAL));
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(inputVideo1);
        int height = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int width = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        Bitmap bitmap1=PhotoUtils.fromBufferToBitmap(width, height, buffer1);
        Matrix m = new Matrix();
        m.postRotate(180);
        m.preScale(-1, 1);
        Bitmap newBitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), m, false);
        buffer1.clear();

        ByteBuffer buffer2 = PhotoUtils.readBufferFromFile(path2, PhotoUtils.checkBufferSize(inputVideo2, VideoDecoder.FrameSize.ORIGINAL));
        MediaMetadataRetriever metaRetriever2 = new MediaMetadataRetriever();
        metaRetriever2.setDataSource(inputVideo2);
        int height2 = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int width2 = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        Bitmap bitmap2=PhotoUtils.fromBufferToBitmap(width2, height2, buffer2);
        Matrix m2 = new Matrix();
        m2.postRotate(180);
        m2.preScale(-1, 1);
        Bitmap newBitmap2 = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), m, false);
        buffer2.clear();

        Bitmap mergedBitmap = Bitmap.createBitmap(2 * width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(mergedBitmap);
        canvas.drawBitmap(newBitmap1, 0, 0, new Paint());
        canvas.drawBitmap(newBitmap2, width, 0, new Paint());

        /*Bitmap bitmap1 = ImageLoader.getInstance().loadImageSync("file://" + path1);
        Bitmap mutableBitmap1 = bitmap1.copy(Bitmap.Config.ARGB_4444, true);
        Bitmap bitmap2 = ImageLoader.getInstance().loadImageSync("file://" + path2);
        Bitmap mutableBitmap2 = bitmap2.copy(Bitmap.Config.ARGB_4444, true);
        int width = mutableBitmap1.getWidth();
        int height = mutableBitmap1.getHeight();

        Bitmap mergedBitmap = Bitmap.createBitmap(2 * width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(mergedBitmap);
        canvas.drawBitmap(mutableBitmap1, 0, 0, new Paint());
        canvas.drawBitmap(mutableBitmap2, width, 0, new Paint());*/

        return BitmapFactory.decodeFile("");
    }

    /**
     * Saving Bitmap to given directory
     *
     * @param path saving image path
     * @param bmp  saving bitmap
     */
    private void saveBitmapToFile(String path, Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
