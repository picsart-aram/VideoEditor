package videoeditor.picsart.com.videoeditor.effects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class SnowEffect extends BaseVideoAction<Void> {

    public static final int COLOR_MAX = 2000;

    public SnowEffect(Activity activity, Void... params) {
        super(activity, params);
    }

    @Override
    protected Bitmap doActionOnBitmap(Bitmap bmp, Void... params) {
        bmp = applySnowEffect(bmp);
        return bmp;
    }

    public static Bitmap applySnowEffect(Bitmap source) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // random object
        Random random = new Random();

        int R, G, B, index = 0, thresHold = 50;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get color
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);
                // generate threshold
                thresHold = random.nextInt(COLOR_MAX);
                if(R > thresHold && G > thresHold && B > thresHold) {
                    pixels[index] = Color.rgb(255, 255, 255);
                }
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }
}
