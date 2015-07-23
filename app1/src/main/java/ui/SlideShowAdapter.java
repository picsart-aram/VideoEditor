package ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.javacodegeeks.androidvideocaptureexample.R;

import java.io.File;
import java.util.ArrayList;


public class SlideShowAdapter extends RecyclerView.Adapter<SlideShowAdapter.ViewHolder> {

    public static final int REQUEST_CODE = 300;
    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/req_images");

    private ArrayList<Integer> array;
    private Context context;
    private LayoutInflater infalter;
    private CollageMainActivity.OnFrameChangeListener frameChangeListener;

    public SlideShowAdapter(Context c , ArrayList<Integer> frames, CollageMainActivity.OnFrameChangeListener frameChangeListener) {

        array=frames;
        infalter = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = c;
        this.frameChangeListener = frameChangeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.slide_show_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.slideShowImage.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), array.get(position)));
        holder.slideShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameChangeListener.onFrameChange(position);
            }
        });



        /*try {
            ImageLoader.getInstance().displayImage(path
                    , holder.slideShowImage, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.slideShowImage.setImageBitmap(null);
                    super.onLoadingStarted(imageUri, view);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Bitmap bitmap = Utils.scaleCenterCrop(loadedImage, 600, 600);
                    holder.slideShowImage.setImageBitmap(bitmap);
                    super.onLoadingComplete(imageUri, view, loadedImage);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public Integer getItem(int i) {
        return array.get(i);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView slideShowImage;


        public ViewHolder(final View itemView) {
            super(itemView);

            slideShowImage = (ImageView) itemView.findViewById(R.id.slide_show_image);

        }

        public void removeAt(int position) {
            array.remove(position);
            //notifyItemRemoved(position);
            notifyDataSetChanged();
            //notifyItemRangeChanged(getPosition(), arrayList.size());
        }

    }

}