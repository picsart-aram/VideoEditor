package hackathon.videoeditor.greenScreenBlending;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.nio.ByteBuffer;

public class EffectUtils {
    
//    public static Bitmap blendGreenScreen2(Bitmap bmpBg, Bitmap bmpFg) {
//        ByteBuffer outBuffer = allocNativeBuffer(bmpFg.getWidth() * bmpFg.getHeight() * 4);
//        bmpFg.copyPixelsToBuffer(outBuffer);
//        outBuffer.position(0);
//        generateColorDifferentMask(outBuffer, 10f, bmpBg.getWidth(), bmpBg.getHeight());
//        outBuffer.position(0);
//        bmpFg.copyPixelsFromBuffer(outBuffer);
//
//        Bitmap bitmap = Bitmap.createBitmap(bmpFg.getWidth(), bmpFg.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawBitmap(bmpBg, 0, 0, new Paint());
//        canvas.drawBitmap(bmpFg, 0, 0, new Paint());
//        return bitmap;
//    }

    public static Bitmap blendGreenScreen(Bitmap bmpBg, Bitmap bmpFg) {

        int defRed;
        int defGreen;
        int defBlue;
        int bgWidth = bmpBg.getWidth();
        int bgHeight = bmpBg.getHeight();
        int fgWidth = bmpFg.getWidth();
        int fgHeight = bmpFg.getHeight();
        Paint paint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(bmpFg.getWidth(), bmpFg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int[] pixels = new int[fgHeight * fgWidth];
        int[] bgPixels = new int[bgHeight * bgWidth];
        bmpFg.getPixels(pixels, 0, fgWidth, 0, 0, fgWidth, fgHeight);
        bmpBg.getPixels(bgPixels, 0, bgWidth, 0, 0, bgWidth, bgHeight);
        defRed = Color.red(pixels[0]);
        defGreen = Color.green(pixels[0]);
        defBlue = Color.blue(pixels[0]);

//        float[] defLab = rgbToLab(defRed, defGreen, defBlue);
        double defHue = getHue(defRed, defGreen, defBlue);

        for (int i = 0; i < pixels.length; i++) {
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);

//            float[] curLab = rgbToLab(r, g, b);


            int row = i / fgWidth;
            int cell = i % fgWidth;

            int bgIndex = row * bgWidth + cell;

//            if (Math.abs(r - defRed) < distance && Math.abs(g - defGreen) < distance && Math.abs(b - defBlue) < distance) {
//            if(Math.pow(defLab[0] - curLab[0], 2) + Math.pow(defLab[1] - curLab[1], 2) + Math.pow(defLab[2] - curLab[2], 2) < 50000){
//            if (Math.pow(defRed - r, 2) + Math.pow(defGreen - g, 2) + Math.pow(defBlue - b, 2) < distance) {
            if (Math.abs(getHue(r, g, b) - defHue) < 2) {
                int bgRed = 0;
                int bgGreen = 0;
                int bgBlue = 0;
                if (bgIndex < bgPixels.length) {
                    bgRed = Color.red(bgPixels[bgIndex]);
                    bgGreen = Color.green(bgPixels[bgIndex]);
                    bgBlue = Color.blue(bgPixels[bgIndex]);
                }
                pixels[i] = getIntFromColor(Math.max(bgRed + r - defRed, 0), Math.max(bgGreen + g - defGreen, 0), Math.max(bgBlue + b - defBlue, 0));

            }
        }

        Bitmap newBitmap = Bitmap.createBitmap(pixels, fgWidth, fgHeight, Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(newBitmap, 0, 0, paint);

        return bitmap;
    }

    private static int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    private static double[] rgbToXYZ(int r, int g, int b) {
        double var_R = (r / 255f);        //R from 0 to 255
        double var_G = (g / 255f);      //G from 0 to 255
        double var_B = (b / 255f);        //B from 0 to 255

        if (var_R > 0.04045) {
            var_R = Math.pow(((var_R + 0.055) / 1.055), 2.4);
        } else {
            var_R = var_R / 12.92;
        }
        if (var_G > 0.04045) {
            var_G = Math.pow(((var_G + 0.055) / 1.055), 2.4);
        } else {
            var_G = var_G / 12.92;
        }
        if (var_B > 0.04045) {
            var_B = Math.pow(((var_B + 0.055) / 1.055), 2.4);
        } else {
            var_B = var_B / 12.92;
        }

        var_R = var_R * 100;
        var_G = var_G * 100;
        var_B = var_B * 100;

//Observer. = 2°, Illuminant = D65
        double[] result = new double[3];
        result[0] = var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805;
        result[1] = var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722;
        result[2] = var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505;
        return result;
    }

    private static final double N = 4.0 / 29.0;

    private static double[] xyzToLab(double x, double y, double z) {
//        double ref_X =  95.047;
//        double ref_Y = 100.000;
//        double ref_Z = 108.883;
//        double var_X = x / ref_X;          //ref_X =  95.047   Observer= 2°, Illuminant= D65
//        double var_Y = y / ref_Y;          //ref_Y = 100.000
//        double var_Z = z / ref_Z;          //ref_Z = 108.883
//
//        if ( var_X > 0.008856 ) {
//            var_X = Math.pow(var_X , ( 1d/3d ));
//        } else{
//            var_X = ( 7.787 * var_X ) + ( 16 / 116 );
//        }
//        if ( var_Y > 0.008856 ){
//            var_Y = Math.pow(var_Y , ( 1/3 ));
//        } else {
//            var_Y = ( 7.787 * var_Y ) + ( 16 / 116 );
//        }
//        if ( var_Z > 0.008856 ){
//            var_Z = Math.pow(var_Z , ( 1/3 ));
//        } else{
//            var_Z = ( 7.787 * var_Z ) + ( 16 / 116 );
//        }
//
//        double[] result = new double[3];
//        result[0] = ( 116 * var_Y ) - 16;
//        result[1] = 500 * ( var_X - var_Y );
//        result[2] = 200 * ( var_Y - var_Z );
//        return result;
        double l = f(y);
        double L = 116.0 * l - 16.0;
        double a = 500.0 * (f(x) - l);
        double b = 200.0 * (l - f(z));
        return new double[] {L, a, b};
    }

    private static double[] rgbToLab(int r, int g, int b) {
        double[] xyz = rgbToXYZ(r, g, b);
        return xyzToLab(xyz[0], xyz[1], xyz[2]);
    }

    private static double f(double x) {
        if (x > 216.0 / 24389.0) {
            return Math.cbrt(x);
        } else {
            return (841.0 / 108.0) * x + N;
        }
    }

    private static double getHue(int r, int g, int b) {
        double[] lab = rgbToLab(r, g, b);
        double var_H = Math.atan2(lab[2], lab[1]);

        if (var_H > 0) {
            var_H = (var_H / Math.PI) * 180;
        } else {
            var_H = 360 - (Math.abs(var_H) / Math.PI) * 180;
        }

        return var_H;
    }

//    /** Allocation ByteBuffer in native code (memory will not be counted in VM heap). */
//    static public native ByteBuffer allocNativeBuffer(long size);
//    /** Free ByteBuffer allocated in native code. */
//    static public native ByteBuffer freeNativeBuffer(ByteBuffer buffer);
//
//    static public native ByteBuffer allocateLCHFloatBuffer(int width, int height);
//    static public native void freeLCHFloatBuffer(ByteBuffer buffer, int width, int height);
//    static public native void generateColorDifferentMask(ByteBuffer maskBuffer,float tollerance,int width,int height);
//    
//    static {
//        System.loadLibrary("imageop");
//    }
}
