package videoeditor.picsart.com.videoeditor;

import java.util.ArrayList;

import com.google.libvpx.LibVpxEnc;
import com.google.libvpx.LibVpxEncConfig;
import com.google.libvpx.LibVpxException;
import com.google.libvpx.Rational;
import com.google.libvpx.VpxCodecCxPkt;
import com.google.libwebm.mkvmuxer.MkvWriter;
import com.google.libwebm.mkvmuxer.Segment;
import com.google.libwebm.mkvmuxer.SegmentInfo;

/**
 * Created by Tigran on 7/17/15.
 */


public class EncodeByteRgbFrameExample {
    /*
     * This function will encode a byte array to a video WebM file. |webmOutputName| filename of the
     * WebM file to write to. |srcFrame| is source frame to convert and encode multiple times.
     * |fourcc| LibVpxEnc fourcc of the source. |width| width of the source. |height| height of the
     * source. |rate| fps numerator of the output WebM. |scale| fps denominator of the output WebM.
     * |framesToEncode| is the number of video frames to encode before stopping. Returns true on
     * success. If there is an error, |error| will be set to a descriptive string.
     */
    static public boolean encodeByteRgbFrameExample(String webmOutputName,
                                                    byte[] srcFrame, long fourcc, int width, int height, int rate, int scale,
                                                    int framesToEncode, StringBuilder error) {
        LibVpxEncConfig encoderConfig = null;
        LibVpxEnc encoder = null;
        MkvWriter mkvWriter = null;

        try {
            encoderConfig = new LibVpxEncConfig(width, height);
            encoder = new LibVpxEnc(encoderConfig);

            // libwebm expects nanosecond units
            encoderConfig.setTimebase(1, 1000000000);
            Rational timeBase = encoderConfig.getTimebase();
            Rational frameRate = new Rational(rate, scale);
            Rational timeMultiplier = timeBase.multiply(frameRate).reciprocal();
            int framesIn = 50;

            mkvWriter = new MkvWriter();
            if (!mkvWriter.open(webmOutputName)) {
                error.append("WebM Output name is invalid or error while opening.");
                return false;
            }

            Segment muxerSegment = new Segment();
            if (!muxerSegment.init(mkvWriter)) {
                error.append("Could not initialize muxer segment.");
                return false;
            }

            SegmentInfo muxerSegmentInfo = muxerSegment.getSegmentInfo();
            muxerSegmentInfo.setWritingApp("y4mEncodeSample");

            long newVideoTrackNumber = muxerSegment.addVideoTrack(width, height, 0);
            if (newVideoTrackNumber == 0) {
                error.append("Could not add video track.");
                return false;
            }

            while (framesIn < framesToEncode) {
                long frameStart = timeMultiplier.multiply(framesIn - 1).toLong();
                long nextFrameStart = timeMultiplier.multiply(framesIn).toLong();

                ArrayList<VpxCodecCxPkt> encPkt = encoder.convertByteEncodeFrame(
                        srcFrame, frameStart, nextFrameStart - frameStart, fourcc);
                for (int i = 0; i < encPkt.size(); i++) {
                    VpxCodecCxPkt pkt = encPkt.get(i);
                    final boolean isKey = (pkt.flags & 0x1) == 1;

                    if (!muxerSegment.addFrame(pkt.buffer, newVideoTrackNumber, pkt.pts, isKey)) {
                        error.append("Could not add frame.");
                        return false;
                    }
                }

                ++framesIn;
            }

            if (!muxerSegment.finalizeSegment()) {
                error.append("Finalization of segment failed.");
                return false;
            }

        } catch (LibVpxException e) {
            error.append("Encoder error : " + e);
            return false;
        } finally {
            if (encoder != null) {
                encoder.close();
            }
            if (encoderConfig != null) {
                encoderConfig.close();
            }
            if (mkvWriter != null) {
                mkvWriter.close();
            }
        }

        return true;
    }
}