package adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.intern.picsartvideo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

import activity.ImageEditActivity;
import item.SlideShowItem;
import utils.SlideShowConst;
import utils.Utils;


public class SlideShowAdapter extends RecyclerView.Adapter<SlideShowAdapter.ViewHolder> {

    public static final int REQUEST_CODE = 300;
    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root, SlideShowConst.MY_DIR_NAME);

    private ArrayList<SlideShowItem> arrayList;
    private Context context;
    private LayoutInflater infalter;
    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;

    public SlideShowAdapter(ArrayList<SlideShowItem> strings, Context c, ViewPager pager, ImagePagerAdapter adapter) {

        imagePagerAdapter = adapter;
        viewPager = pager;
        arrayList = strings;
        infalter = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.slide_show_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.slideShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(position, false);
            }
        });

        String path = arrayList.get(position).getPath();
        if (arrayList.get(position).isFromFileSystem()) {
            path = SlideShowConst.FILE_PREFIX + arrayList.get(position).getPath();
        }

        try {
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
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public SlideShowItem getItem(int i) {
        return arrayList.get(i);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView slideShowImage;
        private Button slideShowDeleteButton;
        private Button slideShowEditButton;

        public ViewHolder(final View itemView) {
            super(itemView);

            slideShowImage = (ImageView) itemView.findViewById(R.id.slide_show_image);
            slideShowDeleteButton = (Button) itemView.findViewById(R.id.slide_show_delete_button);
            Drawable drawable = slideShowDeleteButton.getBackground();
            drawable.mutate();
            drawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            slideShowDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(v.getRootView().getContext());
                    adb.setTitle("Delete");
                    adb.setMessage("Are you sure?");
                    adb.setIcon(android.R.drawable.ic_dialog_info);
                    adb.setPositiveButton("Yes", myClickListener);
                    adb.setNegativeButton("No", myClickListener);
                    adb.show();

                }
            });

            slideShowEditButton = (Button) itemView.findViewById(R.id.slide_show_edit_button);
            Drawable drawable1 = slideShowEditButton.getBackground();
            drawable1.mutate();
            drawable1.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

            slideShowEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, ImageEditActivity.class);
                    intent.putExtra("image_path", arrayList.get(getPosition()).getPath());
                    intent.putExtra("index", getPosition());
                    intent.putExtra("isEdited", arrayList.get(getPosition()).isEdited());
                    intent.putExtra("isfile", arrayList.get(getPosition()).isFromFileSystem());
                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE);

                }
            });
        }

        DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE: {

                        if (arrayList.get(getPosition()).getPath().contains(myDir.toString())) {
                            new File(arrayList.get(getPosition()).getPath()).delete();
                        }
                        removeAt(getPosition());
                        imagePagerAdapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(0);
                    }
                    break;
                    case Dialog.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        public void removeAt(int position) {
            arrayList.remove(position);
            //notifyItemRemoved(position);
            notifyDataSetChanged();
            //notifyItemRangeChanged(getPosition(), arrayList.size());
        }

    }

}