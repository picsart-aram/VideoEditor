package hackathon.videoeditor.utils;

import java.util.ArrayList;
import java.util.List;

import hackathon.videoeditor.video.Video;

public class VideoEditor {
    
    public static void changeSpeed(Video video, int startTime, int endTime, int percentage) {
        if (startTime > endTime || startTime < 0 || endTime > video.getVideoDuration()) {
            return;
        }

        int startFramePosition = Math.round(startTime / video.getFrameDuration());
        int endFramePosition = Math.round(endTime / video.getFrameDuration());
        int effectedFramesCount = endFramePosition - startFramePosition;
        int effectedSectionDuration = endTime - startTime;

        int newEffectedDuration = effectedSectionDuration * 100 / percentage;
        int newEffectedFramesCount = newEffectedDuration / video.getFrameDuration();
        int effectedFramesCountDif = newEffectedFramesCount - effectedFramesCount; // may be negative

        int newVideoFramesCount = video.getFramesCount() + effectedFramesCountDif;
        video.setFramesCount(newVideoFramesCount);
        
        List<Integer> newFrameRefs = new ArrayList<>(newVideoFramesCount);
        for (int i = 0; i < startFramePosition; i++) {
            newFrameRefs.add(i, video.getFrameRef(i));
        }
        for (int i = startFramePosition; i < startFramePosition + newEffectedFramesCount; i++) {
            newFrameRefs.add(i, video.getFrameRef(getPositionMapping(i, startFramePosition, endFramePosition, startFramePosition, startFramePosition + newEffectedFramesCount)));
        }
        for (int i = startFramePosition + newEffectedFramesCount; i < video.getFramesCount(); i++) {
            newFrameRefs.add(i, video.getFrameRef(i - effectedFramesCountDif));
        }
        video.setFrameRefs(newFrameRefs);
    }
    
    private static int getPositionMapping(int position, int sourceFramesStart, int sourceFramesEnd, int destFramesStart, int destFramesEnd) {
        return sourceFramesStart + Math.round((position - sourceFramesStart) * (sourceFramesEnd - sourceFramesStart) / (destFramesEnd - destFramesStart));
    }
}
