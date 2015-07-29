package videoeditor.picsart.com.videoeditor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import hackathon.videoeditor.utils.OnVideoActionFinishListener;
import videoeditor.picsart.com.videoeditor.decoder.PhotoUtils;
import videoeditor.picsart.com.videoeditor.effects.Utils.OnEffectApplyFinishedListener;

/**
 * Created by AramNazaryan on 7/17/15.
 */
public abstract class BaseVideoAction<T> {

    private Activity activity = null;
    private ProgressDialog progressDialog = null;
    private OnVideoActionFinishListener listener = null;

    protected abstract Bitmap doActionOnBitmap(Bitmap videoFrameBitmap, T... params);

    public BaseVideoAction(Activity activity, T... params) {
        this.activity = activity;
        if (activity != null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
    }

    public void startAction(final String folderPath, final T... parameters) {

        AsyncTask<Void, Integer, Void> doActionTask = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                File parentDirectory = new File(folderPath);
                File[] bitmapPaths = parentDirectory.listFiles();

                for (int i = 0; i < bitmapPaths.length; i++) {
                    /*Bitmap bitmap = BitmapFactory.decodeFile(parentDirectory.getPath() + "/" + bitmapPaths[i]);
                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    bitmap.recycle();
                    Bitmap bitmapAfterAction = doActionOnBitmap(mutableBitmap, parameters);
                    saveBitmapToFile(parentDirectory.getPath() + "/" + bitmapPaths[i], bitmapAfterAction);
                    mutableBitmap.recycle();
                    bitmapAfterAction.recycle();*/

                    SharedPreferences sharedPreferences = activity.getSharedPreferences("pics_art_video_editor", Context.MODE_PRIVATE);
                    int bufferSize = sharedPreferences.getInt("buffer_size", 0);
                    int width = sharedPreferences.getInt("frame_width", 0);
                    int height = sharedPreferences.getInt("frame_height", 0);
                    int orientation = sharedPreferences.getInt("frame_orientation", 0);
                    ByteBuffer buffer = PhotoUtils.readBufferFromFile(bitmapPaths[i].getAbsolutePath(), bufferSize);
                    Bitmap bitmap = PhotoUtils.fromBufferToBitmap(width, height, orientation, buffer);
                    Bitmap bitmapAfterAction = doActionOnBitmap(bitmap, parameters);
                    PhotoUtils.saveBufferToSDCard(bitmapPaths[i].getAbsolutePath(), PhotoUtils.fromBitmapToBuffer(bitmapAfterAction));
                    onProgressUpdate(i, bitmapPaths.length);
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressDialog != null) {
                    progressDialog.show();
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                //adapter.notifyDataSetChanged();
//                activity.finish();
                if (listener != null) {
                    listener.onSuccess();
                }
                Log.d("gagagagag", "hasar iji");
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if (progressDialog != null) {
                    progressDialog.setProgress(values[0]);
                    progressDialog.setMax(values[1]);
                }
            }
        };

        doActionTask.execute();
    }

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

    public void setOnVideoFinishListener(OnVideoActionFinishListener listener) {
        this.listener = listener;
    }

    public void applyOnOneFrame(final String path, final OnEffectApplyFinishedListener listener, final T... parameters) {
        AsyncTask<Void, Void, Bitmap> doActionTask = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                System.out.println("decode file " + path);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                System.out.println("bitmap = " + bitmap);
                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                bitmap.recycle();
                Bitmap bitmapAfterAction = doActionOnBitmap(mutableBitmap, parameters);
//                mutableBitmap.recycle();
                return bitmapAfterAction;
            }

            @Override
            protected void onPostExecute(Bitmap bmp) {
                if (listener != null) {
                    listener.onFinish(bmp);
                }
            }
        };

        doActionTask.execute();
    }
}
