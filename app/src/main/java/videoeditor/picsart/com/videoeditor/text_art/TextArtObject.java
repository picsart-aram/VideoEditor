package videoeditor.picsart.com.videoeditor.text_art;

import android.graphics.Color;

/**
 * Created by intern on 7/17/15.
 */
public class TextArtObject {

    private String text = null;
    private int x = 0;
    private int y = 0;
    private int color;

    public TextArtObject(String text,int x,int y,int color){
        this.text=text;
        this.x=x;
        this.y=y;
        this.color=color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
