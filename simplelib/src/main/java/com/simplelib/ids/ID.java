package com.simplelib.ids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ID {
    private static Map<String, Pool> poolMap;

    private static Map<String, Pool> getPoolMap() {
        if (poolMap == null)
            poolMap = new HashMap<>();
        return poolMap;
    }

    public static void unregisterAllPools() {
        Map<String, Pool> poolMap = getPoolMap();
        if (poolMap == null) return;
        synchronized (poolMap) {
            poolMap.clear();
        }
    }

    public static Pool<Object> registerPool(String poolId) {
        return registerPool(poolId, -1, -1);
    }

    public static Pool<Object> registerPool(String poolId, int min, int max) {
        if (poolId == null)
            return null;

        Pool<Object> pool = new Pool<>(min, max);
        return registerPool(poolId, pool);
    }

    public static <E> Pool<E> registerPool(String poolId, Class<E> poolCls) {
        return registerPool(poolId, poolCls, -1, -1);
    }

    public static <E> Pool<E> registerPool(String poolId, Class<E> poolCls, int min, int max) {
        if (poolId == null || poolCls == null)
            return null;

        Pool<E> pool = new Pool<>(min, max);
        return registerPool(poolId, pool);
    }

    public static <E> Pool<E> registerPool(String poolId, Pool<E> pool) {
        if (poolId == null || pool == null)
            return null;

        Map<String, Pool> poolMap = getPoolMap();
        if (poolMap == null) return null;
        synchronized (poolMap) {
            poolMap.put(poolId, pool);
            return pool;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> Pool<E> unregisterPool(String poolId) {
        if (poolId == null)
            return null;

        Map<String, Pool> poolMap = getPoolMap();
        if (poolMap == null) return null;
        synchronized (poolMap) {
            return poolMap.remove(poolId);
        }
    }

    public static boolean hasPool(String poolId) {
        if (poolId == null)
            return false;

        Map<String, Pool> poolMap = getPoolMap();
        if (poolMap == null) return false;
        synchronized (poolMap) {
            return poolMap.containsKey(poolId);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> Pool<E> getPool(String poolId, Class<E> poolCls) {
        if (poolId == null || poolCls == null)
            return null;

        Map<String, Pool> poolMap = getPoolMap();
        if (poolMap == null) return null;
        synchronized (poolMap) {
            try {
                return (Pool<E>) poolMap.get(poolId);
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> Pool<E> getPool(String poolId) {
        if (poolId == null)
            return null;

        Map<String, Pool> poolMap = getPoolMap();
        if (poolMap == null) return null;
        synchronized (poolMap) {
            return poolMap.get(poolId);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> Pool.IdIterator<E> getIdIterator(String poolId) {
        Pool pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return null;
        return pool.getIdIterator();
    }

    @SuppressWarnings("unchecked")
    public static <E> Pool.EntryIterator<E> getEntryIterator(String poolId) {
        Pool pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return null;
        return pool.getEntryIterator();
    }

    public static void clear(String poolId) {
        Pool pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return;
        pool.clear();
    }

    public static boolean has(String poolId, int id) {
        Pool pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return false;
        return pool.hasId(id);
    }

    @SuppressWarnings("unchecked")
    public static <E> E get(String poolId, int id) {
        Pool<E> pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return null;
        return pool.get(id);
    }

    @SuppressWarnings("unchecked")
    public static <E> E remove(String poolId, int id) {
        Pool<E> pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return null;
        return pool.remove(id);
    }

    public static int next(String poolId) {
        return next(poolId, null);
    }

    public static <E> int next(String poolId, E arg) {
        return next(poolId, arg, null);
    }

    @SuppressWarnings("unchecked")
    public static <E> int next(String poolId, E arg, Pool.Entry.Listener<E> listener) {
        Pool<E> pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return -1;
        return pool.next(arg, listener);
    }

    public static boolean invoke(String poolId, int id) {
        Pool pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return false;
        return pool.invoke(id);
    }

    @SuppressWarnings("unchecked")
    public static <E> E removeAndInvoke(String poolId, int id) {
        Pool<E> pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return null;
        return pool.removeAndInvoke(id);
    }

    public static final class Pool<E> implements Iterable<Integer> {
        private final int min, max;

        private final List<Entry<E>> list;
        private int cursor, lastCursor;

        public Pool() {
            this(-1, -1);
        }

        public Pool(int min, int max) {
            if (min < 0) min = -1;
            if (max < 0) max = -1;
            if (max < min && max >= 0) max = min;

            this.min = min;
            this.max = max;

            this.list = new ArrayList<>();
            this.cursor = 0;
            this.lastCursor = 0;
        }

        public synchronized int getMin() {
            return min;
        }

        public synchronized int getMax() {
            return max;
        }

        @Override
        public synchronized Iterator<Integer> iterator() {
            return getIdIterator();
        }

        public synchronized IdIterator<E> getIdIterator() {
            return new IdIterator<E>(this);
        }

        public synchronized EntryIterator<E> getEntryIterator() {
            return new EntryIterator<>(this);
        }

        public synchronized int calculateFirstId() {
            int minId = Math.max(min, 0);
            int maxId = Math.max(max < 0 ? lastCursor : Math.min(lastCursor, max), minId);

            int id = Math.min(minId, maxId);
            Entry<E> entry;

            while (((entry = getEntry(id)) == null ||
                    entry.isEmpty()) &&
                    id >= minId &&
                    id <= maxId)
                id++;

            if (entry != null &&
                    !entry.isEmpty() &&
                    hasId(id) &&
                    id >= minId &&
                    id <= maxId)
                return id;

            return -1;
        }

        public synchronized int calculateLastId() {
            int minId = Math.max(min, 0);
            int maxId = Math.max(max < 0 ? lastCursor : Math.min(lastCursor, max), minId);

            int id = Math.max(minId, maxId);
            Entry<E> entry;

            while (((entry = getEntry(id)) == null ||
                    entry.isEmpty()) &&
                    id >= minId &&
                    id <= maxId)
                id--;

            if (entry != null &&
                    !entry.isEmpty() &&
                    hasId(id) &&
                    id >= minId &&
                    id <= maxId)
                return id;

            return -1;
        }

        public synchronized void clear() {
            this.list.clear();
            this.cursor = 0;
            this.lastCursor = 0;
        }

        public synchronized boolean hasId(int id) {
            Entry<E> entry = getEntry(id);
            return entry != null;
        }

        public synchronized E get(int id) {
            Entry<E> entry = getEntry(id);
            return entry != null && !entry.isEmpty() ? entry.getArg() : null;
        }

        public synchronized Entry<E> getEntry(int id) {
            int pos = id - Math.max(min, 0);
            if (pos < 0 || pos >= list.size())
                return null;
            return list.get(pos);
        }

        public synchronized E remove(int id) {
            Entry<E> entry = removeEntry(id);
            return entry != null && !entry.isEmpty() ? entry.getArg() : null;
        }

        public synchronized Entry<E> removeEntry(int id) {
            int pos = id - Math.max(min, 0);
            if (pos < 0 || pos >= list.size())
                return null;

            Entry<E> removedEntry = list.set(pos, null);

            cursor = Math.max(Math.max(min, 0), Math.min(cursor, id));
            for (Entry<E> entry;
                 ((entry = getEntry(lastCursor)) == null ||
                         entry.isEmpty()) &&
                         lastCursor >= Math.max(min, 0) &&
                         (max < 0 || lastCursor <= max);
                 lastCursor--) {
                int lastPos = lastCursor - Math.max(min, 0);
                if (lastPos >= 0 && lastPos < list.size())
                    list.remove(lastPos);
            }

            return removedEntry;
        }

        public synchronized int next() {
            return next(null);
        }

        public synchronized int next(E arg) {
            return next(arg, null);
        }

        public synchronized int next(E arg, Entry.Listener<E> listener) {
            int id = Math.max(Math.max(min, 0), cursor);
            for (Entry<E> entry;
                 (entry = getEntry(id)) != null &&
                         !entry.isEmpty() &&
                         (max < 0 || id <= max);
                 id++);

            cursor = id + 1;
            for (Entry<E> entry;
                 (entry = getEntry(cursor)) != null &&
                         !entry.isEmpty() &&
                         (max < 0 || cursor <= max);
                 cursor++);

            if ((min < 0 || id >= min) && (max < 0 || id <= max)) {
                lastCursor = Math.max(lastCursor, id);

                int pos = id - Math.max(min, 0);

                if (pos >= 0 && pos <= list.size()) {
                    Entry<E> newEntry = new Entry<>();
                    newEntry.setEmpty(false);
                    newEntry.setArg(arg);
                    newEntry.setListener(listener);
                    list.add(pos, newEntry);

                    return id;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }

        public synchronized boolean invoke(int id) {
            int pos = id - Math.max(min, 0);
            if (pos < 0 || pos >= list.size())
                return false;

            Entry<E> entry = list.get(id);
            if (entry == null || entry.isEmpty())
                return false;
            return entry.invoke(id);
        }

        public synchronized E removeAndInvoke(int id) {
            Entry<E> entry = removeAndInvokeEntry(id);
            return entry != null && !entry.isEmpty() ? entry.getArg() : null;
        }

        public synchronized Entry<E> removeAndInvokeEntry(int id) {
            Entry<E> entry = removeEntry(id);
            if (entry == null || entry.isEmpty())
                return null;
            entry.invoke(id);
            return entry;
        }

        public static final class Entry<E> {
            private boolean empty;
            private E arg;
            private Listener<E> listener;

            private Entry() {
                this.empty = true;
                this.arg = null;
            }

            private Entry(E arg) {
                this.empty = false;
                this.arg = arg;
            }

            private Entry(E arg, Listener<E> listener) {
                this.empty = false;
                this.arg = arg;
                this.listener = listener;
            }

            public synchronized boolean isEmpty() {
                return empty;
            }

            public synchronized void setEmpty(boolean empty) {
                this.empty = empty;
            }

            public synchronized E getArg() {
                return arg;
            }

            public synchronized void setArg(E arg) {
                this.arg = arg;
            }

            public void setListener(Listener<E> listener) {
                this.listener = listener;
            }

            public boolean invoke(int id) {
                if (listener != null && !empty) {
                    listener.onInvoke(id, arg);
                    return true;
                }
                return false;
            }

            public interface Listener<E> {
                void onInvoke(int id, E arg);
            }
        }

        public static class PoolIterator<E> {
            public static final int NO_ID = -1;

            protected final Pool<E> pool;

            private int cursor;
            private boolean hasNext;

            public PoolIterator(Pool<E> pool) {
                if (pool == null)
                    throw new NullPointerException("No pool attached");

                this.pool = pool;

                reset();
            }

            public synchronized final void reset() {
                this.cursor = NO_ID;
                this.hasNext = false;
            }

            public synchronized final void skip() {
                synchronized (pool) {
                    int minId = Math.max(pool.getMin(), 0);

                    int nextCursor;
                    if (cursor != NO_ID &&
                            cursor >= minId) {
                        int currentId = Math.max(Math.max(pool.getMin(), 0), cursor);
                        nextCursor = findValidId(currentId + 1);
                    } else {
                        nextCursor = findValidId(minId);
                    }

                    if (nextCursor != NO_ID &&
                            nextCursor >= minId) {
                        hasNext = true;
                        cursor = nextCursor;
                    } else {
                        hasNext = false;
                    }
                }
            }

            public synchronized final int findValidId(int cursor) {
                synchronized (pool) {
                    int minId = pool.calculateFirstId();
                    int maxId = pool.calculateLastId();

                    int id = Math.max(minId, cursor);
                    Entry<E> entry;

                    while (((entry = pool.getEntry(id)) == null ||
                            entry.isEmpty()) &&
                            id >= minId &&
                            id <= maxId)
                        id++;

                    if (entry != null &&
                            !entry.isEmpty() &&
                            pool.hasId(id) &&
                            id >= minId &&
                            id <= maxId)
                        return id;

                    return NO_ID;
                }
            }

            public synchronized final boolean hasNextId() {
                synchronized (pool) {
                    if (cursor == NO_ID || !hasNext)
                        skip();
                    return hasNext;
                }
            }

            public synchronized final int nextId() {
                synchronized (pool) {
                    if (cursor == NO_ID || !hasNext)
                        skip();

                    int minId = Math.max(pool.getMin(), 0);
                    int id = cursor >= minId ? cursor : NO_ID;

                    if (id != NO_ID && hasNext) {
                        skip();
                        return id;
                    }

                    return NO_ID;
                }
            }
        }

        public static final class IdIterator<E> extends PoolIterator<E> implements Iterator<Integer> {
            public IdIterator(Pool<E> pool) {
                super(pool);
            }

            @Override
            public boolean hasNext() {
                return super.hasNextId();
            }

            @Override
            public Integer next() {
                return super.nextId();
            }
        }

        public static final class EntryIterator<E> extends PoolIterator<E> implements Iterator<Entry<E>> {
            public EntryIterator(Pool<E> pool) {
                super(pool);
            }

            @Override
            public boolean hasNext() {
                return super.hasNextId();
            }

            @Override
            public Entry<E> next() {
                int id = super.nextId();
                return pool.getEntry(id);
            }
        }
    }
}