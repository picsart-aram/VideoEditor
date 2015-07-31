package videoeditor.picsart.com.videoeditor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import hackathon.videoeditor.framegrabber.util.ImageOpCommon;
import hackathon.videoeditor.utils.OnVideoActionFinishListener;
import hackathon.videoeditor.utils.RecyclerItemClickListener;
import videoeditor.picsart.com.videoeditor.clipart.EmbossEffect;
import videoeditor.picsart.com.videoeditor.effects.BoostEffect;
import videoeditor.picsart.com.videoeditor.effects.EffectSelectorAdapter;
import videoeditor.picsart.com.videoeditor.effects.EngraveEffect;
import videoeditor.picsart.com.videoeditor.effects.GrayScaleEffect;
import videoeditor.picsart.com.videoeditor.effects.ReflectionEffect;
import videoeditor.picsart.com.videoeditor.effects.SnowEffect;
import videoeditor.picsart.com.videoeditor.effects.Utils.EffectsItem;
import videoeditor.picsart.com.videoeditor.effects.Utils.OnEffectApplyFinishedListener;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class VideoEffectsActivity extends AppCompatActivity {

    private ImageView previewImageView = null;
    private RecyclerView effectsList = null;
    private EffectSelectorAdapter adapter = null;
    private String framePath;
    private String folderPath;
    private EffectsItem selectedItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.effects_activity_layout);

        Intent intent = getIntent();
        framePath = intent.getStringExtra("image_path");
        folderPath = intent.getStringExtra("folder_path");
        System.out.println("framePath = "+framePath);
        previewImageView = (ImageView) findViewById(R.id.frame_image_imageView);
        ImageLoader.getInstance().displayImage("file://" + framePath, previewImageView);
        effectsList = (RecyclerView) findViewById(R.id.effects_list_recyclerview);
        effectsList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new EffectSelectorAdapter();
        initEffects(adapter);
        effectsList.setAdapter(adapter);


        effectsList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedItem = adapter.getItem(position);
                selectedItem.getAction().applyOnOneFrame(selectedItem.getPath(), new OnEffectApplyFinishedListener() {
                    @Override
                    public void onFinish(Bitmap bmp) {
                        previewImageView.setImageBitmap(bmp);
                    }
                }, null);
            }
        }));

    }

    private void initEffects(EffectSelectorAdapter adapter) {
        EffectsItem item1 = new EffectsItem(framePath, new GrayScaleEffect(VideoEffectsActivity.this));
        EffectsItem item2 = new EffectsItem(framePath, new EngraveEffect(VideoEffectsActivity.this));
        EffectsItem item3 = new EffectsItem(framePath, new EmbossEffect(VideoEffectsActivity.this));
        EffectsItem item4 = new EffectsItem(framePath, new ReflectionEffect(VideoEffectsActivity.this));
        EffectsItem item5 = new EffectsItem(framePath, new SnowEffect(VideoEffectsActivity.this));
        EffectsItem item6 = new EffectsItem(framePath, new BoostEffect(VideoEffectsActivity.this));
        adapter.addItem(item1);
        adapter.addItem(item2);
        adapter.addItem(item3);
        adapter.addItem(item4);
        adapter.addItem(item5);
        adapter.addItem(item6);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.effects_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_apply) {
            applyEffectOnVideo();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void applyEffectOnVideo() {
        selectedItem.getAction().setOnVideoFinishListener(new OnVideoActionFinishListener() {
            @Override
            public void onSuccess() {
                finish();
            }

            @Override
            public void onFailure() {

            }
        });
        selectedItem.getAction().startAction(folderPath, null);
    }
}
