package videoeditor.picsart.com.videoeditor.effects.Utils;

import videoeditor.picsart.com.videoeditor.BaseActionParameter;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class BoostEffectParams extends BaseActionParameter{

    public static final int TYPE_RED = 1;
    public static final int TYPE_GREEN = 2;
    public static final int TYPE_BLUE = 3;

    public int percentage = 100;
    public int type = TYPE_RED;

    public BoostEffectParams(int percentage, int type) {
        this.percentage = percentage;
        this.type = type;
    }

}
