package videoeditor.picsart.com.videoeditor;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.decoder.PhotoUtils;

import java.io.File;

import hackathon.videoeditor.utils.OnVideoActionFinishListener;
import videoeditor.picsart.com.videoeditor.text_art.SimpleTextArt;
import videoeditor.picsart.com.videoeditor.text_art.TextArtObject;


public class TextArtActivity extends ActionBarActivity implements OnVideoActionFinishListener {

    private ImageView imageView;
    private SeekBar sizeSeekBar;
    private Button saveButton;
    private Button addTextButton;
    private Button setColor;
    private TextView textView;
    private int initialColor = Color.WHITE;
    private int initialSize;

    private Intent intent;
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
        sizeSeekBar = (SeekBar) findViewById(R.id.size_seek_bar);
        saveButton = (Button) findViewById(R.id.save_button);
        setColor = (Button) findViewById(R.id.btn_color);
        addTextButton = (Button) findViewById(R.id.add_text_button);
        textView = (TextView) findViewById(R.id.text_view);
        textView.setVisibility(View.GONE);

        Bitmap bitmap = Util.readBitmapFromBufferFile(this, intent.getStringExtra("image_path"));

        imageView.setLayoutParams(new FrameLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
        imageView.setImageBitmap(bitmap);

        addTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextDialog editTextDialog = new EditTextDialog(TextArtActivity.this);
                editTextDialog.show();
                editTextDialog.setOnRadioGroupChangedListener(new EditTextDialog.OnRadioGroupChangedListener() {
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
        setColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(TextArtActivity.this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        textView.setTextColor(color);
                        initialColor = color;
                    }
                });
                colorPickerDialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Typeface typeface = Typeface.SANS_SERIF;
                if (textView.getVisibility() == View.VISIBLE && !textView.getText().toString().equals("")) {
                    SimpleTextArt addTextArt = new SimpleTextArt(TextArtActivity.this);
                    TextArtObject obj = new TextArtObject(text, (int) textView.getX(), (int) textView.getY(), initialColor, (int) textView.getTextSize(), typeface);
                    addTextArt.setOnVideoFinishListener(TextArtActivity.this);
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

        final int defaultTextSize = (int) textView.getTextSize();
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                initialSize = defaultTextSize + progress - 50;
                textView.setTextSize(initialSize);
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
            /*setResult(RESULT_OK);
            finish();*/
            Log.d("textView.getX", textView.getX() + "");
            Log.d("textView.getY", textView.getY() + "");
            Log.d("imageviewx", imageView.getX() + "");
            Log.d("size", textView.getTextSize() + "");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onFailure() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
