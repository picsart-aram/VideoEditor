package videoeditor.picsart.com.videoeditor.clipart;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;

/**
 * Created by ani on 7/24/15.
 */
public class ClipArtAction extends BaseVideoAction<Clipart> {


    public ClipArtAction(Activity activity, Clipart... params) {
        super(activity, params);
    }

    @Override
    protected Bitmap doActionOnBitmap(Bitmap bmp, Clipart... params) {
        int width, height;
        height = bmp.getHeight();
        width = bmp.getWidth();
        Bitmap baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(baseBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        c.drawBitmap(bmp, 0, 0, paint);
        c.drawBitmap(params[0].getBitmap(), params[0].getX(), params[0].getY(), paint);
        return baseBitmap;
    }
}
