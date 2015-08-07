package activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intern.picsartvideo.R;

import java.util.ArrayList;

import adapter.MyFragmentAdapter;
import utils.SpacesItemDecoration;


public class RecyclerViewFragment extends Fragment {

    private ArrayList<Drawable> arrayList = new ArrayList<>();

    private OnStickerChangedListener onStickerChangedListener;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public RecyclerViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new MyFragmentAdapter(arrayList, onStickerChangedListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_blank, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_rec_view);
        return v;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setAdapter(ArrayList<Drawable> arrayList) {

        this.arrayList.clear();
        this.arrayList.addAll(arrayList);

    }

    public void setOnShapeChangedListener(OnStickerChangedListener l) {
        onStickerChangedListener = l;
    }

    public static interface OnStickerChangedListener {
        public void onStickerChanged(int shapeIndex);
    }

}
