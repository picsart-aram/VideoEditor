package service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.io.FileOutputStream;

import utils.Utils;


public class MyService extends Service {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/req_images");

    private boolean isDone = false;
    private String[] paths;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id = 1;

    public boolean getBoolean() {
        return isDone;
    }

    public void onCreate() {
        super.onCreate();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getApplicationContext());

        mBuilder.setContentTitle("PicsArtVideoGenerator")
                .setContentText("0 %")
                .setSmallIcon(R.drawable.ic_action_download);

        mBuilder.setProgress(100, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        paths = intent.getStringArrayExtra("paths");
        Log.d("gagagag", paths[0] + "");
        someTask(paths);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        mBuilder.setContentText("Done");
        // Removes the progress bar
        mBuilder.setProgress(0, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    void someTask(final String[] paths) {
        new Thread(new Runnable() {
            public void run() {

                Looper.prepare();

                for (int i = 0; i < paths.length; i++) {

                    /*if (paths[i].contains("req_images")) {
                        File from = new File(myDir, paths[i].substring(paths[i].lastIndexOf("/")));
                        String fname = "image_" + String.format("%03d", i) + ".jpg";
                        File to = new File(myDir, fname);
                        from.renameTo(to);
                    } else {*/
                        String fname = "image_" + String.format("%03d", i) + ".jpg";

                        try {
                            File file = new File(myDir, fname);
                            Bitmap bitmap;
                            if (file.exists()) {
                                file.delete();
                            }

                            mBuilder.setProgress(1000, i * (1000 / paths.length), false);
                            mBuilder.setContentText(i * (100 / paths.length) + " %");
                            mNotifyManager.notify(id, mBuilder.build());

                            String path = paths[i];

                            //if (paths[i].contains("http://")) {
                                bitmap = ImageLoader.getInstance().loadImageSync(path + "?r1024x1024", new ImageSize(720, 720), DisplayImageOptions.createSimple());
                                bitmap = Utils.scaleCenterCrop(bitmap, 720, 720);
                            /*} else {
                                bitmap = ImageLoader.getInstance().loadImageSync("file://"+path, new ImageSize(720, 720), DisplayImageOptions.createSimple());
                                bitmap = Utils.scaleCenterCrop(bitmap, 720, 720);
                            }*/

                            if (paths[i].contains("req_images")){
                                File file1=new File(myDir,paths[i].substring(paths[i].lastIndexOf("/")));
                                file1.delete();
                            }

                            FileOutputStream out = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.flush();
                            out.close();
                            bitmap.recycle();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error while SaveToMemory", Toast.LENGTH_SHORT).show();
                        }
                    }
               // }
                stopSelf();
            }
        }).start();
    }

}
