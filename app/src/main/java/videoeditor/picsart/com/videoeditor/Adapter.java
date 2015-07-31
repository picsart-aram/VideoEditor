package videoeditor.picsart.com.videoeditor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import videoeditor.picsart.com.videoeditor.decoder.PhotoUtils;

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
        Bitmap bitmap = Util.readBitmapFromBytes(context, array.get(position));
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
