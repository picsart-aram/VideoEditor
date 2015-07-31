package videoeditor.picsart.com.videoeditor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.decoder.PhotoUtils;

import java.nio.ByteBuffer;
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

        SharedPreferences sharedPreferences = context.getSharedPreferences("pics_art_video_editor", Context.MODE_PRIVATE);
        int bufferSize = sharedPreferences.getInt("buffer_size", 0);
        int width = sharedPreferences.getInt("frame_width", 0);
        int height = sharedPreferences.getInt("frame_height", 0);
        int orientation = sharedPreferences.getInt("frame_orientation", 0);
        ByteBuffer buffer = PhotoUtils.readBufferFromFile(array.get(position), bufferSize);
        Bitmap bitmap = PhotoUtils.fromBufferToBitmap(width, height, orientation, buffer);
        holder.icon.setImageBitmap(bitmap);
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
