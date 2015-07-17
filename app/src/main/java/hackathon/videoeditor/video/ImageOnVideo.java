package hackathon.videoeditor.video;

import android.graphics.Bitmap;

public class ImageOnVideo extends Video {

    private Bitmap image;
    private int x;
    private int y;

    public ImageOnVideo(VideoMetaData metaData, Bitmap image) {
        super(metaData);
        this.image = image;
    }

    public ImageOnVideo(Video video, Bitmap image) {
        super(video.getVideoMetaData());
        setFilePath(video.getFilePath());
        setAudioFilePath(video.getAudioFilePath());
        setFps(video.getFps());
        setFramesCount(video.getFramesCount());
        setFrameRefs(video.getFrameRefs());
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
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
