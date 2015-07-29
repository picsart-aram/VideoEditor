package decoder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;


/**
 * Created by Tigran on 7/28/15.
 */
public class PhotoUtils {


    public static void saveBufferToSDCard(String filePath, ByteBuffer buffer) throws UnsatisfiedLinkError {

        try {
            writeToSd(filePath, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ByteBuffer readBufferFromFile(String bufferPath, int bufferSize) {
        FileInputStream inputStream = null;
        FileChannel channel = null;
        try {
            inputStream = new FileInputStream(bufferPath);

            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            channel = inputStream.getChannel();
            channel.read(buffer);

            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static void writeToSd(String path, ByteBuffer buffer) throws IOException {
        File file = new File(path);
        // Create a writable file channel
        FileChannel wChannel = new FileOutputStream(file).getChannel();

        // Write the ByteBuffer contents; the bytes between the ByteBuffer's
        // position and the limit is written to the file
        wChannel.write(buffer);

        // Close the file
        wChannel.close();
    }

    public static HashMap<Object, Object> saveBitmapBufferToSDCard(String filePath, Bitmap image) throws UnsatisfiedLinkError {
        final int width = image.getWidth();
        final int height = image.getHeight();

        ByteBuffer buffer = ByteBuffer.allocate(4 * width * height);
        image.copyPixelsToBuffer(buffer);
        buffer.position(0);
        try {
            writeToSd(filePath, buffer);
        } catch (IOException e) {

        }

        HashMap<Object, Object> bufferData = new HashMap<Object, Object>();
        bufferData.put("width", width);
        bufferData.put("height", height);
        bufferData.put("path", filePath);

        return bufferData;
    }

    public static Bitmap getRotatedBitmapAndRecycle_ARGB8(Bitmap bitmap, int rotation) {
        if (bitmap == null || bitmap.isRecycled())
            return bitmap;

        if (rotation == 0 || rotation == 360)
            return bitmap;

        Bitmap rotatedBitmap;

        Matrix matrix = new Matrix();
        matrix.reset();
        if (rotation == 90 || rotation == 270) {
            rotatedBitmap = BitmapManager.createBitmap(bitmap.getHeight(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            if (rotation == 90) {
                matrix.postRotate(90);
                matrix.postTranslate(rotatedBitmap.getWidth(), 0);
            } else { // 270
                matrix.postRotate(270);
                matrix.postTranslate(0, rotatedBitmap.getHeight());
            }
        } else {
            rotatedBitmap = BitmapManager.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            matrix.postRotate(180);
            matrix.postTranslate(rotatedBitmap.getWidth(), rotatedBitmap.getHeight());
        }
        Canvas c = new Canvas(rotatedBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        c.drawBitmap(bitmap, matrix, paint);

        BitmapManager.recycle(bitmap);

        return rotatedBitmap;

    }

    public static ByteBuffer fromBitmapToBuffer(Bitmap b) {
        ByteBuffer result = ByteBuffer.allocate(b.getWidth() * b.getHeight() * 4); // ByteBuffer.allocate(capacity)
        result.position(0);
        b.copyPixelsToBuffer(result);
        result.position(0);
        return result;
    }

    public static Bitmap fromBufferToBitmap(int w, int h, ByteBuffer buffer) {
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        buffer.rewind();
        result.copyPixelsFromBuffer(buffer);
        return result;
    }

    public static int checkBufferSize(String videoPath, VideoDecoder.FrameSize frameSize) {

        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        int height = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int width = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int newHeight = height / (frameSize.ordinal() + 1);
        int newWidth = width / (frameSize.ordinal() + 1);
        return (newHeight * newWidth * 4);
    }

}
