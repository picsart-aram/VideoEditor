package videoeditor.picsart.com.videoeditor.effects;

import android.app.Activity;
import android.graphics.Bitmap;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;
import videoeditor.picsart.com.videoeditor.effects.Utils.ConvolutionMatrix;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class EngraveEffect extends BaseVideoAction<Void> {

    public EngraveEffect(Activity activity, Void... params) {
        super(activity, params);
    }

    @Override
    protected Bitmap doActionOnBitmap(Bitmap bmp, Void... params) {
        return engrave(bmp);
    }

    public static Bitmap engrave(Bitmap src) {
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.setAll(0);
        convMatrix.Matrix[0][0] = -2;
        convMatrix.Matrix[1][1] = 2;
        convMatrix.Factor = 1;
        convMatrix.Offset = 95;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }
}
