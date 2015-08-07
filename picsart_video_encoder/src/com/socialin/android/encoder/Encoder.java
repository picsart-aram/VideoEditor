package com.socialin.android.encoder;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;

import com.google.libvorbis.AudioFrame;
import com.google.libvorbis.VorbisEncConfig;
import com.google.libvorbis.VorbisEncoderC;
import com.google.libvorbis.VorbisException;
import com.google.libvpx.LibVpxEnc;
import com.google.libvpx.LibVpxEncConfig;
import com.google.libvpx.Rational;
import com.google.libvpx.VpxCodecCxPkt;
import com.google.libwebm.mkvmuxer.AudioTrack;
import com.google.libwebm.mkvmuxer.MkvWriter;
import com.google.libwebm.mkvmuxer.Segment;
import com.google.libwebm.mkvmuxer.SegmentInfo;
import com.google.utils.WavReader;
import com.socialin.android.encoder.factory.VideoEncoderFactory;

public class Encoder implements VideoEncoderFactory {

    private int videoHeight;
    private int videoWidth;
    private int fps;
    private File audioFile;
    private LibVpxEnc encoder;
    private LibVpxEncConfig encoderConfig;
    private Rational timeBase;
    private Rational frameRate;
    private Rational timeMultiplier;
    private File outputFile;
    private MkvWriter mkvWriter;
    private SegmentInfo muxerSegmentInfo;
    private Segment muxerSegment;
    private long newVideoTrackNumber;
    private long lastFrameTime=0;
    private StringBuilder error = new StringBuilder("Can't encode Video");
    private int framesIn;
    private long newAudioTrackNumber;
    private long fourcc = LibVpxEnc.FOURCC_ABGR;
    private VorbisEncoderC vorbisEncoder = null;
    private VorbisEncConfig vorbisConfig = null;
    private WavReader wavReader = null;
    private AudioTrack muxerTrack;
    private final int maxSamplesToRead = 1000;
    private int samplesLeft = 0;
    private ProgressDialog progressDialog = null;

    public Encoder() {
    }

    public String getCurrentTime() {
        long d = lastFrameTime / 1000000;
        long m = d / 60000;
        long s = (d - m * 60000) / 1000;
        long mil = d - m * 60000 - s * 1000;
        return m + "." + s + "." + mil;
    }

    @Override
    public void init(int videoWidth, int videoHeight, int fps, File audioFile) {

        this.videoHeight = videoHeight;
        this.videoWidth = videoWidth;
        this.fps = fps;
        this.audioFile = audioFile;
    }

