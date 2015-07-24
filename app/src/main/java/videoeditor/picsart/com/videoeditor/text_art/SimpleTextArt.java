package videoeditor.picsart.com.videoeditor.text_art;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;

/**
 * Created by intern on 7/17/15.
 */
public class SimpleTextArt extends BaseVideoAction<TextArtObject> {

    public SimpleTextArt(Activity activity, TextArtObject... params) {
        super(activity, params);
    }

    public Bitmap addText(Bitmap bmpOriginal, TextArtObject... params) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        paint.setColor(params[0].getColor());
        paint.setTextSize(params[0].getTextSize());
        paint.setTypeface(params[0].getTypeFace());
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        c.drawText(params[0].getText(), params[0].getX(), params[0].getY(), paint);
        return bmpGrayscale;
    }

    @Override
    protected Bitmap doActionOnBitmap(Bitmap bmp, TextArtObject... params) {
        return addText(bmp, params);
    }

}
