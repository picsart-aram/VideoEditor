package dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.edmodo.cropper.CropImageView;
import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.FileUtils;

/**
 * Created by Tigran Isajanyan on 6/9/15.
 */
public class ImageScaleDialog extends Dialog {

    private CropImageView cropImageView;
    private Button saveButton;
    private Button cancelButton;
    private Button rotateButton;
    private ToggleButton fixedSizeButton;
    private Context context;
    boolean isFixed = false;

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/req_images");

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int ROTATE_NINETY_DEGREES = 90;

    private OnShapeChangedListener onShapeChangedListener;

    public ImageScaleDialog(Context context, final String path, final int width, boolean isFile, Bitmap b) {
        super(context, R.style.Base_Theme_AppCompat_Dialog);

        this.context = context;
        setContentView(R.layout.image_scale_dialog);

        cropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        saveButton = (Button) findViewById(R.id.save_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        rotateButton = (Button) findViewById(R.id.roate_button);
        fixedSizeButton = (ToggleButton) findViewById(R.id.fixed_size_button);

        Bitmap bitmap;
        if (isFile) {
            bitmap = ImageLoader.getInstance().loadImageSync("file://" + path);
        } else {
            bitmap = ImageLoader.getInstance().loadImageSync(path);
        }
        cropImageView.setImageBitmap(b);
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(myDir, "image_scaled.jpg");
                Bitmap bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.WHITE);

                Bitmap bitmap1 = cropImageView.getCroppedImage();
                if (bitmap1.getHeight() > bitmap1.getWidth()) {

                    bitmap1 = Bitmap.createScaledBitmap(bitmap1, (bitmap1.getWidth() * width) / (bitmap1.getHeight()), width, false);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawBitmap(bitmap1, (width - bitmap1.getWidth()) / 2, 0, null);

                } else {

                    bitmap1 = Bitmap.createScaledBitmap(bitmap1, width, (bitmap1.getHeight() * width) / (bitmap1.getWidth()), false);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawBitmap(bitmap1, 0, (width - bitmap1.getHeight()) / 2, null);

                }
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                onShapeChangedListener.onShapeChanged(true, file.toString());
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });

        fixedSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFixed == false) {

                    cropImageView.setFixedAspectRatio(true);
                    isFixed = true;

                } else {

                    cropImageView.setFixedAspectRatio(false);
                    isFixed = false;

                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setOnShapeChangedListener(OnShapeChangedListener l) {
        onShapeChangedListener = l;
    }

    public interface OnShapeChangedListener {
        void onShapeChanged(boolean saved, String text);
    }

}

