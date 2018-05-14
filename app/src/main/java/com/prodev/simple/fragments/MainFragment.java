package com.prodev.simple.fragments;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.prodev.simple.R;
import com.prodev.simple.adapter.MainAdapter;
import com.prodev.simple.container.Item;
import com.simplelib.SimpleFragment;
import com.simplelib.adapter.SimpleItemTouchHelper;

import java.util.ArrayList;

public class MainFragment extends SimpleFragment {
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private MainAdapter adapter;

    public MainFragment() {
        super(R.layout.main_fragment);
    }

    @Override
    public void create(View view) {
        ArrayList<Item> itemList = new ArrayList<>();
        for (int x = 0; x < 100; x++) {
            itemList.add(new Item("Item" + Integer.toString(x)));
        }

        recyclerView = (RecyclerView) findViewById(R.id.main_fragment_recyclerView);

        layoutManager = new GridLayoutManager(getActivity(), 5);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MainAdapter(itemList);
        recyclerView.setAdapter(adapter);

        SimpleItemTouchHelper.apply(recyclerView);
    }
}
