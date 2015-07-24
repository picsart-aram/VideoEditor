package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import item.SlideShowItem;
import utils.Utils;

/**
 * Created by Tirgan Isajanyan on 4/15/15.
 */
public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<SlideShowItem> mImages = new ArrayList<>();
    private RecyclerView recyclerView;


    public ImagePagerAdapter(ArrayList<SlideShowItem> imPaths, Context ctx, RecyclerView recyclerView1) {
        this.mImages = imPaths;
        this.context = ctx;
        recyclerView = recyclerView1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    public ArrayList<SlideShowItem> getmImages() {
        return mImages;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.main_image_frame, container, false);
        final ImageView imageView = (ImageView) frameLayout.findViewById(R.id.view_pager_image);

        String path = mImages.get(position).getPath();
        if (mImages.get(position).isFromFileSystem()) {
            path = "file://" + mImages.get(position).getPath();
        }


        ImageLoader.getInstance().displayImage(path , imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                imageView.setImageBitmap(null);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                if (view != null) {
                    view.setAnimation(null);
                }
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                imageView.setImageBitmap(Utils.scaleCenterCrop(bitmap, 1000, 1000));

            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });

        ((ViewPager) container).addView(frameLayout, 0);
        return frameLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((FrameLayout) object);
    }

}