package item;

public class CustomGalleryItem {

    private String imagePath;
    private boolean isSeleted;
    private int width;
    private int height;

    public CustomGalleryItem() {

        this.imagePath = null;
        this.isSeleted = false;
        this.width = 0;
        this.height = 0;

    }

    public CustomGalleryItem(String imagePath, boolean isSeleted, int width, int height) {

        this.imagePath = imagePath;
        this.isSeleted = isSeleted;
        this.width = width;
        this.height = height;

    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setIsSeleted(boolean isSeleted) {
        this.isSeleted = isSeleted;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
