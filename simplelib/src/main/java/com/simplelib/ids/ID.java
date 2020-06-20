package com.simplelib.ids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@SuppressWarnings({
        "unused",
        "SynchronizationOnLocalVariableOrMethodParameter"
})
public final class ID {
    private static Map<String, Pool<?>> poolMap;

    private static Map<String, Pool<?>> getPoolMap() {
        if (poolMap == null)
            poolMap = new HashMap<>();
        return poolMap;
    }

    public static void unregisterAllPools() {
        Map<String, Pool<?>> poolMap = getPoolMap();
        if (poolMap == null) return;
        synchronized (poolMap) {
            poolMap.clear();
        }
    }

    public static Pool<Object> registerPool(String poolId) {
        return registerPool(poolId, 0, 0);
    }

    public static Pool<Object> registerPool(String poolId, int offset, int count) {
        if (poolId == null)
            return null;

        Pool<Object> pool = new Pool<>(offset, count);
        return registerPool(poolId, pool);
    }

    public static <E> Pool<E> registerPool(String poolId, Class<E> poolCls) {
        return registerPool(poolId, poolCls, 0, 0);
    }

    public static <E> Pool<E> registerPool(String poolId, Class<E> poolCls, int offset, int count) {
        if (poolId == null || poolCls == null)
            return null;

        Pool<E> pool = new Pool<>(offset, count);
        return registerPool(poolId, pool);
    }

