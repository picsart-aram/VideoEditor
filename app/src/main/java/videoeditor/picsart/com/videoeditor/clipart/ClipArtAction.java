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
    protected Bitmap doActionOnBitmap(Bitmap videoFrameBitmap, Clipart... params) {
        int width, height;
        height = videoFrameBitmap.getHeight();
        width = videoFrameBitmap.getWidth();

        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();

        canvas.drawBitmap(videoFrameBitmap, 0, 0, paint);
//        int centerX = width / 2;
//        int centerY = height / 2;
//        canvas.save();
//        canvas.scale(0.5F / ExtractMpegFrames.SCALE_FACTOR, 0.5F / ExtractMpegFrames.SCALE_FACTOR, centerX, centerY);
//        canvas.translate(0, 0);
//        canvas.drawBitmap(params[0].getBitmap(), centerX / 2, centerY / 2, paint);
//        canvas.restore();


//        canvas.save();
//        canvas.scale(1/ExtractMpegFrames.SCALE_FACTOR, 1/ExtractMpegFrames.SCALE_FACTOR, width / 2, height/2);
//        canvas.translate(0, 0);
        canvas.drawBitmap(params[0].getBitmap(), params[0].getX(), params[0].getY(), paint);
//        canvas.restore();

        return resultBitmap;
    }
}
