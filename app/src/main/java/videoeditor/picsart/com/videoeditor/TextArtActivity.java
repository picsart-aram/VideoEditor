package videoeditor.picsart.com.videoeditor;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import videoeditor.picsart.com.videoeditor.text_art.SimpleTextArt;
import videoeditor.picsart.com.videoeditor.text_art.TextArtObject;


public class TextArtActivity extends ActionBarActivity {

    private ImageView imageView;
    private SeekBar colorSeekBar;
    private SeekBar sizeSeekBar;
    private Button saveButton;
    private Button addTextButton;
    private TextView textView;

    private Intent intent;
    int color = 0;
    String text = "gaga";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_art);

        init();

    }

    public void init() {

        intent = getIntent();

        imageView = (ImageView) findViewById(R.id.image_view);
        colorSeekBar = (SeekBar) findViewById(R.id.color_seek_bar);
        sizeSeekBar = (SeekBar) findViewById(R.id.size_seek_bar);
        saveButton = (Button) findViewById(R.id.save_button);
        addTextButton = (Button) findViewById(R.id.add_text_button);
        textView = (TextView) findViewById(R.id.text_view);
        textView.setVisibility(View.GONE);

        imageView.setImageBitmap(ImageLoader.getInstance().loadImageSync("file://" + intent.getStringExtra("image_path")));

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDialod editTextDialod = new EditTextDialod(TextArtActivity.this);
                editTextDialod.show();
                editTextDialod.setOnRadioGroupChangedListener(new EditTextDialod.OnRadioGroupChangedListener() {
                    @Override
                    public void onRadioGroupChanged(String s) {

                        if (!s.equals("")) {
                            text = s;
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(text);
                        }
                    }
                });
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textView.getVisibility() == View.VISIBLE && !textView.getText().toString().equals("")) {
                    SimpleTextArt addTextArt = new SimpleTextArt(TextArtActivity.this);
                    TextArtObject obj = new TextArtObject(text, 10, 30, Color.RED);
                    addTextArt.startAction(new File(Environment.getExternalStorageDirectory(), "test_images").getPath(), obj);
                }

            }
        });

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.startDrag(ClipData.newPlainText("", ""), new View.DragShadowBuilder(textView), null, 0);
                return false;
            }
        });

        imageView.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {

                final int action = event.getAction();
                switch (action) {

                    case DragEvent.ACTION_DRAG_STARTED:

                        Log.d("gagagagaga", event.getX() + "");
                        Log.d("gagagagaga", event.getY() + "");

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

        final int x = (int) textView.getTextSize();
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setTextSize(x + progress - 50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setTextColor(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_text_art, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            setResult(RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
