package videoeditor.picsart.com.videoeditor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaCodec;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import videoeditor.picsart.com.videoeditor.effects.GrayScaleEffect;


public class MainActivity extends AppCompatActivity {

    private Button selectVideoButton = null;
    private ExtractMpegFramesTest mediaExtractor = null;
    private static final int REQUEST_SELECT_VIDEO = 100;
    private ProgressDialog progress = null;
    private View actionsContainer = null;
    private SeekBarWithTwoThumb swtt;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init(){
        mediaExtractor = new ExtractMpegFramesTest();
        selectVideoButton = (Button) findViewById(R.id.select_video);
        selectVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
                mediaChooser.setType("video/*");
                startActivityForResult(mediaChooser, REQUEST_SELECT_VIDEO);
            }
        });
        actionsContainer = findViewById(R.id.video_actions_layout);
        progress = new ProgressDialog(MainActivity.this);
        progress.setMessage("Please wait...");

        findViewById(R.id.make_grayscale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrayScaleEffect effect = new GrayScaleEffect(MainActivity.this);
                effect.startAction(new File(Environment.getExternalStorageDirectory(), "test_images").getPath());
            }
        });
    }
        swtt = (SeekBarWithTwoThumb) findViewById(R.id.myseekbar);
        container = (LinearLayout) findViewById(R.id.frames_holder);

    }

    private ArrayList<String> getImages(){

        ArrayList<String> test=new ArrayList<>();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/test_images");
        for (int i = 0; i < myDir.list().length; i++) {
            test.add(myDir.list()[i]);
        }

//        ArrayList<String> result=new ArrayList<>();
//        String[] mProjection = {MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.DATA};
//        Cursor cursorEx = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mProjection, null, null, MediaStore.Images.Media.DATE_ADDED);
//        while (cursorEx.moveToNext()) {
//            String fileLoc = cursorEx.getString(1);
//            result.add(fileLoc);
//        }
        return test;

    }

    private void addFrameToLayout(ArrayList<String> paths){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.frames_holder);

//        for (int i = 0; i < 3; i++) {
//            File image = new File(paths.get(i));
//            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
//            bitmap = Bitmap.createScaledBitmap(bitmap,115,115,true);
//            ImageView imageView = new ImageView(this);
//            imageView.setLayoutParams(new ViewGroup.LayoutParams(115, 115));
//            imageView.setImageBitmap(bitmap);
//            linearLayout.addView(imageView);
//        }

        for (int i = 0; i < paths.size(); i++) {
            File image = new File(Environment.getExternalStorageDirectory().toString() + "/test_images/"+paths.get(i));
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap,115,115,true);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(115, 115));
            imageView.setImageBitmap(bitmap);
            linearLayout.addView(imageView);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_VIDEO) {
                AsyncTask<Void, Void, Void> encoderTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            mediaExtractor.testExtractMpegFrames(getRealPathFromURI(getApplicationContext(), data.getData()));
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        progress.dismiss();
                        actionsContainer.setVisibility(View.VISIBLE);
                        addFrameToLayout(getImages());
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progress.show();
                    }
                };
                createDir("test_images");
                encoderTask.execute();
                System.out.println("path = "+getRealPathFromURI(getApplicationContext(), data.getData()));

            }
        }
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
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
}
