package videoeditor.picsart.com.videoeditor.text_art;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import videoeditor.picsart.com.videoeditor.Adapter;
import videoeditor.picsart.com.videoeditor.EditVideoActivity;
import videoeditor.picsart.com.videoeditor.Util;

/**
 * Created by Tigran on 7/22/15.
 */

public class TextUtils {

    static Adapter adapter;

    public static void addTextToBitmap(String text, Adapter a) {

        adapter = a;
        new AddText().execute(text);
    }

    private static Bitmap drawText(String text, String imagePath) {

        // Create bitmap and canvas to draw to
        Bitmap b = ImageLoader.getInstance().loadImageSync("file://" + imagePath);
        Bitmap bitmap = b.copy(Bitmap.Config.RGB_565, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize(20);
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;

        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    private static void saveBitmapToFile(String path, Bitmap bmp) {
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

    private static class AddText extends AsyncTask<String, Integer, Void> {

        ProgressDialog progressDialog = new ProgressDialog(EditVideoActivity.context);
        File file = new File(Environment.getExternalStorageDirectory(), Util.VIDEO_FILES_DIR);
        File[] files = file.listFiles();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {

            for (int i = 0; i < files.length; i++) {
                saveBitmapToFile(files[i].getAbsolutePath(), drawText(params[0], files[i].getAbsolutePath()));
                onProgressUpdate(i);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("gagagagagaga", "" + values[0]);
            if (progressDialog != null) {
                progressDialog.setProgress(values[0]);
                progressDialog.setMax(files.length);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().clearMemoryCache();
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();


        }
    }

}
