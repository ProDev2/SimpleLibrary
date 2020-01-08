package com.simplelib.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public abstract class SimpleRecyclerAdapter<V, E> extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {
    private Context context;
    private RecyclerView recyclerView;

    private List<V> list;
    private Provider<V, E> provider;

    public SimpleRecyclerAdapter() {
        this.list = new ArrayList<>();
    }

    public SimpleRecyclerAdapter(List<V> list) {
        if (list != null)
            this.list = list;
        else
            this.list = new ArrayList<>();
    }

    public void applyTo(SimpleRecyclerAdapter<V, E> src) {
        try {
            if (src != null) {
                if (src.list == null)
                    src.list = new ArrayList<>();
                src.list.clear();
                if (list != null)
                    src.list.addAll(list);

                try {
                    src.provider = provider.get();
                } catch (Exception e) {
                    src.provider = null;
                }

                src.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }

    public void applyTo(SimpleRecyclerAdapter<V, E> src, boolean update) {
        try {
            if (src != null) {
                if (src.list == null)
                    src.list = new ArrayList<>();
                src.list.clear();
                if (list != null)
                    src.list.addAll(list);

                try {
                    src.provider = provider.get();
                } catch (Exception e) {
                    src.provider = null;
                }

                if (update)
                    src.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }

    public List<V> getList() {
        return list;
    }

    public void setList(List<V> list) {
        if (list == null)
            list = new ArrayList<>();
        this.list = list;

        notifyDataSetChanged();
    }

    public void setList(List<V> list, boolean update) {
        if (list == null)
            list = new ArrayList<>();
        this.list = list;

        if (update)
            notifyDataSetChanged();
    }

    public Provider<V, E> getProvider() {
        return provider;
    }

    public void setProvider(Provider<V, E> provider) {
        this.provider = provider;

        notifyDataSetChanged();
    }

    public void setProvider(Provider<V, E> provider, boolean update) {
        this.provider = provider;

        if (update)
            notifyDataSetChanged();
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
            int pos = list.indexOf(value);

            if (pos >= 0 && pos < list.size()) {
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

    public void sort(Comparator<? super V> comparator, boolean update) {
        try {
            if (comparator != null) {
                Collections.sort(list, comparator);

                if (update)
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
            if (recyclerView != null) {
                int pos = list.indexOf(item);

                if (pos >= 0 && pos < list.size()) {
                    if (animate)
                        recyclerView.smoothScrollToPosition(pos);
                    else
                        recyclerView.scrollToPosition(pos);
                }
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

    public void swapList(List<?> list, int fromPos, int toPos) {
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
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = createHolder(parent, viewType);

        this.context = itemView.getContext();

        ViewHolder holder = new ViewHolder(itemView);
        try {
            bindViews(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull SimpleRecyclerAdapter.ViewHolder holder, int position) {
        if (holder == null)
            return;

        try {
            int posInList = holder.getAdapterPosition();
            if (posInList < 0 || posInList >= list.size())
                return;

            V value = list.get(posInList);

            E element = null;
            if (provider != null) {
                try {
                    element = provider.provide(value, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            bindHolder((ViewHolder) holder, value, element, position);
        } catch (Exception e) {
            e.printStackTrace();
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

    protected void bindViews(@NonNull ViewHolder holder) {
    }

    protected abstract @NonNull View createHolder(@NonNull ViewGroup parent, int viewType);

    protected abstract void bindHolder(@NonNull ViewHolder holder, V value, @Nullable E element, int pos);

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final HashMap<Integer, View> viewList;

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
                View v;
                if ((v = view.findViewById(id)) != null && !viewList.containsKey(id))
                    viewList.put(id, v);
            } catch (Exception e) {
            }
        }

        public void addViews(View... views) {
            for (View view : views) {
                addView(view);
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
                return viewList.get(id);
            }
        }

        public View getItemView() {
            return itemView;
        }
    }

    public interface Provider<V, R> {
        @Nullable R provide(V value, int pos);

        default Provider<V, R> get() {
            return this;
        }
    }
}
