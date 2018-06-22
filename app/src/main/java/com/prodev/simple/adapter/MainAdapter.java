package com.prodev.simple.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prodev.simple.R;
import com.prodev.simple.container.Item;
import com.simplelib.adapter.SimpleRecyclerFilterAdapter;

import java.util.ArrayList;

public class MainAdapter extends SimpleRecyclerFilterAdapter<Item> {
    public MainAdapter() {
    }

    public MainAdapter(ArrayList<Item> list) {
        super(list);
    }

    @Override
    public View createHolder(ViewGroup parent, int viewType) {
        return inflateLayout(parent, R.layout.main_item);
    }

    @Override
    public void bindHolder(ViewHolder holder, final Item value, int pos) {
        ((TextView) holder.findViewById(R.id.main_item_text)).setText(value.getText());
    }
}
