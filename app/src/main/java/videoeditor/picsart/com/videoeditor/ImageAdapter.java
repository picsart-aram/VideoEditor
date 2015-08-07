package videoeditor.picsart.com.videoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by Lilit on 8/7/15.
 */
public class ImageAdapter extends ArrayAdapter<String> {
    private Context context;
    private LayoutInflater inflater;

    public ImageAdapter(Context context, int resource) {
        super(context, 0);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = convertView;

        if (convertView == null) {
            root = inflater.inflate(R.layout.item, null);
        }

        ImageView image = (ImageView) root
                .findViewById(R.id.image_item);

        Bitmap bitmap = Util.readBitmapFromBufferFile(context, getItem(position));
        image.setImageBitmap(bitmap);

        return root;
    }

}
