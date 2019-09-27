package com.simplelib.holder;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ViewsHolder<K> implements Iterable<Map.Entry<K, ViewHolder>> {
    private Context context;
    private ViewGroup parentView;

    private HashMap<K, ViewHolder> holderMap;

    public ViewsHolder(Context context) {
        if (context == null)
            throw new NullPointerException("No context attached");

        this.context = context;

        this.holderMap = new HashMap<>();
    }

    public ViewsHolder(ViewGroup parentView) {
        if (parentView == null)
            throw new NullPointerException("No parent view attached");

        this.context = parentView.getContext();

        if (context == null)
            throw new NullPointerException("No context attached");

        this.parentView = parentView;

        this.holderMap = new HashMap<>();
    }

    public final int getSize() {
        synchronized (holderMap) {
            Collection<ViewHolder> holderSet = holderMap.values();
            if (holderSet != null)
                return holderSet.size();
            return 0;
        }
    }

    @NonNull
    @Override
    public Iterator<Map.Entry<K, ViewHolder>> iterator() {
        return entryIterator();
    }

    public final Context getContext() {
        return context;
    }

    public final ViewGroup getParentView() {
        return parentView;
    }

    public final void setParentView(ViewGroup parentView) {
        this.parentView = parentView;
    }

    public final Iterator<K> keyIterator() {
        synchronized (holderMap) {
            return new IteratorWrapper<>(holderMap.keySet());
        }
    }

    public final Iterator<ViewHolder> holderIterator() {
        synchronized (holderMap) {
            return new IteratorWrapper<>(holderMap.values());
        }
    }

    public final Iterator<Map.Entry<K, ViewHolder>> entryIterator() {
        synchronized (holderMap) {
            return new IteratorWrapper<>(holderMap.entrySet());
        }
    }

    public final void setKeys(List<K> keys) {
        setKeys(keys, true);
    }

    public final void setKeys(List<K> keys, boolean create) {
        setKeys(keys, create, false);
    }

    public final void setKeys(List<K> keys, boolean create, boolean rebind) {
        if (keys == null) return;

        synchronized (holderMap) {
            removeViewHolders(keys, true, true);
            addViewHolders(keys, create, rebind);
        }
    }

    public final HashMap<K, ViewHolder> clearViewHolders() {
        return clearViewHolders(true);
    }

    public final HashMap<K, ViewHolder> clearViewHolders(boolean destroy) {
        synchronized (holderMap) {
            Set<K> keySet = holderMap.keySet();
            if (keySet == null) return new HashMap<>();

            HashMap<K, ViewHolder> removedHolderMap = removeViewHolders(keySet, false, destroy);
            if (removedHolderMap != null)
                return removedHolderMap;
            return new HashMap<>();
        }
    }

    public final ViewHolder addViewHolder(K key) {
        return addViewHolder(key, true);
    }

    public final ViewHolder addViewHolder(K key, boolean create) {
        return addViewHolder(key, create, false);
    }

    public final ViewHolder addViewHolder(K key, boolean create, boolean rebind) {
        if (key == null) return null;
        List<K> keys = Collections.singletonList(key);
        HashMap<K, ViewHolder> addedHolderMap = addViewHolders(keys, create, rebind);
        if (addedHolderMap != null && addedHolderMap.containsKey(key))
            return addedHolderMap.get(key);
        return null;
    }

    public final HashMap<K, ViewHolder> addViewHolders(Collection<K> keys) {
        return addViewHolders(keys, true);
    }

    public final HashMap<K, ViewHolder> addViewHolders(Collection<K> keys, boolean create) {
        return addViewHolders(keys, create, false);
    }

    public final HashMap<K, ViewHolder> addViewHolders(Collection<K> keys, boolean create, boolean rebind) {
        if (keys == null) return new HashMap<>();

        synchronized (holderMap) {
            Iterator<K> keyIterator = keys.iterator();
            if (keyIterator == null) return new HashMap<>();

            HashMap<K, ViewHolder> addedHolderMap = new HashMap<>();

            while (keyIterator.hasNext()) {
                K key = keyIterator.next();
                if (key == null) continue;

                ViewHolder holder = getViewHolder(key);
                if (holder == null) continue;

                addedHolderMap.put(key, holder);
            }

            if (create) {
                for (Map.Entry<K, ViewHolder> holderEntry : addedHolderMap.entrySet()) {
                    if (holderEntry == null) continue;

                    ViewHolder holder = holderEntry.getValue();
                    if (holder == null) continue;

                    try {
                        holder.create(rebind);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return addedHolderMap;
        }
    }

    public final ViewHolder removeViewHolder(K key) {
        return removeViewHolder(key, true);
    }

    public final ViewHolder removeViewHolder(K key, boolean destroy) {
        if (key == null) return null;
        List<K> keys = Collections.singletonList(key);
        HashMap<K, ViewHolder> removedHolderMap = removeViewHolders(keys, false, destroy);
        if (removedHolderMap != null && removedHolderMap.containsKey(key))
            return removedHolderMap.get(key);
        return null;
    }

    public final HashMap<K, ViewHolder> removeViewHolders(Collection<K> keys) {
        return removeViewHolders(keys, false);
    }

    public final HashMap<K, ViewHolder> removeViewHolders(Collection<K> keys, boolean retain) {
        return removeViewHolders(keys, retain, true);
    }

    public final HashMap<K, ViewHolder> removeViewHolders(Collection<K> keys, boolean retain, boolean destroy) {
        if (keys == null) return new HashMap<>();

        synchronized (holderMap) {
            Set<K> keySet = holderMap.keySet();
            if (keySet == null) return new HashMap<>();

            Iterator<K> keyIterator = keySet.iterator();
            if (keyIterator == null) return new HashMap<>();

            HashMap<K, ViewHolder> removedHolderMap = new HashMap<>();

            while (keyIterator.hasNext()) {
                K key = keyIterator.next();
                if (key == null) continue;

                boolean inKeyList = keySet == keys || keys.contains(key);
                if ((inKeyList && !retain) || (!inKeyList && retain)) {
                    ViewHolder holder = holderMap.get(key);

                    keyIterator.remove();
                    removedHolderMap.put(key, holder);
                }
            }

            if (destroy) {
                for (Map.Entry<K, ViewHolder> holderEntry : removedHolderMap.entrySet()) {
                    if (holderEntry == null) continue;

                    K key = holderEntry.getKey();
                    ViewHolder holder = holderEntry.getValue();
                    if (holder == null) continue;

                    try {
                        destroyViewHolder(key, holder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        holder.destroy();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return removedHolderMap;
        }
    }

    public final ViewHolder getViewHolder(K key) {
        if (key == null) return null;

        ViewHolder holder = null;
        try {
            synchronized (holderMap) {
                if (!holderMap.containsKey(key)) {
                    holder = createViewHolder(key, context, parentView);
                    holderMap.put(key, holder);
                } else {
                    holder = holderMap.get(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (holder != null)
                holder.setParentView(parentView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return holder;
    }

    public final View getContentView(K key) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.getContentView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public final boolean detach(K key) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public final boolean attach(K key) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.attach();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public final boolean attach(K key, int index) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.attach(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public final boolean attach(K key, int width, int height) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.attach(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public final boolean attach(K key, ViewGroup.LayoutParams params) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.attach(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public final boolean attach(K key, int index, ViewGroup.LayoutParams params) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.attach(index, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public final <T extends View> T findViewById(K key, @IdRes int id) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.findViewById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public final View inflateLayout(@LayoutRes int id) {
        return inflateLayout(id, true);
    }

    public final View inflateLayout(@LayoutRes int id, boolean useParent) {
        return inflateLayout(id, useParent, false);
    }

    public final View inflateLayout(@LayoutRes int id, boolean useParent, boolean attachToParent) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (parentView != null && useParent)
                return inflater.inflate(id, parentView, attachToParent);
            else
                return inflater.inflate(id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public final boolean recreateAll() {
        boolean recreated = true;
        synchronized (holderMap) {
            try {
                for (Map.Entry<K, ViewHolder> holderEntry : holderMap.entrySet()) {
                    if (holderEntry == null) continue;

                    K key = holderEntry.getKey();
                    if (key == null) continue;

                    recreated &= recreate(key);
                }
            } catch (Exception e) {
                recreated = false;
                e.printStackTrace();
            }
        }
        return recreated;
    }

    public final boolean recreate(K key) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.recreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public final boolean createAll() {
        return createAll(false);
    }

    public final boolean createAll(boolean rebind) {
        boolean created = true;
        synchronized (holderMap) {
            try {
                for (Map.Entry<K, ViewHolder> holderEntry : holderMap.entrySet()) {
                    if (holderEntry == null) continue;

                    K key = holderEntry.getKey();
                    if (key == null) continue;

                    created &= create(key, rebind);
                }
            } catch (Exception e) {
                created = false;
                e.printStackTrace();
            }
        }
        return created;
    }

    public final boolean create(K key) {
        return create(key, false);
    }

    public final boolean create(K key, boolean rebind) {
        ViewHolder holder = getViewHolder(key);
        try {
            if (holder != null)
                return holder.create(rebind);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    protected ViewHolder createViewHolder(final K key, final Context context, final ViewGroup parentView) {
        ViewHolder holder = new ViewHolder(context) {
            @Override
            public View createHolder(ViewGroup parentView) {
                return ViewsHolder.this.createHolder(key, ViewsHolder.this.parentView);
            }

            @Override
            public void bindHolder(View contentView) {
                ViewsHolder.this.bindHolder(key, contentView);
            }
        };
        holder.setParentView(parentView);
        return holder;
    }

    protected void destroyViewHolder(K key, ViewHolder viewHolder) {
    }

    protected abstract View createHolder(K key, ViewGroup parentView);

    protected abstract void bindHolder(K key, View contentView);

    private static class IteratorWrapper<E> implements Iterator<E> {
        private Iterator<E> iterator;

        public IteratorWrapper(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        public IteratorWrapper(Collection<E> collection) {
            if (collection != null)
                this.iterator = collection.iterator();
        }

        @Override
        public boolean hasNext() {
            if (iterator == null)
                return false;
            return iterator.hasNext();
        }

        @Override
        public E next() {
            if (iterator == null)
                return null;
            return iterator.next();
        }
    }
}