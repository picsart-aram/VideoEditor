package videoeditor.picsart.com.videoeditor.clipart;

import android.app.Activity;
import android.graphics.Bitmap;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;
import videoeditor.picsart.com.videoeditor.effects.Utils.ConvolutionMatrix;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class EmbossEffect extends BaseVideoAction<Void> {

    public EmbossEffect(Activity activity, Void... params) {
        super(activity, params);
    }

    @Override
    protected Bitmap doActionOnBitmap(Bitmap bmp, Void... params) {
        return emboss(bmp);
    }

    public static Bitmap emboss(Bitmap src) {
        double[][] EmbossConfig = new double[][] {
                { -1 ,  0, -1 },
                {  0 ,  4,  0 },
                { -1 ,  0, -1 }
        };
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.applyConfig(EmbossConfig);
        convMatrix.Factor = 1;
        convMatrix.Offset = 127;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }
}
