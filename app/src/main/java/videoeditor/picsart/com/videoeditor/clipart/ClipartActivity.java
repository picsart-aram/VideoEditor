package videoeditor.picsart.com.videoeditor.clipart;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import videoeditor.picsart.com.videoeditor.MainView;
import videoeditor.picsart.com.videoeditor.R;
import videoeditor.picsart.com.videoeditor.Util;

public class ClipartActivity extends ActionBarActivity {

    private final String TAG = ClipartActivity.class.getSimpleName();
    private final int[] clipartList = new int[]{
            R.drawable.clipart_1,
            R.drawable.clipart_2,
            R.drawable.clipart_3,
            R.drawable.clipart_4,
            R.drawable.clipart_5,
            R.drawable.clipart_6,
            R.drawable.clipart_7,
            R.drawable.clipart_8
    };

    private MainView mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clipart_main);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setTitle("Add Clipart");
        }

        String realPath = getIntent().getStringExtra("image_path");
        String message = realPath;
        File imgFile = new File(Environment.getExternalStorageDirectory() + realPath);
        System.out.println(TAG + "::  imgFile= " + imgFile.getAbsolutePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            System.out.println(TAG + "::  bitmap= " + bitmap.getWidth() + "x" + bitmap.getHeight());
            message += "\n bitmap= " + bitmap.getWidth() + "x" + bitmap.getHeight();
        }
        System.out.println(message);

        initView(realPath);
        intiCliparts();
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            done();
            finish();
        }
        return true;
    }

    private void done() {
        if (mainView != null) {
            mainView.saveItemsToBitmap();
            Bitmap resultBitmap = mainView.getOriginBitmapCopy();
            if (resultBitmap != null) {
                ClipArtAction clipartAction = new ClipArtAction(ClipartActivity.this);
                Clipart clipart = new Clipart(resultBitmap, 0, 0);
                clipartAction.startAction(Util.getVideoFilePath(), clipart);
                //TODO replace with callback listener
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void initView(String path) {
        mainView = new MainView(this, path);
        mainView.setId(R.id.mainViewId);
        ViewGroup container = (ViewGroup) findViewById(R.id.main_view_container);
        container.removeAllViews();
        container.addView(mainView);
    }

    private void intiCliparts() {
        LinearLayout container = (LinearLayout) findViewById(R.id.clipart_horizontal_list_container);
        container.removeAllViews();

        for (int i = 0; i < clipartList.length; i++) {
            //TODO replace to LinearHorizontalRecyclerView
            ImageView imgView = new ImageView(this);
            int size = (int) Util.dpToPixel(35, this);
            int margin = (int) Util.dpToPixel(7, this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            layoutParams.setMargins(margin, margin, margin, margin);
            imgView.setLayoutParams(layoutParams);
            imgView.setBackgroundResource(clipartList[i]);

            final int position = i;
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addClipart(position);
                }
            });

            container.addView(imgView);
        }

        container.setVisibility(View.VISIBLE);
    }


    private void addClipart(int position) {
        if (mainView != null) {
            mainView.addClipart(clipartList[position]);
        }
    }
}
