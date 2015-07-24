package activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.intern.picsartvideo.R;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import adapter.CustomGalleryAdapter;
import item.CustomGalleryItem;
import utils.SpacesItemDecoration;
import utils.Utils;

public class CustomGalleryActivity extends ActionBarActivity {

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private ProgressBar progressBar;

    private CustomGalleryAdapter customGalleryAdapter;
    private ArrayList<CustomGalleryItem> customGalleryArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_gallery);

        init();

    }

    private void init() {

        getSupportActionBar().setTitle("PicsArtVideo");
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        customGalleryAdapter = new CustomGalleryAdapter(customGalleryArrayList, this, getSupportActionBar());

        recyclerView = (RecyclerView) findViewById(R.id.gallery_rec_view);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        recyclerView.setAdapter(customGalleryAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(CustomGalleryActivity.this,"Gorisi tti arax",Toast.LENGTH_LONG).show();
                recyclerView.smoothScrollToPosition(0);
            }
        });

        new MyTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (customGalleryAdapter.getSelected().size() < 1) {
                Toast.makeText(getApplicationContext(), "no images selected", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sharedPreferences = this.getSharedPreferences("pics_art_video", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("custom_gallery_isopen", false) == true || sharedPreferences.getBoolean("pics_art_gallery_isopen", false) == true) {

                    Intent data = new Intent().putExtra("image_paths", customGalleryAdapter.getSelected());
                    setResult(RESULT_OK, data);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("custom_gallery_isopen", true);
                    editor.commit();

                } else {

                    Intent intent = new Intent(CustomGalleryActivity.this, SlideShowActivity.class);
                    intent.putCharSequenceArrayListExtra("image_paths", customGalleryAdapter.getSelected());
                    intent.putExtra("isfile", true);
                    startActivity(intent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("custom_gallery_isopen", true);
                    editor.commit();

                }
                finish();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            customGalleryArrayList.addAll(Utils.getGalleryPhotos(CustomGalleryActivity.this));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            customGalleryAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

}
