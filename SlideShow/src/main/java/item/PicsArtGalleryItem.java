package item;

import java.io.Serializable;

/**
 * Created by Tigran on 4/28/15.
 */

public class PicsArtGalleryItem implements Serializable {

    private String imagePath;
    private Integer height;
    private Boolean isLoaded;
    private Boolean isSeleted;
    private Integer width;

    public PicsArtGalleryItem() {
        this.imagePath = null;
        this.width = 0;
        this.height = 0;
        this.isSeleted = false;
        this.isLoaded = false;
    }

    public PicsArtGalleryItem(String imagePath, int width, int height, boolean isSeleted, boolean isLoaded) {
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        this.isSeleted = isSeleted;
        this.isLoaded = isLoaded;

    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Boolean getIsLoaded() {
        return isLoaded;
    }

    public void setIsLoaded(Boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public Boolean getIsSeleted() {
        return isSeleted;
    }

    public void setIsSeleted(Boolean isSeleted) {
        this.isSeleted = isSeleted;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

}
