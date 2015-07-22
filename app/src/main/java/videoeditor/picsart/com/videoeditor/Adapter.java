package videoeditor.picsart.com.videoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Tigran on 6/23/15.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public static final String FILE_PREFIX = "file://";
    private ArrayList<String> array;
    private Context context;

    public Adapter(ArrayList<String> arr, Context c) {

        array = arr;
        context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {

            ImageLoader.getInstance().displayImage(FILE_PREFIX + array.get(position)
                    , holder.icon, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.icon.setImageBitmap(null);
                    super.onLoadingStarted(imageUri, view);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                    holder.icon.setImageBitmap(loadedImage);
                    super.onLoadingComplete(imageUri, view, loadedImage);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.image_item);

        }
    }

    public String getItem(int i) {
        return array.get(i);
    }

}
