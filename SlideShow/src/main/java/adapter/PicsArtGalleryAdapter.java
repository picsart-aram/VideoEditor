package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import item.PicsArtGalleryItem;

import com.example.intern.picsartvideo.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * Created by Tigran Isajanyan on 4/28/15.
 */
public class PicsArtGalleryAdapter extends RecyclerView.Adapter<PicsArtGalleryAdapter.ViewHolder> {

    private ArrayList<PicsArtGalleryItem> array;
    private Context context;

    private int displayWidth = 0;
    private int width = 0;
    private int height = 0;

    private ActionBar actionBar;


    public PicsArtGalleryAdapter(ArrayList<PicsArtGalleryItem> arr, Context c, int w, ActionBar actionBar) {

        this.actionBar=actionBar;
        displayWidth = w;
        array = arr;
        context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (array.get(position).getIsLoaded() == true) {
                    if (array.get(position).getIsSeleted()) {
                        array.get(position).setIsSeleted(false);
                        actionBar.setTitle(getSelected().size() + " Selected");
                        if (getSelected().size() == 0) {
                            actionBar.setTitle("PicsArtVideo");
                        }
                    } else {
                        array.get(position).setIsSeleted(true);
                        actionBar.setTitle(getSelected().size() + " Selected");
                    }

                    holder.select.setSelected(array
                            .get(position).getIsSeleted());
                }
            }
        });

        try {

            width = (displayWidth / 3);
            height = (array.get(position).getHeight() * width) / ((array.get(position).getWidth()));

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
            holder.icon.setLayoutParams(layoutParams);

            ImageLoader.getInstance().displayImage(array.get(position).getImagePath() + "?r240x240"
                    , holder.icon, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.icon.setImageBitmap(null);
                    super.onLoadingStarted(imageUri, view);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    array.get(position).setIsLoaded(true);
                    holder.icon.setImageBitmap(loadedImage);
                    super.onLoadingComplete(imageUri, view, loadedImage);
                }
            });

            holder.select
                    .setSelected(array.get(position).getIsSeleted());

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
        private ImageView select;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.gallery_image_item);
            select = (ImageView) itemView.findViewById(R.id.gallery_item_selected);
            select.setVisibility(View.VISIBLE);

        }
    }

    public String getItem(int i) {
        return array.get(i).getImagePath();
    }

    public void addAll(ArrayList<PicsArtGalleryItem> files) {

        try {
            this.array.clear();
            this.array.addAll(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public ArrayList<CharSequence> getSelected() {
        ArrayList<CharSequence> arrayList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getIsSeleted() == true) {
                arrayList.add(array.get(i).getImagePath());
            }
        }
        return arrayList;
    }

    public void deselectAll() {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getIsSeleted() == true) {
                array.get(i).setIsSeleted(false);
            }
        }
        notifyDataSetChanged();
    }

}
