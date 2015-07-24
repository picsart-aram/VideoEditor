package videoeditor.picsart.com.videoeditor.clipart;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * Created by ani on 7/24/15.
 */
public class Clipart {

    private Bitmap bitmap;
    private int x = 0;
    private int y = 0;

    public Clipart() {

    }

    public Clipart(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
