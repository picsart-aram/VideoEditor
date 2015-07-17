package hackathon.videoeditor.video;

import android.graphics.Bitmap;

public class GreenScreenVideo extends Video {
    
    private Bitmap background;
    
    public GreenScreenVideo(VideoMetaData metaData, Bitmap background) {
        super(metaData);
        this.background = background;
    }

    public GreenScreenVideo(Video video, Bitmap background) {
        super(video.getVideoMetaData());
        setFilePath(video.getFilePath());
        setAudioFilePath(video.getAudioFilePath());
        setFps(video.getFps());
        setFramesCount(video.getFramesCount());
        setFrameRefs(video.getFrameRefs());
        this.background = background;
    }
    
    public Bitmap getBackground() {
        return background;
    }
}
