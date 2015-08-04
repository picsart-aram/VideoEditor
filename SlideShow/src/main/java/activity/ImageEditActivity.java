package activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import dialog.EditTextDialod;
import dialog.ImageScaleDialog;


public class ImageEditActivity extends ActionBarActivity {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/req_images");
    public static final String EDITED_IMAGE_PATH = "edited_image_path";
    public static final String INDEX = "index";
    public static final String IMAGE_PATH = "image_path";
    public static final String FILE_PREFIX = "file://";

    private ImageView editedImageView;
    private TextView textView;
    private Button rotateButton;
    private Button addStickerButton;
    private Button addTextButton;
    private Intent intent;
    private String fileName;
    private int count = 0;
    private String path;

    private int stickerIndex;
    private boolean isFile = false;

    private ImageScaleDialog imageScaleDialog;
    private EditTextDialod editTextDialod;
    private FragmentManager fragmentManager = getFragmentManager();
    private RecyclerViewFragment multiSelectFragment = new RecyclerViewFragment();
    private boolean fragmentIsOpen = false;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        init();

    }

    private void init() {

        context = this;
        SharedPreferences sharedPreferences = getSharedPreferences("pics_art_video", MODE_PRIVATE);
        count = sharedPreferences.getInt("edited_count", 0);
        intent = getIntent();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int width = displaymetrics.widthPixels;

        editedImageView = (ImageView) findViewById(R.id.edited_image_view);
        editedImageView.buildDrawingCache();
        textView = (TextView) findViewById(R.id.text_view);
        rotateButton = (Button) findViewById(R.id.rotate_button);
        addStickerButton = (Button) findViewById(R.id.add_sticker_button);
        addTextButton = (Button) findViewById(R.id.add_text_button);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, width);
        editedImageView.setLayoutParams(layoutParams);

        isFile = intent.getBooleanExtra("isfile", false);

        Bitmap imageBitmap;
        path = intent.getStringExtra(IMAGE_PATH);
        if (isFile) {
            imageBitmap = ImageLoader.getInstance().loadImageSync(FILE_PREFIX + path);
        } else {
            imageBitmap = ImageLoader.getInstance().loadImageSync(path);
        }

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                textView.startDrag(ClipData.newPlainText("", ""), new View.DragShadowBuilder(textView), null, 0);
                return false;
            }
        });

        Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);

        if (imageBitmap.getHeight() > imageBitmap.getWidth()) {

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, (imageBitmap.getWidth() * width) / (imageBitmap.getHeight()), width, false);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(imageBitmap, (width - imageBitmap.getWidth()) / 2, 0, null);

        } else {

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width, (imageBitmap.getHeight() * width) / (imageBitmap.getWidth()), false);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(imageBitmap, 0, (width - imageBitmap.getHeight()) / 2, null);

        }

        editedImageView.setImageBitmap(bitmap);

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bitmap bitmap = ((BitmapDrawable) editedImageView.getDrawable()).getBitmap();
                imageScaleDialog = new ImageScaleDialog(ImageEditActivity.getContext(), path, width, isFile, bitmap);
                imageScaleDialog.show();
                imageScaleDialog.setOnShapeChangedListener(new ImageScaleDialog.OnShapeChangedListener() {
                    @Override
                    public void onShapeChanged(boolean saved, String path1) {

                        isFile = true;
                        ImageLoader.getInstance().clearMemoryCache();
                        ImageLoader.getInstance().clearDiskCache();
                        ImageLoader.getInstance().displayImage(FILE_PREFIX + path1
                                , editedImageView, new SimpleImageLoadingListener());
                        path = path1;

                    }
                });
            }
        });

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (textView.getText().toString().equals("")) {
                    editTextDialod = new EditTextDialod(ImageEditActivity.getContext());
                    editTextDialod.show();
                    editTextDialod.setOnRadioGroupChangedListener(new EditTextDialod.OnRadioGroupChangedListener() {
                        @Override
                        public void onRadioGroupChanged(int shapeIndex, int colorIndex, String s) {

                            if (!s.equals("")) {
                                switch (shapeIndex) {
                                    case 0:
                                        textView.setTextSize(20);
                                        break;
                                    case 1:
                                        textView.setTextSize(30);
                                        break;
                                    case 2:
                                        textView.setTextSize(50);
                                        break;
                                    default:
                                        break;
                                }
                                switch (colorIndex) {
                                    case 0:
                                        textView.setTextColor(Color.parseColor("#0192E6"));
                                        break;
                                    case 1:
                                        textView.setTextColor(Color.RED);
                                        break;
                                    case 2:
                                        textView.setTextColor(Color.GREEN);
                                        break;
                                    default:
                                        break;
                                }
                                textView.setText(s);
                                textView.setVisibility(View.VISIBLE);
                            } else {
                                textView.setText("");
                                textView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        addStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fragmentIsOpen == false) {

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_out, R.anim.slide_out);
                    fragmentTransaction.add(R.id.frame_layout, multiSelectFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    fragmentIsOpen = true;

                    ArrayList<Drawable> bitmaps = new ArrayList<>();
                    bitmaps.add(getResources().getDrawable(R.drawable.sticker1));
                    bitmaps.add(getResources().getDrawable(R.drawable.sticker2));
                    bitmaps.add(getResources().getDrawable(R.drawable.sticker3));

                    multiSelectFragment.setAdapter(bitmaps);

                    editedImageView.setOnTouchListener(myTouchListener);

                } else {

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_in);
                    fragmentTransaction.remove(multiSelectFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    fragmentIsOpen = false;

                    editedImageView.setOnTouchListener(null);

                }
            }
        });

        editedImageView.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {

                final int action = event.getAction();
                switch (action) {

                    case DragEvent.ACTION_DRAG_STARTED:
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;

                    case DragEvent.ACTION_DROP:
                        textView.setX(event.getX() - textView.getWidth() / 2);
                        textView.setY(event.getY() - textView.getHeight() / 2);
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        multiSelectFragment.setOnShapeChangedListener(new RecyclerViewFragment.OnStickerChangedListener() {
            @Override
            public void onStickerChanged(int shapeIndex) {

                stickerIndex = shapeIndex;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_in);
                fragmentTransaction.remove(multiSelectFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fragmentIsOpen = false;

                editedImageView.setOnTouchListener(myTouchListener);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextDialod.show();
            }
        });

    }

    public static Context getContext() {
        return context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            saveImage();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void saveImage() {

        new Thread(new Runnable() {

            public void run() {

                Looper.prepare();
                fileName = "image_edit_" + String.format("%03d", count) + ".jpg";
                count++;

                SharedPreferences sharedPreferences = getSharedPreferences("pics_art_video", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("edited_count", count);
                editor.commit();

                File file = new File(myDir, fileName);


                Bitmap bitmap = ((BitmapDrawable) editedImageView.getDrawable()).getBitmap();
                Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                if (textView.getVisibility() == View.VISIBLE && !textView.getText().toString().equals("")) {

                    textView.setDrawingCacheEnabled(true);
                    Bitmap b = textView.getDrawingCache();
                    Canvas canvas = new Canvas(mutableBitmap);
                    canvas.drawBitmap(b, textView.getX(), textView.getY(), null);
                }

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent data = new Intent();
                data.putExtra(EDITED_IMAGE_PATH, file.getAbsolutePath());
                data.putExtra(INDEX, intent.getIntExtra(INDEX, -1));
                if (intent.getBooleanExtra("isEdited", false) == true) {
                    new File(path).delete();
                }
                setResult(RESULT_OK, data);
                File file1 = new File(myDir, "image_scaled.jpg");
                file1.delete();
                finish();
            }
        }
        ).start();
        Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
    }

    View.OnTouchListener myTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            Bitmap bitmap = ((BitmapDrawable) editedImageView.getDrawable()).getBitmap();
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap stickerBitmap = null;
            switch (stickerIndex) {

                case 0:
                    stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker1, options);
                    break;

                case 1:
                    stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker2, options);
                    break;

                case 2:
                    stickerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sticker3, options);
                    break;
                default:
                    break;
            }

            canvas.drawBitmap(stickerBitmap, null, new Rect((int) motionEvent.getX() - getContext().getResources().getInteger(R.integer.size), (int) motionEvent.getY() - getContext().getResources().getInteger(R.integer.size),
                    (int) motionEvent.getX() + getContext().getResources().getInteger(R.integer.size), (int) motionEvent.getY() + getContext().getResources().getInteger(R.integer.size)), null);

            editedImageView.setImageBitmap(mutableBitmap);
            editedImageView.setOnTouchListener(null);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_in);
            fragmentTransaction.remove(multiSelectFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            fragmentIsOpen = false;

            return false;
        }
    };

}
