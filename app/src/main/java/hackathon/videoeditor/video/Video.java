package hackathon.videoeditor.video;

import java.util.ArrayList;
import java.util.List;

public class Video {
    
    private int fps;
    private int framesCount;
    private List<Integer> frameRefs;
    private String filePath;
    private String audioFilePath;
    private VideoMetaData videoMetaData;
    
    public Video(VideoMetaData metaData) {
        this.videoMetaData = metaData;
        fps = metaData.getFps();
        framesCount = metaData.getDuration() * fps;
        frameRefs = new ArrayList<>(framesCount);
        for (int i = 0; i < framesCount; i++) {
            frameRefs.add(i, i * metaData.getFrameDuration());
        }
    }

//    public Video(Video video) {
//        this.videoMetaData = video.getVideoMetaData();
//        filePath = video.getFilePath();
//        audioFilePath = video.getAudioFilePath();
//        fps = videoMetaData.getFps();
//        framesCount = videoMetaData.getDuration() * fps;
//        frameRefs = video.getFrameRefs();
//    }
    
    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }
    
    public int getFramesCount() {
        return framesCount;
    }
    
    public void setFramesCount(int framesCount) {
        this.framesCount = framesCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public VideoMetaData getVideoMetaData() {
        return videoMetaData;
    }

    public int getFrameDuration() {
        return 1000 / fps;
    }

    public int getVideoDuration() {
        return getFrameDuration() * framesCount;
    }

    public List<Integer> getFrameRefs() {
        return frameRefs;
    }

    public void setFrameRefs(List<Integer> frameRefs) {
        this.frameRefs = frameRefs;
    }

    public Integer getFrameRef(int framePosition) {
        if (framePosition < frameRefs.size()) {
            return frameRefs.get(framePosition);
        } else {
            return null;
        }
    }

    public void setFrameRef(int framePosition, int frameRef) {
        if (framePosition < frameRefs.size()) {
            frameRefs.set(framePosition, frameRef);
        }
    }
}
