package com.generatingmain;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.javacodegeeks.androidvideocaptureexample.R;

/**
 * Created by Arman on 2/3/15.
 */
public class ItemChooserActivity extends ListActivity {

    public static final String RES_SELECTED_PRODUCT = "SelectedProduct";

    public static final EffectChoser[] AVAILABLE_PRODUCTS = new EffectChoser[]{
            new EffectChoser("FadeIn"),
            new EffectChoser("Slide from Right"),
            new EffectChoser("None"),

    };

    protected ArrayAdapter<EffectChoser> adapter;



    public static Intent newIntent(Context context) {
        Intent i = new Intent(context, ItemChooserActivity.class);
        return i;
    }

    // =============================================================================================
    // Lifecycle
    // ==

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        adapter = new ArrayAdapter<EffectChoser>(this, android.R.layout.simple_list_item_1, AVAILABLE_PRODUCTS);
        setListAdapter(adapter);
    }

    // =============================================================================================
    // Event handlers
    // ==

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        EffectChoser selectedItem = adapter.getItem(position);

        Intent res = new Intent();
        res.putExtra(RES_SELECTED_PRODUCT, selectedItem);
        setResult(Activity.RESULT_OK, res);
        finish();
    }

    // =============================================================================================
    // Other methods
    // ==
}

