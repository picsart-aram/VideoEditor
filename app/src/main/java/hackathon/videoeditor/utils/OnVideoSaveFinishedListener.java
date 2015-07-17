package hackathon.videoeditor.utils;

/**
 * Created by AramNazaryan on 7/10/15.
 */
public interface OnVideoSaveFinishedListener {

    void onSaveSuccess(String path);

    void onSaveFailed();
}
