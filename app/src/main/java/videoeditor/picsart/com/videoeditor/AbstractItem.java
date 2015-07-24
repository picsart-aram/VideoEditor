package videoeditor.picsart.com.videoeditor;

import android.content.Context;
import android.graphics.Path;

/**
 * Created by ani on 7/24/15.
 */
public class AbstractItem {
    public float curWidth = 0f;
    public float curHeight = 0f;

    public float centerX = 0;
    public float centerY = 0;

    protected float imageZoom = 1f;
    protected float imageLeft = 0f;
    protected float imageTop = 0f;

    protected float rotateDegree = 0f;

    public float X = 10f;
    public float Y = 10f;

    protected float scaleX = 1f;
    protected float scaleY = 1f;

    protected boolean isActive = true;
    protected boolean isDrawHandle = false;

    protected int opacity = 255;

    public float getWidth() {
        return curWidth;
    }

    public float getHeight() {
        return curHeight;
    }

    public float getImageZoom() {
        return imageZoom;
    }

    public float getImageLeft() {
        return imageLeft;
    }

    public float getImageTop() {
        return imageTop;
    }

    public float getRotation() {
        return rotateDegree;
    }

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public int getOpacity() {
        return opacity;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDrawHandle() {
        return isDrawHandle;
    }

    public void setDrawHandle(boolean drawHandle) {
        isDrawHandle = drawHandle;
    }

    public void clearData() {
    }

    public void initSpecStateObjects(Context context) {
    }

    protected class DrawPath {
        public Path path;
        public boolean clear;
        public Transform transform;
        public float brushSize;
        public float brushHardness;

        public DrawPath(Path path, Transform transform, boolean clear, float brushSize, float brushHardness) {
            this.path = path;
            this.transform = transform;
            this.clear = clear;
            this.brushSize = brushSize;
            this.brushHardness = brushHardness;
        }
    }

    protected class Transform {
        public float sx = 1f;
        public float sy = 1f;

        public Transform(float sx, float sy) {
            this.sx = sx;
            this.sy = sy;
        }
    }
}
