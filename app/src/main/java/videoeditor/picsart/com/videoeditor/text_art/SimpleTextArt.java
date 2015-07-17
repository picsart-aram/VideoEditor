package videoeditor.picsart.com.videoeditor.text_art;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

    @Override
    protected Bitmap doActionOnBitmap(Bitmap bmp, TextArtObject... params) {
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect bounds = new Rect();
        paint.getTextBounds(params[0].text, 0, params[0].text.length(), bounds);
        canvas.drawText(params[0].text, params[0].x, params[0].y, paint);
        return bmp;
    }
}
