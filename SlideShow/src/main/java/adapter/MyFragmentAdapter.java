package adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.intern.picsartvideo.R;

import java.util.ArrayList;

import activity.RecyclerViewFragment;

public class MyFragmentAdapter extends RecyclerView.Adapter<MyFragmentAdapter.ViewHolder> {

    private ArrayList<Drawable> arrayList;
    private RecyclerViewFragment.OnStickerChangedListener onStickerChangedListener;


    public MyFragmentAdapter(ArrayList<Drawable> arrayList, RecyclerViewFragment.OnStickerChangedListener onStickerChangedListener) {
        this.onStickerChangedListener = onStickerChangedListener;
        this.arrayList = arrayList;
    }

    @Override
    public MyFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item
                , parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.icon.setImageDrawable(arrayList.get(position));
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onStickerChangedListener.onStickerChanged(position);

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.ic);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}