package videoeditor.picsart.com.videoeditor;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String realPath = cursor.getString(idx);
                cursor.close();
                return realPath;
            } else {
                return contentUri.getPath();
            }
        } catch (Exception e) {
           e.printStackTrace();
            return contentUri.getPath();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getRealPathFromGallery(Activity activity, Intent data) {
        if (data == null) {
            return "";
        }
        Uri currImageURI = data.getData();
        if (currImageURI == null)
            return "";
        String realPath = currImageURI.toString();

        // check if it is already real path
        if (currImageURI.getScheme() != null && currImageURI.getScheme().startsWith("content")) {
            realPath = getRealPathFromURI(activity, currImageURI);
        } else if (currImageURI.getScheme() != null && currImageURI.getScheme().startsWith("file") && currImageURI.getPath() != null) {
            realPath = currImageURI.getPath();
        }

        try {
            if (realPath != null && !(new File(realPath)).exists()) {
                realPath = null;
            }
        } catch (Exception ex) {
           ex.printStackTrace();
        }

        // /read from inputstream
        if (realPath == null) {
            InputStream is = null;
            try {

                try {
                    ContentResolver res = activity.getContentResolver();
                    Uri uri = Uri.parse(data.getData().toString());
                    is = res.openInputStream(uri);
                } catch (SecurityException e) {
                   e.printStackTrace();
                }

                if (is == null) {
                    return "";
                }
                // create tmp file
                File customDir = createCustomDir(activity.getResources().getString(R.string.tmp_dir), activity);

                File tmpFile = new File(customDir, "tmp_" + System.currentTimeMillis());
                try {
                    tmpFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // /

                if (tmpFile != null && tmpFile.exists() && tmpFile.canRead()) {
                    FileOutputStream out = new FileOutputStream(tmpFile);
                    copyInputStream(is, out);
                    realPath = tmpFile.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d( "realPath:", realPath);
        return realPath;
    }

    public static File createCustomDir(String folderName, Context context) {
        File sdDir = Environment.getExternalStorageDirectory();
        File customDir = null;

        if (sdDir.exists() && sdDir.canWrite()) {
            File pmDir = new File(sdDir, context.getResources().getString(R.string.image_dir));
            pmDir.mkdirs();

            if (pmDir.exists() && pmDir.canWrite()) {
                // create custom folder by name
                if (folderName != null) {
                    customDir = new File(pmDir, folderName);
                    customDir.mkdirs();
                } else {
                    customDir = pmDir;
                }
            }
        }
        return customDir;
    }

    public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }


    public static float pixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return  px / (metrics.densityDpi / 160f);

    }

    public static float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }


}
