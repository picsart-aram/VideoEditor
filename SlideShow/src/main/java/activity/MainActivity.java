package activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import utils.FileUtils;
import utils.SlideShowConst;
import utils.Utils;


public class MainActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root, SlideShowConst.MY_DIR_NAME);

    private static Context context;
    private Button picsArtGalleryButton;
    private Button customGalleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SlideShowConst.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        init();

    }

    private void init() {

        Utils.initImageLoader(getApplicationContext());
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();

        context = this;
        FileUtils.craeteDir(SlideShowConst.MY_DIR_NAME);

        picsArtGalleryButton = (Button) findViewById(R.id.pics_art_gallery_button);
        picsArtGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PicsArtGalleryActvity.class);
                startActivity(intent);
            }
        });

        customGalleryButton = (Button) findViewById(R.id.custom_gallery_button);
        customGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomGalleryActivity.class);
                startActivity(intent);
            }
        });
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.info) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("INFO");
            adb.setMessage("this is the best ( ha-ha ) slide show application in the world");
            adb.setIcon(android.R.drawable.ic_dialog_info);
            adb.setNegativeButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            adb.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
