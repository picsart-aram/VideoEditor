package videoeditor.picsart.com.videoeditor.effects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;
import videoeditor.picsart.com.videoeditor.VideoActionInterface;

/**
 * Created by AramNazaryan on 7/17/15.
 */
public class GrayScaleEffect extends BaseVideoAction<Void> {

    public GrayScaleEffect(Activity activity, Void... params) {
        super(activity, params);
    }

//    @Override
//    public void doActionOnVideo(String[] bitmapPaths) {
//        for (int i = 0; i < bitmapPaths.length; i++) {
//            Bitmap bitmap = BitmapFactory.decodeFile(bitmapPaths[i]);
//            Bitmap grayScaleBitmap = toGrayscale(bitmap);
//
//        }
//    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    @Override
    protected Bitmap doActionOnBitmap(Bitmap bmp, Void... params) {
        return toGrayscale(bmp);
    }

}
