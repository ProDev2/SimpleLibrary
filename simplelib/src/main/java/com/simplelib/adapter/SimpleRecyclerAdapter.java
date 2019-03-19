package com.simplelib.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public abstract class SimpleRecyclerAdapter<V> extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {
    private Context context;
    private RecyclerView recyclerView;

    private ArrayList<V> list;

    public SimpleRecyclerAdapter() {
        this.list = new ArrayList<>();
    }

    public SimpleRecyclerAdapter(ArrayList<V> list) {
        if (list != null)
            this.list = list;
        else
            this.list = new ArrayList<>();
    }

    public ArrayList<V> getList() {
        return list;
    }

    public void setList(ArrayList<V> list) {
        if (list != null)
            this.list = list;
    }

    public void setList(ArrayList<V> list, boolean update) {
        if (list != null) {
            this.list = list;

            if (update)
                notifyDataSetChanged();
        }
    }

    public void add(V value) {
        try {
            if (list != null) {
                list.add(value);
                notifyItemInserted(list.size() - 1);
            }
        } catch (Exception e) {
        }
    }

    public void add(int index, V value) {
        try {
            if (list != null) {
                list.add(index, value);
                notifyItemInserted(index);
            }
        } catch (Exception e) {
        }
    }

    public V get(int pos) {
        if (pos >= 0 && pos < list.size())
            return list.get(pos);
        else
            return null;
    }

    public V getAtAdapterPos(int pos) {
        if (pos >= 0 && pos < list.size())
            return list.get(pos);
        else
            return null;
    }

    public int getAdapterItemPos(V value) {
        return list.indexOf(value);
    }

    public int getItemPos(V value) {
        return list.indexOf(value);
    }

    public void move(int posFrom, int posTo) {
        try {
            if (posFrom >= 0 && posTo >= 0 && posFrom < list.size() && posTo < list.size() && posFrom != posTo) {
                swapList(list, posFrom, posTo);
                notifyItemMoved(posFrom, posTo);
            }
        } catch (Exception e) {
        }
    }

    public boolean contains(V value) {
        return list.contains(value);
    }

    public void remove(V value) {
        try {
            if (list.contains(value)) {
                int pos = list.indexOf(value);

                list.remove(pos);
                notifyItemRemoved(pos);
            }
        } catch (Exception e) {
        }
    }

    public void remove(int pos) {
        try {
            if (pos >= 0 && pos < list.size()) {
                list.remove(pos);
                notifyItemRemoved(pos);
            }
        } catch (Exception e) {
        }
    }

    public void clear() {
        try {
            list.clear();
            notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    public void sort(Comparator<? super V> comparator) {
        try {
            if (comparator != null) {
                Collections.sort(list, comparator);
                notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }

    public int getListSize() {
        return list.size();
    }

    public void reload() {
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void scrollUp() {
        if (list.size() > 0) scrollToPosition(0);
    }

    public void scrollDown() {
        if (list.size() > 0) scrollToPosition(list.size() - 1);
    }

    public void scrollToPosition(int pos) {
        scrollToPosition(pos, true);
    }

    public void scrollToPosition(V item) {
        scrollToPosition(item, true);
    }

    public void scrollToPosition(int pos, boolean animate) {
        try {
            if (recyclerView != null && pos >= 0 && pos < list.size()) {
                if (animate)
                    recyclerView.smoothScrollToPosition(pos);
                else
                    recyclerView.scrollToPosition(pos);
            }
        } catch (Exception e) {
        }
    }

    public void scrollToPosition(V item, boolean animate) {
        try {
            if (recyclerView != null && list.contains(item)) {
                if (animate)
                    recyclerView.smoothScrollToPosition(list.indexOf(item));
                else
                    recyclerView.scrollToPosition(list.indexOf(item));
            }
        } catch (Exception e) {
        }
    }

    public void runAfterUpdate(final Runnable runnable) {
        try {
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    runnable.run();
                }
            });
        } catch (Exception e) {
            runnable.run();
        }
    }

    public void swapList(ArrayList<?> list, int fromPos, int toPos) {
        if (list != null && fromPos != toPos) {
            if (fromPos < toPos) {
                for (int pos = fromPos; pos < toPos; pos++) {
                    Collections.swap(list, pos, pos + 1);
                }
            } else {
                for (int pos = fromPos; pos > toPos; pos--) {
                    Collections.swap(list, pos, pos - 1);
                }
            }
        }
    }

    public LayoutInflater getLayoutInflaterFrom(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext());
    }

    public View inflateLayout(ViewGroup parent, int layoutId) {
        return getLayoutInflaterFrom(parent).inflate(layoutId, parent, false);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        if (recyclerView != null) {
            this.recyclerView = recyclerView;
            this.context = recyclerView.getContext();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = createHolder(parent, viewType);

        this.context = itemView.getContext();

        ViewHolder holder = new ViewHolder(itemView);
        bindViews(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(SimpleRecyclerAdapter.ViewHolder holder, int position) {
        try {
            int posInList = holder.getAdapterPosition();
            if (posInList >= 0 && posInList < list.size())
                bindHolder(holder, list.get(posInList), position);
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        try {
            if (list != null && list.size() > 0)
                return list.size();
        } catch (Exception e) {
        }

        return 0;
    }

    public void bindViews(ViewHolder holder) {
    }

    public abstract View createHolder(ViewGroup parent, int viewType);

    public abstract void bindHolder(ViewHolder holder, V value, int pos);

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private HashMap<Integer, View> viewList;

        public ViewHolder(View view) {
            super(view);

            this.view = view;
            this.viewList = new HashMap<>();
        }

        public void addViews(int... ids) {
            for (int id : ids) {
                addView(id);
            }
        }

        public void addView(int id) {
            try {
                if (view.findViewById(id) != null && !viewList.containsKey(id)) {
                    View item = view.findViewById(id);
                    viewList.put(id, item);
                }
            } catch (Exception e) {
            }
        }

        public void addViews(View... views) {
            for (View view : views) {
                addViews(view);
            }
        }

        public void addView(View view) {
            addView(view.getId(), view);
        }

        public void addView(int id, View view) {
            if (!viewList.containsKey(id)) {
                viewList.put(id, view);
            }
        }

        public View findViewById(int id) {
            if (viewList.containsKey(id))
                return viewList.get(id);
            else {
                addView(id);

                if (viewList.containsKey(id))
                    return viewList.get(id);
            }

            return null;
        }

        public View getItemView() {
            return itemView;
        }
    }
}
