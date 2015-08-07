package activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.intern.picsartvideo.R;
import com.picsart.api.LoginManager;
import com.picsart.api.Photo;
import com.picsart.api.PicsArtConst;
import com.picsart.api.RequestListener;
import com.picsart.api.UserController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapter.PicsArtGalleryAdapter;
import item.PicsArtGalleryItem;
import utils.FileUtils;
import utils.SlideShowConst;
import utils.SpacesItemDecoration;

public class PicsArtGalleryActvity extends ActionBarActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;
    private PicsArtGalleryAdapter picsArtGalleryAdapter;

    private ArrayList<Photo> photos = new ArrayList<>();
    private ArrayList<PicsArtGalleryItem> picsArtGalleryItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics_art_gallery_actvity);

        init();

        SharedPreferences sharedPreferences = this.getSharedPreferences(SlideShowConst.SHARED_PREFERENCES, MODE_PRIVATE);
        if (!LoginManager.getInstance().hasValidSession(this)) {
            LoginManager.getInstance().openSession(PicsArtGalleryActvity.this, new RequestListener(0) {
                @Override
                public void onRequestReady(int reqnumber, String message) {
                    if (reqnumber == 7777) {
                        /*Toast.makeText(PicsArtGalleryActvity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        RequestQueue queue = Volley.newRequestQueue(PicsArtGalleryActvity.this);
                        String url = "https://api.picsart.com/users/me/photos?token=" + LoginManager.getAccessToken();

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        JSONArray jsonArray = null;
                                        try {
                                            jsonArray = new JSONArray(response);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = jsonArray.getJSONObject(i);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                //Log.d("gaga", jsonObject.getString("url") + "");
                                                PicsArtGalleryItem picsArtGalleryItem = new PicsArtGalleryItem(jsonObject.getString("url"),
                                                        jsonObject.getInt("width"), jsonObject.getInt("height"), false, false);
                                                picsArtGalleryItems.add(picsArtGalleryItem);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        FileUtils.writeListToJson(PicsArtGalleryActvity.this, picsArtGalleryItems, "myfile.json");
                                        picsArtGalleryAdapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                finish();
                            }
                        });
                        queue.add(stringRequest);*/
                        final UserController userController = new UserController(LoginManager.getAccessToken(), PicsArtGalleryActvity.this);
                        userController.requestUserPhotos("me", 0, UserController.MAX_LIMIT);
                        userController.setListener(new RequestListener(0) {
                            @Override
                            public void onRequestReady(int i, String s) {
                                photos = userController.getPhotos();
                                for (int j = 0; j < photos.size(); j++) {
                                    PicsArtGalleryItem picsArtGalleryItem = new PicsArtGalleryItem(photos.get(j).getUrl(),
                                            photos.get(j).getWidth(), photos.get(j).getHeight(), false, false);
                                    picsArtGalleryItems.add(picsArtGalleryItem);

                                }
                                FileUtils.writeListToJson(PicsArtGalleryActvity.this, picsArtGalleryItems, "myfile.json");
                                picsArtGalleryAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                    }
                }
            });
        } else {
            if (sharedPreferences.getBoolean("isopen", false) == false) {
                /*RequestQueue queue = Volley.newRequestQueue(PicsArtGalleryActvity.this);
                String url = "https://api.picsart.com/users/me/photos?token=" + LoginManager.getAccessToken();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray(response);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = jsonArray.getJSONObject(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        //Log.d("gaga", jsonObject.getString("url") + "");
                                        PicsArtGalleryItem picsArtGalleryItem = new PicsArtGalleryItem(jsonObject.getString("url"),
                                                jsonObject.getInt("width"), jsonObject.getInt("height"), false, false);
                                        picsArtGalleryItems.add(picsArtGalleryItem);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                FileUtils.writeListToJson(PicsArtGalleryActvity.this, picsArtGalleryItems, "myfile.json");
                                picsArtGalleryAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finish();
                    }
                });
                queue.add(stringRequest);*/
                final UserController userController = new UserController(LoginManager.getAccessToken(), PicsArtGalleryActvity.this);
                userController.requestUserPhotos("me", 0, UserController.MAX_LIMIT);
                userController.setListener(new RequestListener(0) {
                    @Override
                    public void onRequestReady(int i, String s) {
                        photos = userController.getPhotos();
                        for (int j = 0; j < photos.size(); j++) {
                            PicsArtGalleryItem picsArtGalleryItem = new PicsArtGalleryItem(photos.get(j).getUrl(),
                                    photos.get(j).getWidth(), photos.get(j).getHeight(), false, false);
                            picsArtGalleryItems.add(picsArtGalleryItem);

                        }
                        FileUtils.writeListToJson(PicsArtGalleryActvity.this, picsArtGalleryItems, "myfile.json");
                        picsArtGalleryAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                    }
                });
            } else {

                FileUtils.readListFromJson(PicsArtGalleryActvity.this, picsArtGalleryItems, "myfile.json");
                picsArtGalleryAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        }


    }


    /*@Override
    protected void onResume() {
        super.onResume();
        SharedPreferences SHARED_PREFERENCES = this.getSharedPreferences("pics_art_video", MODE_PRIVATE);
        if (!LoginManager.getInstance().hasValidSession(this)) {
            LoginManager.getInstance().openSession(PicsArtGalleryActvity.this, new RequestListener(0) {
                @Override
                public void onRequestReady(int reqnumber, String message) {
                    if (reqnumber == 7777) {
                        Toast.makeText(PicsArtGalleryActvity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        final UserController userController = new UserController(LoginManager.getAccessToken(), PicsArtGalleryActvity.this);
                        userController.requestUserPhotos("me", 0, UserController.MAX_LIMIT);
                        userController.setListener(new RequestListener(0) {
                            @Override
                            public void onRequestReady(int i, String s) {
                                photos = userController.getPhotos();
                                for (int j = 0; j < photos.size(); j++) {
                                    PicsArtGalleryItem picsArtGalleryItem = new PicsArtGalleryItem(photos.get(j).getUrl(),
                                            photos.get(j).getWidth(), photos.get(j).getHeight(), false, false);
                                    picsArtGalleryItems.add(picsArtGalleryItem);

                                }
                                FileUtils.writeListToJson(PicsArtGalleryActvity.this, picsArtGalleryItems, "myfile.json");
                                picsArtGalleryAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                            }
                        });
                    }
                }
            });
        } else {
            if (SHARED_PREFERENCES.getBoolean("isopen", false) == false) {
                final UserController userController = new UserController(LoginManager.getAccessToken(), PicsArtGalleryActvity.this);
                userController.requestUserPhotos("me", 0, UserController.MAX_LIMIT);
                userController.setListener(new RequestListener(0) {
                    @Override
                    public void onRequestReady(int i, String s) {
                        photos = userController.getPhotos();
                        for (int j = 0; j < photos.size(); j++) {
                            PicsArtGalleryItem picsArtGalleryItem = new PicsArtGalleryItem(photos.get(j).getUrl(),
                                    photos.get(j).getWidth(), photos.get(j).getHeight(), false, false);
                            picsArtGalleryItems.add(picsArtGalleryItem);

                        }
                        FileUtils.writeListToJson(PicsArtGalleryActvity.this, picsArtGalleryItems, "myfile.json");
                        picsArtGalleryAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);

                    }
                });
            } else {

                FileUtils.readListFromJson(PicsArtGalleryActvity.this, picsArtGalleryItems, "myfile.json");
                picsArtGalleryAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        }
    }*/

    private void init() {

        PicsArtConst.CLIENT_ID = "ZetOmniaexo1SNtY52usPTry";
        PicsArtConst.CLIENT_SECRET = "yY2fEJU8R9rFmuwtOZRQhm4ZK2Kdwqhk";
        PicsArtConst.REDIRECT_URI = "localhost";
        PicsArtConst.GRANT_TYPE = "authorization_code";

        getSupportActionBar().setTitle("PicsArtVideo");

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.pics_art_rec_view);

        picsArtGalleryAdapter = new PicsArtGalleryAdapter(picsArtGalleryItems, this, width, getSupportActionBar());
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setHasFixedSize(true);
        recyclerView.setClipToPadding(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(picsArtGalleryAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pics_art_gallery_actvity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (picsArtGalleryAdapter.getSelected().size() < 1) {
                Toast.makeText(getApplicationContext(), "no images selected", Toast.LENGTH_LONG).show();
            } else {

                SharedPreferences sharedPreferences = this.getSharedPreferences("pics_art_video", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("custom_gallery_isopen", false) == true || sharedPreferences.getBoolean("pics_art_gallery_isopen", false) == true) {
                    Intent data = new Intent().putExtra("image_paths", picsArtGalleryAdapter.getSelected());
                    setResult(RESULT_OK, data);
                } else {
                    Intent intent = new Intent(PicsArtGalleryActvity.this, SlideShowActivity.class);
                    intent.putCharSequenceArrayListExtra("image_paths", picsArtGalleryAdapter.getSelected());
                    startActivity(intent);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("pics_art_gallery_isopen", true);
                    editor.commit();
                }
                finish();
            }

            return true;
        }
        if (id == R.id.logout) {
            LoginManager.getInstance().closeSession(PicsArtGalleryActvity.this);
            Toast.makeText(getApplicationContext(), "logged out", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(SlideShowConst.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isopen", true);
        editor.commit();
        super.onDestroy();
    }

}
