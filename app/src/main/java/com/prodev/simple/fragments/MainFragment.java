package com.prodev.simple.fragments;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.prodev.simple.R;
import com.prodev.simple.adapter.MainAdapter;
import com.prodev.simple.container.Item;
import com.simplelib.SimpleFragment;
import com.simplelib.adapter.SimpleItemTouchHelper;
import com.simplelib.adapter.SimpleRecyclerAdapter;
import com.simplelib.container.SimpleFilter;

import java.util.ArrayList;

public class MainFragment extends SimpleFragment {
    private EditText searchView;

    private SimpleFilter<String, Item> filter;

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private MainAdapter adapter;

    public MainFragment() {
        super(R.layout.main_fragment);
    }

    @Override
    public void create(View view) {
        searchView = (EditText) findViewById(R.id.main_fragment_search);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.updateFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filter = new SimpleFilter<String, Item>() {
            @Override
            public boolean filter(String value) {
                if (value == null)
                    return false;

                String searchText = searchView.getText().toString().toLowerCase();

                if (searchText != null && searchText.length() > 0)
                    return value.toLowerCase().contains(searchText);
                else
                    return true;
            }
        };

        ArrayList<String> itemList = new ArrayList<>();
        for (int x = 0; x < 100; x++) {
            itemList.add("Item" + Integer.toString(x));
        }

        recyclerView = (RecyclerView) findViewById(R.id.main_fragment_recyclerView);

        layoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MainAdapter(itemList);
        adapter.setProvider(new SimpleRecyclerAdapter.Provider<String, Item>() {
            @Nullable
            @Override
            public Item provide(String value, int pos) {
                return new Item(value);
            }
        });

        adapter.setFilter(filter);
        recyclerView.setAdapter(adapter);

        SimpleItemTouchHelper.apply(recyclerView);
    }
}
