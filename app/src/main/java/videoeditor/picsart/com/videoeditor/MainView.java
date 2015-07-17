package videoeditor.picsart.com.videoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class MainView extends View {

    private Bitmap origBitmap;
    private Bitmap clipartBitmap;
    private RectF onDrawRect = new RectF();
    private RectF savedRect = new RectF();
    private RectF bitmapRect = new RectF();
    private Canvas savedCanvas;
    private Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    private float scaleFactor = 1f;
    public float currentZoom = 1f;

    private int currentWidth = 0;
    private int currentHeight = 0;

    private int viewWidth = 0;
    private int viewHeight = 0;
    private boolean isDefaluts = true;

    public int origWidth = 0;
    public int origHeight = 0;

    public int scaledWidth = 0;
    public int scaledHeight = 0;

    public int left = 0;
    public int top = 0;


    private int imgWidth;
    private int imgHeight;

    private final Queue<Runnable> sizeChangedActioQueue = new LinkedList<Runnable>();

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, String path) {
        super(context);

        if (!TextUtils.isEmpty(path)) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                if (origBitmap != null) {
                    origBitmap.recycle();
                    origBitmap = null;
                }

                Bitmap tempBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (tempBitmap != null) {
                    origBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }

                if (origBitmap != null) {
                    this.imgWidth = origBitmap.getWidth();
                    this.imgHeight = origBitmap.getHeight();
                    System.out.println("MainView::  bitmap= " + origBitmap.getWidth() + "x" + origBitmap.getHeight());
                }

                if (tempBitmap != null && !tempBitmap.isRecycled()) {
                    tempBitmap.recycle();
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;

        float oldScaleFactor = scaleFactor;
        int oldLeft = left;
        int oldTop = top;

        initView(isDefaluts, w, h);

        sizeChanged(w, h, oldScaleFactor, oldLeft, oldTop);
        isDefaluts = false;
        while (!sizeChangedActioQueue.isEmpty()) {
            sizeChangedActioQueue.poll().run();
        }
    }

    public void sizeChanged(int viewWidth, int viewHeight, float oldScaleFactor, int oldLeft, int oldTop) {
        initImageData();
        //clipart size changed
        invalidate();
    }

    private void initImageData() {
        int oldW = currentWidth;
        int oldH = currentHeight;

        currentWidth = (int) (scaledWidth * currentZoom);
        currentHeight = (int) (scaledHeight * currentZoom);

        left = left + (oldW - currentWidth) / 2;
        top = top + (oldH - currentHeight) / 2;

        onDrawRect.set(left, top, currentWidth + left, currentHeight + top);
    }


    private void initView(boolean initDefaluts, int viewWidth, int viewHeight) {
        if (origBitmap == null) {
            if (getContext() instanceof ClipartActivity) {
                ((ClipartActivity) getContext()).finish();
            }
            return;
        }

        origWidth = origBitmap.getWidth();
        origHeight = origBitmap.getHeight();

        scaleFactor = Math.min((float) viewWidth / (float) origWidth, (float) viewHeight / (float) origHeight);

        scaledWidth = (int) (origWidth * scaleFactor);
        scaledHeight = (int) (origHeight * scaleFactor);
        if (initDefaluts) {
            initDefaultProperties();
        } else {
            initProperties();
        }

        savedCanvas = new Canvas(origBitmap);
        bitmapRect.set(0, 0, origWidth, origHeight);
    }

    public void initDefaultProperties() {
        currentZoom = 1f;

        currentWidth = scaledWidth;
        currentHeight = scaledHeight;

        left = (viewWidth - scaledWidth) / 2;
        top = (viewHeight - scaledHeight) / 2;

        onDrawRect.set(left, top, scaledWidth + left, scaledHeight + top);
        savedRect.set(onDrawRect);
        invalidate();
    }

    private void initProperties() {
        currentWidth = scaledWidth;
        currentHeight = scaledHeight;

        left = (viewWidth - scaledWidth) / 2;
        top = (viewHeight - scaledHeight) / 2;

        onDrawRect.set(left, top, scaledWidth + left, scaledHeight + top);
        savedRect.set(onDrawRect);
        invalidate();
    }

    public void addClipart(int clipartResId) {
        clipartBitmap = BitmapFactory.decodeResource(getContext().getResources(), clipartResId);
        invalidate();
    }

    public void saveItemsToBitmap() {
        //TODO
        if(savedCanvas != null){
           drawClipart(savedCanvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (origBitmap == null || origBitmap.isRecycled()) {
            return;
        }
        canvas.drawBitmap(origBitmap, null, onDrawRect, bitmapPaint);
        drawClipart(canvas);
    }

    private void drawClipart(Canvas canvas) {
        if (clipartBitmap != null && !clipartBitmap.isRecycled()) {

            canvas.save();
            canvas.scale(0.5f, 0.5f, onDrawRect.centerX(), onDrawRect.centerY());
            canvas.translate(0, 0);
            canvas.drawBitmap(clipartBitmap, onDrawRect.centerX() / 2, onDrawRect.centerY() / 2, bitmapPaint);
            canvas.restore();
        }
    }
}
