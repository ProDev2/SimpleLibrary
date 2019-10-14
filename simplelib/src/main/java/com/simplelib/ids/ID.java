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
        if (poolId == null)
            return null;

        Pool<Object> pool = new Pool<>();
        return registerPool(poolId, pool);
    }

    public static <E> Pool<E> registerPool(String poolId, Class<E> poolCls) {
        if (poolId == null || poolCls == null)
            return null;

        Pool<E> pool = new Pool<>();
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

    public static Pool getPool(String poolId) {
        if (poolId == null)
            return null;

        Map<String, Pool> poolMap = getPoolMap();
        if (poolMap == null) return null;
        synchronized (poolMap) {
            return poolMap.get(poolId);
        }
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

    public static final class Pool<E> implements Iterable<Pool.Entry<E>> {
        private final List<Entry<E>> list;
        private int cursor;

        public Pool() {
            this.list = new ArrayList<>();
            this.cursor = 0;
        }

        @Override
        public synchronized Iterator<Entry<E>> iterator() {
            Iterator<Entry<E>> entryIterator = list.iterator();

            return new Iterator<Entry<E>>() {
                @Override
                public boolean hasNext() {
                    synchronized (Pool.this) {
                        return entryIterator != null ? entryIterator.hasNext() : null;
                    }
                }

                @Override
                public Entry<E> next() {
                    synchronized (Pool.this) {
                        return entryIterator != null ? entryIterator.next() : null;
                    }
                }

                @Override
                public void remove() {
                    synchronized (Pool.this) {
                        if (entryIterator != null)
                            entryIterator.remove();
                    }
                }
            };
        }

        public synchronized void clear() {
            this.list.clear();
            this.cursor = 0;
        }

        public synchronized boolean hasId(int id) {
            Entry<E> entry = getEntry(id);
            return entry != null;
        }

        public synchronized E get(int id) {
            Entry<E> entry = getEntry(id);
            return entry != null ? entry.getArg() : null;
        }

        public synchronized Entry<E> getEntry(int id) {
            if (id < 0 || id >= list.size())
                return null;
            return list.get(id);
        }

        public synchronized E remove(int id) {
            Entry<E> entry = removeEntry(id);
            return entry != null ? entry.getArg() : null;
        }

        public synchronized Entry<E> removeEntry(int id) {
            if (id < 0 || id >= list.size())
                return null;
            Entry<E> entry = list.remove(id);
            cursor = Math.min(cursor, id);
            return entry;
        }

        public synchronized int next() {
            return next(null);
        }

        public synchronized int next(E arg) {
            return next(arg, null);
        }

        public synchronized int next(E arg, Entry.Listener<E> listener) {
            int id = Math.max(0, cursor);
            for (Entry<E> entry; (entry = getEntry(id)) != null && !entry.isEmpty(); id++);
            cursor = id + 1;
            for (Entry<E> entry; (entry = getEntry(cursor)) != null && !entry.isEmpty(); cursor++);

            Entry<E> newEntry = new Entry<>();
            newEntry.setEmpty(false);
            newEntry.setArg(arg);
            newEntry.setListener(listener);
            list.add(id, newEntry);

            return id;
        }

        public synchronized boolean invoke(int id) {
            if (id < 0 || id >= list.size())
                return false;
            Entry<E> entry = list.get(id);
            if (entry == null || entry.isEmpty())
                return false;
            return entry.invoke(id);
        }

        public synchronized E removeAndInvoke(int id) {
            Entry<E> entry = removeAndInvokeEntry(id);
            return entry != null ? entry.getArg() : null;
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
    }
}