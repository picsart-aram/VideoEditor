package utils;

import android.os.Environment;

/**
 * Created by Tigran on 8/4/15.
 */
public class SlideShowConst {

    public static int FRAME_SIZE_720 = 720;

    public static int OUTPUT_FPS = 15;

    public static String APP_TITLE = "PicsArtVideo";

    public static String SHARED_PREFERENCES = "pics_art_video";

    public static String MY_DIR_NAME = "req_images";

    public static String FILE_PREFIX = "file://";

    public static String ROOT_DIR = Environment.getExternalStorageDirectory().toString();

    public static String IS_FILE = "isfile";

    public static String IS_EDITED = "isEdited";

    public static String IS_OPEN = "isopen";

    public static String IMAGE_PATHS = "image_paths";

    public static String NO_IMAGE_SELECTED = "no images selected";

    public static String WEB_IMAGE_SIZE_240 = "?r240x240";

    public static String WEB_IMAGE_SIZE_1024 = "?r1024x1024";

    public static String USER_ID = "me";

    public static String MY_JSON_FILE_NAME = "myfile.json";

    public static String EDITED_COUNT = "edited_count";

    public static String OUTPUT_VIDEO_DIR = ROOT_DIR + "/vid1.mp4";

}
