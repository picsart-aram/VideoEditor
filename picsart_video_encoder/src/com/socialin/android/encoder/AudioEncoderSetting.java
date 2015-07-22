package com.socialin.android.encoder;

public class AudioEncoderSetting {
    // Default audio encoder settings.
    public static String mAduioFilePath = null;
    // Rate control values. Set the min and max values to -1 to
    // encode at an average bitrate. Use the same value for minimum, average,
    // and maximum to encode at a constant bitrate. Values are in kilobits.
    public static int mAudioBitrate = 64000;
    public static int mMinAudioBitRate = -1;
    public static int mMaxAudioBitrate = -1;
}