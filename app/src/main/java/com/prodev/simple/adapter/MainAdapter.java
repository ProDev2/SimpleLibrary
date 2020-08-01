package com.prodev.simple.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.prodev.simple.R;
import com.prodev.simple.container.Item;
import com.simplelib.adapter.SimpleRecyclerFilterAdapter;

import java.util.ArrayList;

public class MainAdapter extends SimpleRecyclerFilterAdapter<String, Item> {
    public MainAdapter() {
    }

    public MainAdapter(ArrayList<String> list) {
        super(list);
    }

    @NonNull
    @Override
    protected View createView(@NonNull ViewGroup parent, int viewType) {
        return inflateLayout(parent, R.layout.main_item);
    }

    @Override
    protected void bindView(@NonNull ViewHolder holder, String value, @Nullable Item element, int pos) {
        if (element == null)
            return;

        ((TextView) holder.findViewById(R.id.main_item_text)).setText(element.getText());
    }
}
