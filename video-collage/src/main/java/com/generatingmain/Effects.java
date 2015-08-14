package com.generatingmain;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Arman on 2/3/15.
 */
public class Effects {
    public enum EFFECT{FADE,SlIDE,ROTATE}

    private static Effects efectInstance;
    private  EFFECT effect;
    private  DisplayMetrics dm;




    private Effects(){

    }
    private Effects(EFFECT eftype){
    this.effect = eftype;
    }

    public static Effects builder(EFFECT effectType){

        synchronized (Effects.class){

                efectInstance = new Effects(effectType);

        }
        return efectInstance;
    }

    public Effects setParams(DisplayMetrics dm){
        efectInstance.dm=dm;
        return  efectInstance;
    }



   public void generateFrames(Bitmap source, int counter) {


       int width = source.getWidth();
       int height = source.getHeight();
       Bitmap transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
       FileOutputStream out = null;

       for (int i = 0; i < 25; i++) {
           Canvas canvas = new Canvas(transBitmap);
           canvas.drawRGB(0, 0, 0);
           final Paint paint = new Paint();
           Matrix mx = new Matrix();
           ///choosing effect
           if (efectInstance.effect == EFFECT.FADE) {
               paint.setAlpha((i + 1) * 4);
               canvas.drawBitmap(source, 0, 0, paint);
           } else if (efectInstance.effect == EFFECT.SlIDE) {
               canvas.drawBitmap(source, width - i * (height / 12), 0, paint);

           } else if (efectInstance.effect == EFFECT.ROTATE) {
               mx.postRotate(90 - i * 4);
               mx.postTranslate(150 + width - i * (width / 12 + 50), 0);
               canvas.concat(mx);
               canvas.drawBitmap(source, 0, 0, paint);

           }
           /// starting encode frame
           // se.encodeNativeFrameForPartialEffect(BitmapUtil.fromBitmap(transBitmap));
           out = null;
           try {
               File filename = new File(Environment.getExternalStorageDirectory().getPath() + "/req_images/ts" + counter + "/image_" + String.format("%05d", i) + ".jpg");
               out = new FileOutputStream(filename);
               transBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

           } catch (Exception e) {
               e.printStackTrace();
           } finally {
               try {
                   if (out != null) {
                       out.close();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }


       }
   }






}
