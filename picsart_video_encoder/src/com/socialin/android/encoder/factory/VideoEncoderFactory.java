package com.socialin.android.encoder.factory;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by margaritmirzoyan on 8/6/14.
 */
public interface VideoEncoderFactory {

    public void init(int videoWidth, int videoHeight, int fps, File audioFile);

    public boolean startVideoGeneration(File outputFile);

    public boolean cancelVideoGeneration();

    public boolean endVideoGeneration() ;

    public boolean addFrame(Bitmap bitmap, long frameDurationInMilliseconds);
}
