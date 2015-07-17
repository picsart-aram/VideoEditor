package videoeditor.picsart.com.videoeditor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;

import videoeditor.picsart.com.videoeditor.effects.GrayScaleEffect;
import videoeditor.picsart.com.videoeditor.text_art.SimpleTextArt;
import videoeditor.picsart.com.videoeditor.text_art.TextArtObject;


public class MainActivity extends AppCompatActivity {

    private ExtractMpegFramesTest mediaExtractor = null;
    private static final int REQUEST_SELECT_VIDEO = 100;
    private static final int REQUEST_SELECT_IMAGE = 101;
    private ProgressDialog progress = null;
    private View actionsContainer = null;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        mediaExtractor = new ExtractMpegFramesTest();
        findViewById(R.id.select_video).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.add_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTextArt addTextArt = new SimpleTextArt(MainActivity.this);
                TextArtObject obj = new TextArtObject();
                obj.x = 100;
                obj.y = 30;
                obj.text = "Hello ashxarh";
                addTextArt.startAction(new File(Environment.getExternalStorageDirectory(), "test_images").getPath(), obj);
            }
        });
        container = (LinearLayout) findViewById(R.id.frames_holder);


        findViewById(R.id.select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgChooser = new Intent(Intent.ACTION_GET_CONTENT);
                imgChooser.setType("image/*");
                startActivityForResult(Intent.createChooser(imgChooser, "Select Picture"), REQUEST_SELECT_IMAGE);
            }
        });
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
            container.addView(imageView);
        }

    }

    private ArrayList<String> filterPaths (ArrayList<String> paths){
        int duration=paths.size()/5;
        ArrayList<String> result=new ArrayList<>();
        for (int i = 0; i <paths.size() ; i++) {
            if(i%duration==0){
                result.add(paths.get(i));
            }
        }
        return result;

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
                            mediaExtractor.testExtractMpegFrames(Util.getRealPathFromURI(getApplicationContext(), data.getData()));
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
                        addFrameToLayout(filterPaths(getImages()));
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progress.show();
                    }
                };
                createDir("test_images");
                encoderTask.execute();
                System.out.println("path = " + Util.getRealPathFromURI(getApplicationContext(), data.getData()));

            } else if (requestCode == REQUEST_SELECT_IMAGE) {
                String imgPath = Util.getRealPathFromGallery(this, data);
                System.out.println("Video::  imgPath= " + imgPath);
                Intent intent = new Intent(this, ClipartActivity.class);
                intent.putExtra("path", imgPath);
                startActivity(intent);
            }
        }
    }


//    public String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = {MediaStore.Images.Media.DATA};
//            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }

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
