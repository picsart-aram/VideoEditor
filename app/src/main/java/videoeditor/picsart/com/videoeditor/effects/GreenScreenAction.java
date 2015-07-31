package videoeditor.picsart.com.videoeditor.effects;

import android.app.Activity;
import android.graphics.Bitmap;

import hackathon.videoeditor.greenScreenBlending.EffectUtils;
import videoeditor.picsart.com.videoeditor.BaseVideoAction;

/**
 * Created by AramNazaryan on 7/31/15.
 */
public class GreenScreenAction extends BaseVideoAction<Bitmap>{

    public GreenScreenAction(Activity activity, Bitmap... params) {
        super(activity, params);
    }

    @Override
    protected Bitmap doActionOnBitmap(Bitmap videoFrameBitmap, Bitmap... bg) {
        return EffectUtils.blendGreenScreen(bg[0], videoFrameBitmap);
    }
}
