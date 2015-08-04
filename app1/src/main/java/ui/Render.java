package ui;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


import com.decoder.PhotoUtils;
import com.decoder.VideoDecoder;
import com.socialin.android.photo.imgop.ImageOp;
import com.socialin.android.encoder.Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;


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

    private static OnRenderFinishedListener onRenderFinishedListener;

    public Render(Context context) {
        this.context = context;
    }

    public void decodeVideos() {

        final VideoDecoder videoDecoder = new VideoDecoder(context, inputVideo1, VideoDecoder.FrameSize.NORMAL, outputImagesDir1);
        videoDecoder.extractVideoFrames();
        videoDecoder.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
            @Override
            public void onFinish(boolean isDone) {
                if (isDone) {
                    onRenderFinishedListener.onFinish(1);
                    VideoDecoder videoDecoder1 = new VideoDecoder(context, inputVideo2, VideoDecoder.FrameSize.NORMAL, outputImagesDir2);
                    videoDecoder1.extractVideoFrames();
                    videoDecoder1.setOnDecodeFinishedListener(new VideoDecoder.OnDecodeFinishedListener() {
                        @Override
                        public void onFinish(boolean isDone) {
                            onRenderFinishedListener.onFinish(2);
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

        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        int x = Math.min(files1.length, files2.length);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < x; i++) {
                bitmaps.add(merge(files1[i].getAbsolutePath(), files2[i].getAbsolutePath()));
                Log.d("gagagagag",bitmaps.get(i).getHeight()+"");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Encoder encoder = new Encoder();
            encoder.init(bitmaps.get(0).getWidth(), bitmaps.get(0).getHeight(), 15, null);
            encoder.startVideoGeneration(new File(root + "/vid.mp4"));
            for (int i = 0; i < bitmaps.size(); i++) {
                encoder.addFrame(bitmaps.get(i), 50);
            }
            onRenderFinishedListener.onFinish(3);
            super.onPostExecute(result);
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

        ByteBuffer buffer1 = PhotoUtils.readBufferFromFile(path1, 360*640*4);
        Bitmap bitmap1 = PhotoUtils.fromBufferToBitmap(360, 640, 0, buffer1);
        ImageOp.freeNativeBuffer(buffer1);


        ByteBuffer buffer2 = PhotoUtils.readBufferFromFile(path2, 360*640*4);
        Bitmap bitmap2 = PhotoUtils.fromBufferToBitmap(360, 640, 0, buffer2);
        ImageOp.freeNativeBuffer(buffer2);

        Bitmap mergedBitmap = Bitmap.createBitmap(720, 640, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(mergedBitmap);
        canvas.drawBitmap(bitmap1, 0, 0, new Paint());
        canvas.drawBitmap(bitmap2, 360, 0, new Paint());

        return mergedBitmap;
    }

    public void setOnRenderFinishedListener(OnRenderFinishedListener l) {
        onRenderFinishedListener = l;
    }

    public interface OnRenderFinishedListener {

        void onFinish(int progress);

    }

}
