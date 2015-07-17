package hackathon.videoeditor.video;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;

public class VideoMetaData {
    
    private String fileName;
    private String filePath;
    private Point resolution;
    private int duration;
    private int fps;
    private int rotation;

    private MediaMetadataRetriever retriever;
    
    public VideoMetaData(String filePath) {
        this.filePath = filePath;
        this.fileName = filePath.substring(filePath.lastIndexOf("/"));
    }

    public Bitmap getFrameAtTime(long time) {
        if (retriever == null) {
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(filePath);
        }
        return retriever.getFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
    }
    
    public int getFrameDuration() {
        return 1000 / fps;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public Point getResolution() {
        return resolution;
    }

    public void setResolution(Point resolution) {
        this.resolution = resolution;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