    public static <E> Pool<E> registerPool(String poolId, Pool<E> pool) {
        if (poolId == null || pool == null)
            return null;

        Map<String, Pool<?>> poolMap = getPoolMap();
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

        Map<String, Pool<?>> poolMap = getPoolMap();
        if (poolMap == null) return null;
        synchronized (poolMap) {
            try {
                return (Pool<E>) poolMap.remove(poolId);
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
            return null;
        }
    }

    public static boolean hasPool(String poolId) {
        if (poolId == null)
            return false;

        Map<String, Pool<?>> poolMap = getPoolMap();
        if (poolMap == null) return false;
        synchronized (poolMap) {
            return poolMap.containsKey(poolId);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> Pool<E> getPool(String poolId, Class<E> poolCls) {
        if (poolId == null || poolCls == null)
            return null;

        Map<String, Pool<?>> poolMap = getPoolMap();
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

        Map<String, Pool<?>> poolMap = getPoolMap();
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

    public static Pool.IdIterator getIdIterator(String poolId) {
        Pool<?> pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return null;
        return pool.getIdIterator();
    }

    public static <E> Pool.EntryIterator<E> getEntryIterator(String poolId) {
        Pool<E> pool = null;
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
        Pool<?> pool = null;
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
        Pool<?> pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return false;
        return pool.hasId(id);
    }

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

    public static <E> boolean set(String poolId, int id, E arg) {
        Pool<E> pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return false;
        return pool.set(id, arg);
    }

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
        Pool<?> pool = null;
        try {
            pool = getPool(poolId);
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
        if (pool == null)
            return false;
        return pool.invoke(id);
    }

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
        public static final int NO_ID = -1;

        private final int mOff, mLen;

        private final List<Entry<E>> mList;
        private int mCursor, mLastCursor;

        public Pool() {
            this(0, 0);
        }

        public Pool(int offset, int count) {
            mOff = Math.max(offset, 0);
            mLen = Math.max(count, 0);

            mList = new ArrayList<>();
            mCursor = 0;
            mLastCursor = -1;
        }

        public final int getMin() {
            return mOff;
        }

        public final int getMax() {
            return mLen > 0
                   ? mOff + mLen - 1
                   : Math.max(mOff, Integer.MAX_VALUE);
        }

        @Override
        public synchronized Iterator<Integer> iterator() {
            return getIdIterator();
        }

        public synchronized IdIterator getIdIterator() {
            return new IdIterator(this);
        }

        public synchronized EntryIterator<E> getEntryIterator() {
            return new EntryIterator<>(this);
        }

        public synchronized int findId() {
            return findId(NO_ID);
        }

        public synchronized int findId(int beginId) {
            int len = mList.size();
            mLastCursor = Math.min(mLastCursor, len - 1);

            int max = mLen <= 0
                      ? mLastCursor
                      : Math.min(mLastCursor, mLen - 1);

            int pos = Math.max(beginId - mOff, 0);
            Entry<E> entry;
            while (pos <= max &&
                    ((entry = mList.get(pos)) == null ||
                            entry.isEmpty()))
                pos++;

            return pos <= max
                   ? pos + mOff
                   : NO_ID;
        }

        public synchronized int findLastId() {
            return findLastId(NO_ID);
        }

        public synchronized int findLastId(int beginId) {
            int len = mList.size();
            mLastCursor = Math.min(mLastCursor, len - 1);

            int pos = mLen <= 0
                      ? mLastCursor
                      : Math.min(mLastCursor, mLen - 1);

            int beginPos = beginId - mOff;
            if (beginPos >= 0)
                pos = Math.min(pos, beginPos);

            Entry<E> entry;
            while (pos >= 0 &&
                    ((entry = mList.get(pos)) == null ||
                            entry.isEmpty()))
                pos--;

            return pos >= 0
                   ? pos + mOff
                   : NO_ID;
        }

        public synchronized void clear() {
            mList.clear();
            mCursor = 0;
            mLastCursor = -1;
        }

        public synchronized boolean hasId(int id) {
            Entry<E> entry = getEntry(id);
            return entry != null && !entry.isEmpty();
        }

        public synchronized E get(int id) {
            Entry<E> entry = getEntry(id);
            return entry != null && !entry.isEmpty() ? entry.getArg() : null;
        }

        public synchronized Entry<E> getEntry(int id) {
            int pos = id - mOff;
            if (pos < 0 || pos >= mList.size())
                return null;
            return mList.get(pos);
        }

        public synchronized boolean set(int id, E arg) {
            Entry<E> entry = getEntry(id);
            if (entry == null || entry.isEmpty())
                return false;

            entry.setArg(arg);
            return true;
        }

        public synchronized E remove(int id) {
            Entry<E> entry = removeEntry(id);
            return entry != null && !entry.isEmpty() ? entry.getArg() : null;
        }

        public synchronized Entry<E> removeEntry(int id) {
            int pos = id - mOff;
            int len;
            if (pos < 0 || pos >= (len = mList.size()))
                return null;

            Entry<E> removedEntry = mList.set(pos, null);

            mCursor = Math.max(Math.min(mCursor, pos), 0);
            mLastCursor = Math.min(mLastCursor, len - 1);

            Entry<E> entry;
            while (mLastCursor >= 0 &&
                    ((entry = mList.get(mLastCursor)) == null ||
                            entry.isEmpty() ||
                            (mLen > 0 &&
                                    mLastCursor >= mLen)))
                mList.remove(mLastCursor--);

            return removedEntry;
        }

        public synchronized int next() {
            return next(null);
        }

        public synchronized int next(E arg) {
            return next(arg, null);
        }

        public synchronized int next(E arg, Entry.Listener<E> listener) {
            mCursor = Math.max(mCursor, 0);

            int len = mList.size();
            Entry<E> entry;
            while (mCursor < len &&
                    (entry = mList.get(mCursor)) != null &&
                    !entry.isEmpty() &&
                    (mLen <= 0 ||
                            mCursor < mLen))
                mCursor++;

            if (mLen <= 0 || mCursor < mLen) {
                mLastCursor = Math.max(mLastCursor, mCursor);

                Entry<E> nextEntry = new Entry<>();
                nextEntry.setEmpty(false);
                nextEntry.setArg(arg);
                nextEntry.setListener(listener);

                if (mCursor >= len)
                    mList.add(mCursor, nextEntry);
                else
                    mList.set(mCursor, nextEntry);

                return mCursor + mOff;
            } else {
                return NO_ID;
            }
        }

        public synchronized boolean invoke(int id) {
            int pos = id - mOff;
            if (pos < 0 || pos >= mList.size())
                return false;

            Entry<E> entry = mList.get(id);
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

        public static class PoolIterator {
            public static final int NO_ID = Pool.NO_ID;

            protected final Pool<?> mPool;

            private int mCursor;
            private boolean mHasNext;

            public PoolIterator(Pool<?> pool) {
                if (pool == null)
                    throw new NullPointerException("No pool attached");

                mPool = pool;

                reset();
            }

            public synchronized final void reset() {
                mCursor = NO_ID;
                mHasNext = false;
            }

            public synchronized final void skip() {
                synchronized (mPool) {
                    int minId = mPool.getMin();

                    int mNextCursor;
                    if (mCursor != NO_ID && mCursor >= minId) {
                        mNextCursor = mPool.findId(mCursor + 1);
                    } else {
                        mNextCursor = mPool.findId(minId);
                    }

                    if (mNextCursor != NO_ID) {
                        mCursor = mNextCursor;
                        mHasNext = true;
                    } else {
                        mHasNext = false;
                    }
                }
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            public synchronized final boolean hasNextId() {
                if (mCursor == NO_ID || !mHasNext)
                    skip();
                return mHasNext;
            }

            public synchronized final int nextId() {
                if (mCursor == NO_ID || !mHasNext)
                    skip();
                if (mCursor == NO_ID || !mHasNext)
                    return NO_ID;

                mHasNext = false;
                return mCursor;
            }
        }

        public static final class IdIterator extends PoolIterator implements Iterator<Integer> {
            public IdIterator(Pool<?> pool) {
                super(pool);
            }

            @Override
            public boolean hasNext() {
                return super.hasNextId();
            }

            @Override
            public Integer next() {
                int id = super.nextId();
                if (id == NO_ID)
                    throw new NoSuchElementException();
                return id;
            }
        }

        @SuppressWarnings("unchecked")
        public static final class EntryIterator<E> extends PoolIterator implements Iterator<Entry<E>> {
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
                if (id == NO_ID)
                    throw new NoSuchElementException();
                return (Entry<E>) mPool.getEntry(id);
            }
        }
    }
}
