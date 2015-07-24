package videoeditor.picsart.com.videoeditor.effects.Utils;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class EffectsItem {
    private String path;
    private BaseVideoAction action;

    public EffectsItem(String path, BaseVideoAction action) {
        this.path = path;
        this.action = action;
    }

    public BaseVideoAction getAction() {
        return action;
    }

    public String getPath() {
        return path;
    }

}
