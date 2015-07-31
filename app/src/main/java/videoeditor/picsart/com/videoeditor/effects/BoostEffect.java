package videoeditor.picsart.com.videoeditor.effects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class BoostEffect extends BaseVideoAction<Void> {


    public BoostEffect(Activity activity, Void... params) {
        super(activity, params);
    }

    @Override
    protected Bitmap doActionOnBitmap(Bitmap bmp, Void... params) {
        return boost(bmp, 2, 20);
    }

    public static Bitmap boost(Bitmap src, int type, float percent) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        int A, R, G, B;
        int pixel;

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                if(type == 1) {
                    R = (int)(R * (1 + percent));
                    if(R > 255) R = 255;
                }
                else if(type == 2) {
                    G = (int)(G * (1 + percent));
                    if(G > 255) G = 255;
                }
                else if(type == 3) {
                    B = (int)(B * (1 + percent));
                    if(B > 255) B = 255;
                }
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }
}
