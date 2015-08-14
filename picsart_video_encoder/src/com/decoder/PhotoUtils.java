package com.decoder;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


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
//            ByteBuffer buffer = ImageOp.allocNativeBuffer(bufferSize);

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

    public static ByteBuffer fromBitmapToBuffer(Bitmap b) {
        ByteBuffer result = ByteBuffer.allocate(b.getWidth() * b.getHeight() * 4); // ByteBuffer.allocate(capacity)
        result.position(0);
        b.copyPixelsToBuffer(result);
        result.position(0);
        return result;
    }

    //gago xi ches push linum
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

    public static int checkFrameWidth(String videoPath, VideoDecoder.FrameSize frameSize) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        int height = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int width = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int orientation = checkFrameOrientation(videoPath);
        int x;
        if (orientation == 90 || orientation == 270) {
            x = height < width ? height : width;
        } else {
            x = height > width ? height : width;
        }
        return x / (frameSize.ordinal() + 1);
    }

    public static int checkFrameHeight(String videoPath, VideoDecoder.FrameSize frameSize) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        int height = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        int width = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int orientation = checkFrameOrientation(videoPath);
        int x;
        if (orientation == 90 || orientation == 270) {
            x = height > width ? height : width;
        } else {
            x = height < width ? height : width;
        }
        return x / (frameSize.ordinal() + 1);
    }

    public static int checkFrameOrientation(String videoPath) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoPath);
        String orientation = metaRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

        return Integer.parseInt(orientation);
    }

}

