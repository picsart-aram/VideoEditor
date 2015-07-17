package videoeditor.picsart.com.videoeditor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import videoeditor.picsart.com.videoeditor.effects.GrayScaleEffect;


public class MainActivity extends AppCompatActivity {

    private Button selectVideoButton = null;
    private ExtractMpegFramesTest mediaExtractor = null;
    private static final int REQUEST_SELECT_VIDEO = 100;
    private ProgressDialog progress = null;
    private View actionsContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
