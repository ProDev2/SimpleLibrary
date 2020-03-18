package com.prodev.simple.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prodev.simple.R;
import com.prodev.simple.container.Item;
import com.prodev.simple.states.Fruits;
import com.simplelib.SimpleFragment;
import com.simplelib.container.SimpleFilter;
import com.simplelib.container.SimpleItem;
import com.simplelib.struct.Tree;
import com.simplelib.struct.adapter.SimpleTreeAdapter;

public class TreeFragment extends SimpleFragment {
    private EditText searchView;

    private SimpleFilter<Tree.Item, SimpleItem> filter;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SimpleTreeAdapter<SimpleItem> treeAdapter;

    public TreeFragment() {
        super(R.layout.tree_fragment);
    }

    @Override
    public void create(View view, Bundle savedInstanceState) {
        searchView = (EditText) findViewById(R.id.tree_fragment_search);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (filter != null)
                    filter.update();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filter = new SimpleFilter<Tree.Item, SimpleItem>() {
            @Override
            public boolean filter(Tree.Item value) {
                if (value == null)
                    return false;
                if (!(value instanceof Fruits.FruitItem))
                    return true;

                SimpleItem data = ((Fruits.FruitItem) value).getData();
                if (data == null)
                    return true;

                String searchText = searchView.getText().toString().toLowerCase();

                if (searchText != null && searchText.length() > 0)
                    return data.getText().getText(getActivity()).toLowerCase().contains(searchText);
                else
                    return true;
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.tree_fragment_recyclerView);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        treeAdapter = new SimpleTreeAdapter<>();
        treeAdapter.setProvider(new Fruits.FruitProvider(), false);

        treeAdapter.setFilter(filter, false);

        recyclerView.setAdapter(treeAdapter);

        // Create tree
        final Tree tree = Tree.with("", Fruits.class);
        Tree.Helper.expandAllLevels(tree);
        treeAdapter.setTree(tree, true);
    }
}
