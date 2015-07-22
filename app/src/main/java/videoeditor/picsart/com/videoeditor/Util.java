package videoeditor.picsart.com.videoeditor;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

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

        Log.d("realPath:", realPath);
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
        return px / (metrics.densityDpi / 160f);

    }

    public static float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static void clearDir(File dir) {
        try {
            File[] files = dir.listFiles();
            if (files != null)
                for (File f : files) {
                    if (f.isDirectory())
                        clearDir(f);
                    f.delete();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createDir(String fileName) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + fileName);
        if (!myDir.exists()) {
            myDir.mkdirs();
        } else {
            clearDir(myDir);
            File file = new File(myDir.toString());
            file.delete();
            myDir.mkdirs();
        }
    }

    public static void initImageLoader(Context context) {
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();

            File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                    CACHE_DIR);

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .considerExifParams(true)
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .decodingOptions(new BitmapFactory.Options())
                    .bitmapConfig(Bitmap.Config.RGB_565).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    /*.memoryCacheExtraOptions(1000, 1000) // width, height
                    .discCacheExtraOptions(1000, 1000, new BitmapProcessor() {
                        @Override
                        public Bitmap process(Bitmap bitmap) {
                            return null;
                        }
                    })*/
                    //.threadPoolSize(3)
                    //.threadPriority(Thread.MIN_PRIORITY + 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new UsingFreqLimitedMemoryCache(3 * 1024 * 1024)) // 3 Mb
                    .discCache(new UnlimitedDiscCache(cacheDir))
                    .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .imageDownloader(new BaseImageDownloader(context)) // connectTimeout (5 s), readTimeout (30 s)
                    .defaultDisplayImageOptions(defaultOptions)
                    .build();

            ImageLoader.getInstance().init(config);

        } catch (Exception e) {
        }
    }

    public static boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

}
