package videoeditor.picsart.com.videoeditor.decoder;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Tigran on 7/20/15.
 */
public class VideoDecoder {

    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/test_images");

    private String inputFilePath = "";
    private String outputDirectory;

    private FrameSize frameSize;

    private ArrayList<String> savedFramePath = new ArrayList<>();
    private static OnDecodeFinishedListener onDecodeFinishedListener;

    public VideoDecoder(String inputFilePath, FrameSize frameSize, String outputDirectory) {

        this.inputFilePath = inputFilePath;
        this.frameSize = frameSize;
        this.outputDirectory = outputDirectory;

    }

    /**
     * extracting frames from video
     */
    public void extractVideoFrames() {
        new ExtractFrames().execute();
    }

    /**
     * Call after extractVideoFrames()
     * returns all saved frames absolut paths
     *
     * @return
     */
    public ArrayList<String> getSavedFrames() {
        return savedFramePath;
    }

    /**
     * Call after extractVideoFrames() , returns saved frames absolut paths from - to
     *
     * @return
     */
    private ArrayList<String> getSavedFramesFromTo(int offset, int limit) {
        ArrayList<String> subArray = new ArrayList<>();
        int a = savedFramePath.size();
        if (offset + limit < a)
            a = offset + limit;
        for (int i = offset; i < a; i++) {
            subArray.add(savedFramePath.get(i));
        }
        return subArray;
    }

    public void setOnDecodeFinishedListener(OnDecodeFinishedListener l) {
        onDecodeFinishedListener = l;
    }

    private class ExtractFrames extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            ExtractMpegFrames extractMpegFrames = new ExtractMpegFrames();
            try {
                extractMpegFrames.extractMpegFrames(inputFilePath, frameSize.ordinal()+1, outputDirectory);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            File[] files = new File(outputDirectory).listFiles();

            for (int i = 0; i < files.length; i++) {
                savedFramePath.add(files[i].getAbsolutePath());
            }
            onDecodeFinishedListener.onFinish(true);

        }
    }

    public interface OnDecodeFinishedListener {

        void onFinish(boolean isDone);

    }

    public enum FrameSize {
        ORIGINAL,
        NORMAL,
        SMALL
    }

}