    @Override
    public boolean startVideoGeneration(File outputFile) {
        try {
            framesIn = 0;
            this.outputFile = outputFile;
            encoderConfig = new LibVpxEncConfig(videoWidth, videoHeight);
            encoderConfig.setRCTargetBitrate(400000);
            encoderConfig.setTimebase(1, 1000000000);
            encoderConfig.setRCBufInitialSz(500);
            encoderConfig.setRCBufOptimalSz(600);
            encoderConfig.setRCBufSz(1000);
            encoderConfig.setLagInFrames(0);
            encoderConfig.setRCMaxQuantizer(56);
            encoderConfig.setRCMinQuantizer(4);
            encoderConfig.setRCOvershootPct(100);
            encoderConfig.setThreads(3);
            encoderConfig.setRCDropframeThresh(0);
            encoder = new LibVpxEnc(encoderConfig);
            timeBase = encoderConfig.getTimebase();
            frameRate = new Rational(fps, 1);
            timeMultiplier = timeBase.multiply(frameRate).reciprocal();
            mkvWriter = new MkvWriter();
            if (!mkvWriter.open(outputFile.toString())) {
                error.append("WebM Output name is invalid or error while opening.");
                return false;
            }

            muxerSegment = new Segment();
            if (!muxerSegment.init(mkvWriter)) {
                error.append("Could not initialize muxer segment.");
                return false;
            }

            muxerSegmentInfo = muxerSegment.getSegmentInfo();
            muxerSegmentInfo.setWritingApp("Lalalala");

            newVideoTrackNumber = muxerSegment.addVideoTrack(videoWidth, videoHeight, 0);
            if (newVideoTrackNumber == 0) {
                error.append("Could not add video track.");
                return false;
            }

            if (audioFile != null) {
                try {
                    wavReader = new WavReader(audioFile);
                } catch (Exception e) {
                    error.append("Could not create wav reader.");
                    return false;
                }

                final int channels = wavReader.nChannels();
                final int sampleRate = wavReader.nSamplesPerSec();

                try {
                    vorbisConfig = new VorbisEncConfig(channels, sampleRate, wavReader.wBitsPerSample());
                    vorbisConfig.setTimebase(1, 1000000000);

                    vorbisEncoder = new VorbisEncoderC(vorbisConfig);
                } catch (VorbisException e) {
                    error.append("Error creating Vorbis encoder. e:" + e);
                    return false;
                }

                // Add Audio Track
                newAudioTrackNumber = muxerSegment.addAudioTrack(sampleRate, channels, 0);
                if (newAudioTrackNumber == 0) {
                    error.append("Could not add audio track.");
                    return false;
                }

                muxerTrack = (AudioTrack) muxerSegment.getTrackByNumber(newAudioTrackNumber);
                if (muxerTrack == null) {
                    error.append("Could not get audio track.");
                    return false;
                }

                byte[] buffer = vorbisEncoder.CodecPrivate();
                if (buffer == null) {
                    error.append("Could not get audio private data.");
                    return false;
                }
                if (!muxerTrack.setCodecPrivate(buffer)) {
                    error.append("Could not add audio private data.");
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean endVideoGeneration() {
        if (!muxerSegment.finalizeSegment()) {
            error.append("Finalization of segment failed.");
            return false;
        }

        if (encoder != null) {
            encoder.close();
        }
        if (encoderConfig != null) {
            encoderConfig.close();
        }
        if (mkvWriter != null) {
            mkvWriter.close();
        }
        return true;
    }

    @Override
    public boolean cancelVideoGeneration() {
        endVideoGeneration();
        return outputFile.delete();
    }


    public boolean addFrame(Bitmap bitmap) {
        long frameStart = timeMultiplier.multiply(framesIn).toLong();
        long nextFrameStart = timeMultiplier.multiply(framesIn + 1).toLong();
        framesIn++;
        return addFrame(bitmap, nextFrameStart - frameStart);
    }

    @Override
    public boolean addFrame(Bitmap bitmap, long frameDurationInMilliseconds) {
        return audioFile == null ? addFrameWithouthAudio(bitmap, frameDurationInMilliseconds) : addFrameWithAudio(bitmap, frameDurationInMilliseconds);
    }

    private boolean addFrameWithouthAudio(Bitmap bitmap, long frameDurationInMilliseconds) {
        try {
            byte[] frame = bitmapToByteBuffer(bitmap);
            long frameDuration = frameDurationInMilliseconds * 1000000;
            ArrayList<VpxCodecCxPkt> encPkt = encoder.convertByteEncodeFrame(frame, lastFrameTime, frameDuration, fourcc);
            lastFrameTime += frameDuration;
            for (int i = 0; i < encPkt.size(); i++) {
                VpxCodecCxPkt pkt = encPkt.get(i);
                final boolean isKey = (pkt.flags & 0x1) == 1;

                if (!muxerSegment.addFrame(pkt.buffer, newVideoTrackNumber, pkt.pts, isKey)) {
                    error.append("Could not add frame.");
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    private boolean addFrameWithAudio(Bitmap bitmap, long frameDurationInMilliseconds) {
        try {

            AudioFrame vorbisFrame = null;
            ArrayList<VpxCodecCxPkt> encPkt = null;
            VpxCodecCxPkt pkt = null;
            int pktIndex = 0;
            boolean audioDone = false;
            boolean videoDone = false;
            boolean encoding = true;
            boolean b = false;
            while (encoding) {
                // Prime the audio encoder.
                while (vorbisFrame == null) {
                    final int samplesLeft = wavReader.samplesRemaining();
                    final int samplesToRead = Math.min(samplesLeft, maxSamplesToRead);
                    if (samplesToRead > 0) {
                        // Read raw audio data.
                        byte[] pcmArray = null;
                        try {
                            pcmArray = wavReader.readSamples(samplesToRead);
                        } catch (Exception e) {
                            error.append("Could not read audio samples.");
                            return false;
                        }

                        if (!vorbisEncoder.Encode(pcmArray)) {
                            error.append("Error encoding audio samples.");
                            return false;
                        }

                        vorbisFrame = vorbisEncoder.ReadCompressedFrame();
                    } else {
                        audioDone = true;
                        break;
                    }
                }

                if (videoDone)
                    break;

                if (encPkt == null) {
                    // Read raw video data.
                    byte[] rawVideoArray;
                    if (b) {
                        rawVideoArray = null;
                    } else {
                        rawVideoArray = bitmapToByteBuffer(bitmap);
                        b = true;
                    }
                    if (rawVideoArray != null) {
                        long frameDuration = frameDurationInMilliseconds * 1000000;
                        encPkt = encoder.convertByteEncodeFrame(rawVideoArray, lastFrameTime, frameDuration, fourcc);
                        lastFrameTime += frameDuration;
                        // Get the first vpx encoded frame.
                        pktIndex = 0;
                        pkt = encPkt.get(pktIndex++);
                    } else {
                        videoDone = true;
                    }
                }

                if (!videoDone && (audioDone || pkt.pts <= vorbisFrame.pts)) {
                    final boolean isKey = (pkt.flags & 0x1) == 1;
                    if (!muxerSegment.addFrame(pkt.buffer, newVideoTrackNumber, pkt.pts, isKey)) {
                        error.append("Could not add video frame.");
                        return false;
                    }

                    // Get the next vpx encoded frame.
                    if (pktIndex < encPkt.size()) {
                        pkt = encPkt.get(pktIndex++);
                    } else {
                        // Read the next raw video frame.
                        encPkt = null;
                    }
                } else if (!audioDone) {
                    if (!muxerSegment.addFrame(vorbisFrame.buffer, newAudioTrackNumber, vorbisFrame.pts, true)) {
                        error.append("Could not add audio frame.");
                        return false;
                    }

                    vorbisFrame = vorbisEncoder.ReadCompressedFrame();
                }
            }

        } catch (Exception e) {
            error.append("Caught error in main encode loop.");
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    private byte[] bitmapToByteBuffer(Bitmap bitmap) {

        ByteBuffer b = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(b);
        byte[] bitmapdata = b.array();

        b.clear();

        return bitmapdata;
    }

    public boolean checkAudio(File audioFile) {
        try {
            new WavReader(audioFile);
        } catch (Exception e) {
            error.append("Could not create wav reader.");
            return false;
        }
        return true;
    }

}