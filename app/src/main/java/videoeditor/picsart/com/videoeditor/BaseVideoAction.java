package videoeditor.picsart.com.videoeditor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by AramNazaryan on 7/17/15.
 */
public abstract class BaseVideoAction<T> {

    private Activity activity = null;
    private ProgressDialog progressDialog = null;

    protected abstract Bitmap doActionOnBitmap (Bitmap bmp, T... params);

    public BaseVideoAction( Activity activity, T... params) {
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
                String[] bitmapPaths = parentDirectory.list();

                for (int i = 0; i < bitmapPaths.length; i++) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inMutable = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(parentDirectory.getPath()+"/"+bitmapPaths[i], opts);
                    Bitmap bitmapAfterAction = doActionOnBitmap(bitmap, parameters);
                    bitmap.recycle();
                    saveBitmapToFile(parentDirectory.getPath()+"/"+bitmapPaths[i], bitmapAfterAction);
                    bitmapAfterAction.recycle();
                    onProgressUpdate(i, bitmapPaths.length);
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
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
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
