package item;

/**
 * Created by Tigran Isajnayan on 4/10/15.
 */
public class SlideShowItem {


    private String path;
    private boolean isEdited;
    private boolean isFromFileSystem;

    public SlideShowItem() {
        this.path = null;
        this.isEdited = false;
        this.isFromFileSystem = false;
    }

    public SlideShowItem(String path, boolean isEdited, boolean isFromFileSystem) {
        this.path = path;
        this.isEdited = isEdited;
        this.isFromFileSystem = isFromFileSystem;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public boolean isFromFileSystem() {
        return isFromFileSystem;
    }

    public void setIsFromFileSystem(boolean isFromFileSystem) {
        this.isFromFileSystem = isFromFileSystem;
    }

}
