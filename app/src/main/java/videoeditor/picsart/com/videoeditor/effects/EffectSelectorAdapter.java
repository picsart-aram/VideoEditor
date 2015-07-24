package videoeditor.picsart.com.videoeditor.effects;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import videoeditor.picsart.com.videoeditor.BaseVideoAction;
import videoeditor.picsart.com.videoeditor.R;
import videoeditor.picsart.com.videoeditor.effects.Utils.EffectsItem;
import videoeditor.picsart.com.videoeditor.effects.Utils.OnEffectApplyFinishedListener;

/**
 * Created by AramNazaryan on 7/24/15.
 */
public class EffectSelectorAdapter extends RecyclerView.Adapter<EffectSelectorAdapter.EffectsViewHolder>{

    private ArrayList<EffectsItem> items = new ArrayList<>();
    private LayoutInflater inflater = null;

    @Override
    public EffectsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return new EffectsViewHolder(inflater.inflate(R.layout.effect_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final EffectsViewHolder holder, final int position) {
        EffectsItem item = items.get(position);
        BaseVideoAction action = item.getAction();
        action.applyOnOneFrame(item.getPath(), new OnEffectApplyFinishedListener() {
            @Override
            public void onFinish(Bitmap bmp) {
                System.out.println("finished "+position);
                holder.imageView.setImageBitmap(bmp);
            }
        }, null);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(EffectsItem item) {
        items.add(item);
    }

    public void clear(){
        items.clear();
    }

    public EffectsItem getItem(int position) {
        return items.get(position);
    }

    class EffectsViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView = null;
        public EffectsViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }

}
