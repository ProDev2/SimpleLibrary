package com.simplelib.loader;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ListLoader<K, V, E> {
    // Flags
    public static final int FLAG_NONE = 1;
    public static final int FLAG_LOAD = 2;
    public static final int FLAG_CLEAR = 4;

    public static final int FLAG_LOAD_IF_EMPTY = 8;
    public static final int FLAG_LOAD_IF_FILLED = 16;

    public static final int FLAG_SKIP_KEY_CHECK = 32;
    public static final int FLAG_SKIP_VALUE_CHECK = 64;

    public static final int FLAG_IGNORE_ABORT = 128;
    public static final int FLAG_IGNORE_ERROR = 256;
    public static final int FLAG_IGNORE_FAILURE = 512;

    public static final int FLAG_DO_NOT_STORE = 1024;

    // Flag usages
    public static final int USAGE_NONE = FLAG_NONE;
    public static final int USAGE_CLEAR = FLAG_NONE | FLAG_CLEAR;
    public static final int USAGE_FILL = FLAG_NONE | FLAG_LOAD_IF_EMPTY;
    public static final int USAGE_LOAD = FLAG_NONE | FLAG_LOAD;
    public static final int USAGE_UPDATE = FLAG_NONE | FLAG_LOAD_IF_FILLED;
    public static final int USAGE_UPDATE_ALL = FLAG_NONE | FLAG_LOAD_IF_FILLED | FLAG_CLEAR;
    public static final int USAGE_RELOAD = FLAG_NONE | FLAG_LOAD | FLAG_CLEAR;

    // Loader states
    public static final int STATE_NONE = Loader.STATE_NONE;
    public static final int STATE_STARTED = Loader.STATE_STARTED;
    public static final int STATE_LOADING = Loader.STATE_LOADING;
    public static final int STATE_ENDED = Loader.STATE_ENDED;
    public static final int STATE_CANCELED = Loader.STATE_CANCELED;
    public static final int STATE_ERROR = Loader.STATE_ERROR;
    public static final int STATE_SUCCESS = Loader.STATE_SUCCESS;

    // Execution
    public static final long NO_EXECUTION_DELAY = -1;

    // ListLoader
    private IMap<K, E> listMap;

    private Looper looper;
    private Handler handler;

    private long lastExecution;
    private boolean lastExecutionRunning;

    private Task task;

    private OnLoadingListener<K, V, E> onLoadingListener;

    public ListLoader() {
        this((IMap<K, E>) null, null);
    }

    public ListLoader(HashMap<K, List<E>> listMap) {
        this(listMap, Looper.getMainLooper());
    }

    public ListLoader(HashMap<K, List<E>> listMap, Looper looper) {
        this(listMap != null ? new ListMapWrapper<>(listMap) : null, looper);
    }

    public ListLoader(IMap<K, E> listMap) {
        this(listMap, Looper.getMainLooper());
    }

    public ListLoader(IMap<K, E> listMap, Looper looper) {
        if (listMap == null)
            listMap = new ListMap<>();
        this.listMap = listMap;

        this.looper = looper;
        if (this.looper != null)
            this.handler = new Handler(this.looper);
        else
            this.handler = new Handler();
        this.lastExecution = -1;
    }

    public final IMap<K, E> getListMap() {
        synchronized (listMap) {
            return listMap;
        }
    }

    public void setListMap(IMap<K, E> listMap) {
        if (listMap != null) {
            synchronized (listMap) {
                this.listMap = null;
            }
        }

        if (listMap == null)
            listMap = new ListMap<>();
        this.listMap = listMap;
    }

    public final void setOnLoadingListener(OnLoadingListener<K, V, E> onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }

    public final boolean canExecute(final long lockDelay, final boolean lockIfRunning) {
        return execute(null, lockDelay, lockIfRunning);
    }

    public final boolean execute(final Runnable runnable, final long lockDelay, final boolean lockIfRunning) {
        final Runnable executionRunnable = new Runnable() {
            @Override
            public void run() {
                lastExecutionRunning = true;

                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                lastExecutionRunning = false;
            }
        };

        long time = 0;
        try {
            time = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (handler == null) {
            if (runnable != null) {
                lastExecution = time;
                lastExecutionRunning = true;

                boolean executedOnHandler = runOnHandler(executionRunnable);
                if (!executedOnHandler) {
                    lastExecutionRunning = false;
                }
            }
            return true;
        }

        synchronized (handler) {
            boolean canRun = lockDelay <= 0 ||
                    lastExecution < 0 ||
                    lastExecution + lockDelay <= time;
            canRun &= !(lastExecutionRunning && lockIfRunning);

            if (canRun) {
                if (runnable != null) {
                    lastExecution = time;
                    lastExecutionRunning = true;

                    boolean executedOnHandler = runOnHandler(executionRunnable);
                    if (!executedOnHandler) {
                        lastExecutionRunning = false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final boolean runOnHandler(final Runnable runnable) {
        if (runnable == null)
            return false;

        boolean executedOnHandler = false;
        if (handler != null) {
            try {
                executedOnHandler = handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runnable.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!executedOnHandler) {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public final void clearLists() {
        synchronized (listMap) {
            HashMap<K, List<E>> tempListMap = new HashMap<>();
            try {
                listMap.applyTo(tempListMap);
                listMap.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Map.Entry<K, List<E>> listEntry : tempListMap.entrySet()) {
                if (listEntry == null) continue;

                K key = listEntry.getKey();
                List<E> list = listEntry.getValue();

                try {
                    onListRemoved(key, list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            tempListMap.clear();
        }
    }

    public final boolean hasList(K key) {
        synchronized (listMap) {
            try {
                return listMap.contains(key);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public final List<E> getList(K key, boolean createIfNeeded) {
        synchronized (listMap) {
            List<E> list = null;
            try {
                if (listMap.contains(key))
                    list = listMap.getKey(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (createIfNeeded && list == null) {
                list = new ArrayList<>();
                try {
                    listMap.putKey(key, list);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    onListCreated(key, list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
    }

    public final boolean removeList(K key) {
        synchronized (listMap) {
            if (hasList(key)) {
                List<E> list = null;
                try {
                    list = listMap.removeKey(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (list != null) {
                    try {
                        onListRemoved(key, list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public final boolean has(int v, int c) {
        return (v & c) == c;
    }

    public final synchronized boolean isStarted() {
        if (task == null)
            return false;

        return task.isStarted();
    }

    public final synchronized boolean isLoading() {
        if (task == null)
            return false;

        return task.isLoading();
    }

    public final synchronized Task load(K key, V value, int flags) {
        return load(key, value, flags, null, null);
    }

    public final synchronized Task load(K key, V value, int flags, OnLoadingListener<K, V, E> onLoadingListener) {
        return load(key, value, flags, null, onLoadingListener);
    }

    public final synchronized Task load(K key, V value, int flags, Comparator<E> comparator) {
        return load(key, value, flags, comparator, null);
    }

    public final synchronized Task load(K key, V value, int flags, Comparator<E> comparator, OnLoadingListener<K, V, E> onLoadingListener) {
        boolean loading = true;
        try {
            flags = onModifyFlags(flags);

            key = onModifyKey(key);
            value = onModifyValue(value);

            comparator = onModifyComparator(comparator);

            if (loading && !has(flags, FLAG_SKIP_KEY_CHECK) && !isKeyLoadable(key, value))
                loading = false;
            if (loading && !has(flags, FLAG_SKIP_VALUE_CHECK) && !isValueLoadable(key, value))
                loading = false;
        } catch (Exception e) {
            e.printStackTrace();
            loading = false;
        }

        if (loading) {
            release();
            task = null;

            boolean createListIfNeeded = !has(flags, FLAG_DO_NOT_STORE);
            List<E> list = getList(key, createListIfNeeded);

            task = new Task(flags, key, value, list, comparator, onLoadingListener);
            try {
                task.start();
            } catch (Exception e) {
                e.printStackTrace();

                release();
                task = null;

                loading = false;
            }
        }

        try {
            if (onLoadingListener != null)
                onLoadingListener.onListLoading(loading, flags, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ListLoader.this.onListLoading(loading, flags, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return loading ? task : null;
    }

    public final synchronized boolean cancel() {
        if (task == null)
            return false;

        boolean canceled = false;
        if (task.isLoading()) {
            try {
                task.cancel();
                canceled = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return canceled;
    }

    public final synchronized boolean release() {
        if (task == null)
            return false;

        boolean released = false;
        try {
            released = cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        task = null;

        return released;
    }

    public final void throwIfInvalidThread() {
        if (task == null)
            throw new IllegalStateException("No current loader");
        task.throwIfInvalidThread();
    }

    public final void throwIfNotLoading() {
        if (task == null)
            throw new IllegalStateException("No current loader");
        task.throwIfNotLoading();
    }

    public final void throwIfNotLoaderThread() {
        if (task == null)
            throw new IllegalStateException("No current loader");
        task.throwIfNotLoaderThread();
    }

    public final boolean isLoaderThread() {
        if (task == null)
            return false;
        return task.isLoaderThread();
    }

    protected void onListCreated(K key, List<E> list) {

    }

    protected void onListRemoved(K key, List<E> list) {

    }

    protected void onListLoading(boolean success, int flags, K key, V value) {
        if (onLoadingListener != null)
            onLoadingListener.onListLoading(success, flags, key, value);
    }

    protected void onListChanged(K key, V value, List<E> list) {
        if (onLoadingListener != null)
            onLoadingListener.onListChanged(key, value, list);
    }

    protected void onListLoaded(boolean success, K key, V value, List<E> list) {
        if (onLoadingListener != null)
            onLoadingListener.onListLoaded(success, key, value, list);
    }

    protected void onDispatchLoader(K key, Task task) {
        release();
    }

    protected int onModifyFlags(int flags) {
        return flags;
    }

    protected K onModifyKey(K key) {
        return key;
    }

    protected V onModifyValue(V value) {
        return value;
    }

    protected Comparator<E> onModifyComparator(Comparator<E> comparator) {
        return comparator;
    }

    protected boolean isKeyLoadable(K key, V value) {
        return true;
    }

    protected boolean isValueLoadable(K key, V value) {
        return true;
    }

    protected void addElementsToList(List<E> srcList, List<E> list, boolean makeStorable) {
        if (list == null)
            return;

        synchronized (list) {
            list.clear();
            if (srcList != null) {
                synchronized (srcList) {
                    list.addAll(srcList);
                }
            }
        }
    }

    protected abstract boolean onLoad(Task task, K key, V value, ListInterface<E> listInterface);

    protected final ListInterface<E> getListInterface() {
        throwIfNotLoaderThread();

        return task.getListInterface();
    }

    protected final boolean publish() {
        throwIfNotLoaderThread();

        return task.publish();
    }

    protected final boolean publish(long lockDelay) {
        throwIfNotLoaderThread();

        return task.publish(lockDelay);
    }

    protected final boolean publish(long lockDelay, boolean lockIfRunning) {
        throwIfNotLoaderThread();

        return task.publish(lockDelay, lockIfRunning);
    }

    protected final boolean publish(long lockDelay, boolean lockIfRunning, boolean copyList) {
        throwIfNotLoaderThread();

        return task.publish(lockDelay, lockIfRunning, copyList);
    }

    // Listloader task
    public final class Task extends Loader {
        private final int flags;

        private final K key;
        private final V value;

        private final List<E> srcList;

        private final List<E> list;
        private final List<E> tempList;

        private final Comparator<E> comparator;

        private final ListInterface<E> listInterface;

        private OnLoadingListener<K, V, E> onLoadingListener;

        private Task(int flags, K key, V value, List<E> srcList) {
            this(flags, key, value, srcList, null);
        }

        private Task(int flags, K key, V value, List<E> srcList, Comparator<E> comparator) {
            this(flags, key, value, srcList, comparator, null);
        }

        private Task(int flags, K key, V value, List<E> srcList, Comparator<E> comparator, OnLoadingListener<K, V, E> onLoadingListener) {
            this.flags = flags;

            this.key = key;
            this.value = value;

            this.srcList = srcList;

            this.list = new ArrayList<>();
            this.tempList = new ArrayList<>();

            this.comparator = comparator;

            this.listInterface = new ListInterface<E>(this.list, this.comparator) {
                @Override
                protected void onInvokeInterface() {
                    throwIfNotLoaderThread();
                }
            };

            this.onLoadingListener = onLoadingListener;
        }

        public final int getFlags() {
            return flags;
        }

        public final boolean hasFlag(int flag) {
            return (this.flags & flag) == flag;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final List<E> getList() {
            return list;
        }

        public final List<E> getTempList() {
            return tempList;
        }

        public final Comparator<E> getComparator() {
            return comparator;
        }

        public final ListInterface<E> getListInterface() {
            return listInterface;
        }

        public final void setOnLoadingListener(OnLoadingListener<K, V, E> onLoadingListener) {
            this.onLoadingListener = onLoadingListener;
        }

        @Override
        protected final boolean onLoad() {
            if (!isLoaderThread())
                return false;

            if (!isLoading() || isEnded())
                return false;

            boolean success = true;

            final boolean canLoad = hasFlag(FLAG_LOAD) ||
                    (hasFlag(FLAG_LOAD_IF_EMPTY) && (srcList == null || srcList.isEmpty())) ||
                    (hasFlag(FLAG_LOAD_IF_FILLED) && (srcList != null && !srcList.isEmpty()));

            if (isLoading() && list != null) {
                synchronized (list) {
                    list.clear();
                }
            }

            if (isLoading() && !hasFlag(FLAG_CLEAR) && srcList != null && list != null) {
                synchronized (srcList) {
                    synchronized (list) {
                        try {
                            ListLoader.this.addElementsToList(srcList, list, false);

                            if (comparator != null)
                                Collections.sort(list, comparator);
                        } catch (Exception e) {
                            e.printStackTrace();
                            list.clear();
                        }
                    }
                }
            }

            if (isLoading() && canLoad) {
                try {
                    success = ListLoader.this.onLoad(this, key, value, listInterface);

                    if (!success && !hasFlag(FLAG_IGNORE_FAILURE) && list != null) {
                        synchronized (list) {
                            list.clear();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    success = false;

                    if (!hasFlag(FLAG_IGNORE_ERROR) && list != null) {
                        synchronized (list) {
                            list.clear();
                        }
                    }
                }
            }

            if (!isLoading() && !hasFlag(FLAG_IGNORE_ABORT) && list != null) {
                synchronized (list) {
                    list.clear();
                }
            }

            if (!hasFlag(FLAG_DO_NOT_STORE) && (isLoading() || hasFlag(FLAG_IGNORE_ABORT)) && srcList != null && list != null) {
                synchronized (srcList) {
                    synchronized (list) {
                        try {
                            ListLoader.this.addElementsToList(list, srcList, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            srcList.clear();
                        }
                    }
                }
            }

            return success;
        }

        @Override
        protected final void onFinish(final boolean success) {
            runOnHandler(new Runnable() {
                @Override
                public void run() {
                    try {
                        onListLoaded(success, key, value, list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public final boolean publish() {
            return publish(NO_EXECUTION_DELAY);
        }

        public final boolean publish(long lockDelay) {
            return publish(lockDelay, true);
        }

        public final boolean publish(long lockDelay, boolean lockIfRunning) {
            return publish(lockDelay, lockIfRunning, true);
        }

        public final boolean publish(final long lockDelay, final boolean lockIfRunning, final boolean copyList) {
            throwIfNotLoaderThread();

            if (!canExecute(lockDelay, lockIfRunning))
                return false;

            if (copyList && list != null && tempList != null) {
                synchronized (list) {
                    synchronized (tempList) {
                        try {
                            tempList.clear();
                            tempList.addAll(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!copyList)
                            onListChanged(key, value, list);
                        else
                            onListChanged(key, value, tempList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, lockDelay, lockIfRunning);
        }

        private void onListChanged(K key, V value, List<E> list) {
            try {
                if (onLoadingListener != null)
                    onLoadingListener.onListChanged(key, value, list);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ListLoader.this.onListChanged(key, value, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void onListLoaded(boolean success, K key, V value, List<E> list) {
            try {
                if (onLoadingListener != null)
                    onLoadingListener.onListLoaded(success, key, value, list);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ListLoader.this.onListLoaded(success, key, value, list);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ListLoader.this.onDispatchLoader(key, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Listloader loader
    public static abstract class Loader implements Runnable {
        // States
        public static final int STATE_NONE = 1;
        public static final int STATE_STARTED = 2;
        public static final int STATE_LOADING = 4;
        public static final int STATE_ENDED = 8;
        public static final int STATE_CANCELED = 16;
        public static final int STATE_ERROR = 32;
        public static final int STATE_SUCCESS = 64;

        // Loader
        private Thread thread;
        private int state;

        public Loader() {
            try {
                thread = new Thread(this);
                state = STATE_NONE;
            } catch (Exception e) {
                e.printStackTrace();

                state = STATE_ENDED | STATE_ERROR;
            }
        }

        public final void start() {
            if (isEnded())
                throw new IllegalStateException("Loader was already used");
            if (isLoading() || isStarted())
                throw new IllegalStateException("Loader is already started");

            try {
                thread.start();
                state = STATE_STARTED;
            } catch (Exception e) {
                e.printStackTrace();

                state = STATE_ENDED | STATE_ERROR;
            }
        }

        public final void cancel() {
            if (!isStarted())
                throw new IllegalStateException("Loader was never started");
            if ((!isStarted() && !isLoading()) || isEnded())
                throw new IllegalStateException("Loader is not running");

            try {
                thread.interrupt();
                state |= STATE_ENDED | STATE_CANCELED;
            } catch (Exception e) {
                e.printStackTrace();

                state |= STATE_ENDED | STATE_ERROR;
            }
        }

        public final int getState() {
            return state;
        }

        public final boolean isState(int state) {
            return (this.state & state) == state;
        }

        public final boolean isStarted() {
            return !isState(STATE_NONE) && isState(STATE_STARTED);
        }

        public final boolean isLoading() {
            return isState(STATE_LOADING) && !isState(STATE_ENDED);
        }

        public final boolean isEnded() {
            return !isState(STATE_LOADING) && isState(STATE_ENDED);
        }

        public final boolean isCanceled() {
            return isState(STATE_ENDED) && isState(STATE_CANCELED);
        }

        public final boolean isError() {
            return isState(STATE_ENDED) && isState(STATE_ERROR);
        }

        public final boolean isSuccess() {
            return isState(STATE_ENDED) && isState(STATE_SUCCESS);
        }

        public final void throwIfInvalidThread() {
            throwIfNotLoading();
            throwIfNotLoaderThread();
        }

        public final void throwIfNotLoading() {
            if (!isLoading())
                throw new IllegalStateException("Loader is no longer loading");
        }

        public final void throwIfNotLoaderThread() {
            if (!isLoaderThread())
                throw new IllegalStateException("Not running on loader thread");
        }

        public final boolean isLoaderThread() {
            try {
                if (thread != Thread.currentThread())
                    return false;
            } catch (Exception e) {
            }

            return true;
        }

        @Override
        public final void run() {
            if (!isStarted() ||
                    isLoading()) return;

            if (!isEnded()) {
                state |= STATE_LOADING;

                try {
                    boolean success = onLoad();

                    if (!isEnded()) {
                        if (success) {
                            state |= STATE_ENDED | STATE_SUCCESS;
                        } else {
                            state |= STATE_ENDED;
                        }
                    } else {
                        try {
                            System.gc();
                        } catch (Exception ge) {
                        }
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();

                    try {
                        System.gc();
                    } catch (Exception ge) {
                    }

                    state |= STATE_ENDED | STATE_ERROR;
                } catch (Exception e) {
                    e.printStackTrace();

                    state |= STATE_ENDED | STATE_ERROR;
                }

                state &= ~STATE_LOADING;
            }

            if (isLoading() || !isEnded())
                return;

            try {
                onFinish(isSuccess());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected abstract boolean onLoad();
        protected abstract void onFinish(boolean success);
    }

    // Listloader list interface
    public static abstract class ListInterface<E> implements Iterable<E> {
        private final List<E> list;
        private final Comparator<E> comparator;

        public ListInterface() {
            this(null);
        }

        public ListInterface(List<E> list) {
            this(list, null);
        }

        public ListInterface(List<E> list, Comparator<E> comparator) {
            if (list == null)
                list = new ArrayList<>();
            this.list = list;
            this.comparator = comparator;
        }

        @Override
        public final Iterator<E> iterator() {
            onInvokeInterface();

            if (list == null)
                return null;

            final Iterator<E> listIterator = list.iterator();

            return new Iterator<E>() {
                @Override
                public boolean hasNext() {
                    synchronized (list) {
                        return listIterator.hasNext();
                    }
                }

                @Override
                public E next() {
                    synchronized (list) {
                        return listIterator.next();
                    }
                }

                @Override
                public void remove() {
                    synchronized (list) {
                        listIterator.remove();
                    }
                }
            };
        }

        public final List<E> getList() {
            onInvokeInterface();

            return list;
        }

        public final Comparator<E> getComparator() {
            onInvokeInterface();

            return comparator;
        }

        public final boolean modify(Runnable runnable) {
            onInvokeInterface();

            if (list == null || runnable == null)
                return false;

            synchronized (list) {
                try {
                    runnable.run();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        public final boolean sort() {
            onInvokeInterface();

            if (list == null || comparator == null)
                return false;

            synchronized (list) {
                ArrayList<E> tempList = new ArrayList<>();

                try {
                    tempList.addAll(list);

                    Collections.sort(tempList, comparator);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    try {
                        list.clear();
                        list.addAll(tempList);
                        tempList.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }

        public final boolean addSorted(E element) {
            return addSorted(element, true);
        }

        public final boolean addSorted(E element, boolean canIgnoreSorting) {
            onInvokeInterface();

            if (list == null || (comparator == null && !canIgnoreSorting))
                return false;

            synchronized (list) {
                if (comparator != null && element != null) {
                    try {
                        int pos = Collections.binarySearch(list, element, comparator);
                        if (pos < 0)
                            pos = ~pos;

                        list.add(pos, element);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (canIgnoreSorting) {
                    try {
                        return list.add(element);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        public final boolean contains(E element) {
            onInvokeInterface();

            if (list == null)
                return false;

            synchronized (list) {
                try {
                    return list.contains(element);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        public final int indexOf(E element) {
            onInvokeInterface();

            if (list == null)
                return -1;

            synchronized (list) {
                try {
                    return list.indexOf(element);
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        }

        public final void clear() {
            onInvokeInterface();

            if (list == null)
                return;

            synchronized (list) {
                try {
                    list.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public final int size() {
            onInvokeInterface();

            if (list == null)
                return 0;

            synchronized (list) {
                try {
                    return list.size();
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }

        public final boolean isEmpty() {
            onInvokeInterface();

            if (list == null)
                return true;

            synchronized (list) {
                try {
                    return list.isEmpty();
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            }
        }

        public final E get(int index) {
            onInvokeInterface();

            if (list == null)
                return null;

            synchronized (list) {
                try {
                    return list.get(index);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        public final boolean add(E element) {
            onInvokeInterface();

            if (list == null)
                return false;

            synchronized (list) {
                try {
                    return list.add(element);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        public final void add(int index, E element) {
            onInvokeInterface();

            if (list == null)
                return;

            synchronized (list) {
                try {
                    list.add(index, element);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public final boolean addAll(Collection<E> elements) {
            onInvokeInterface();

            if (list == null || elements == null)
                return false;

            synchronized (list) {
                try {
                    return list.addAll(elements);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        public final boolean addAll(int index, Collection<E> elements) {
            onInvokeInterface();

            if (list == null || elements == null)
                return false;

            synchronized (list) {
                try {
                    return list.addAll(index, elements);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        public final E set(int index, E element) {
            onInvokeInterface();

            if (list == null)
                return null;

            synchronized (list) {
                try {
                    return list.set(index, element);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        public final boolean remove(E element) {
            onInvokeInterface();

            if (list == null)
                return false;

            synchronized (list) {
                try {
                    return list.remove(element);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        public final E remove(int index) {
            onInvokeInterface();

            if (list == null)
                return null;

            synchronized (list) {
                try {
                    return list.remove(index);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        public final boolean removeAll(Collection<?> elements) {
            onInvokeInterface();

            if (list == null || elements == null)
                return false;

            synchronized (list) {
                try {
                    return list.removeAll(elements);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        public final boolean retainAll(Collection<?> elements) {
            onInvokeInterface();

            if (list == null || elements == null)
                return false;

            synchronized (list) {
                try {
                    return list.retainAll(elements);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        protected abstract void onInvokeInterface();
    }

    // Listloader map
    public interface IMap<K, E> {
        void applyTo(Map<K, List<E>> map);

        boolean contains(K key);
        void clear();
        List<E> getKey(K key);
        List<E> putKey(K key, List<E> list);
        List<E> removeKey(K key);
    }

    // Listloader list map
    public static class ListMap<K, E> extends HashMap<K, List<E>> implements IMap<K, E> {
        public ListMap() {
        }

        public ListMap(Map<? extends K, ? extends List<E>> map) {
            super(map);
        }

        @Override
        public synchronized void applyTo(Map<K, List<E>> map) {
            try {
                if (map != null) {
                    synchronized (map) {
                        map.clear();
                        map.putAll(this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public synchronized boolean contains(K key) {
            try {
                return containsKey(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public synchronized void clear() {
            try {
                clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public synchronized List<E> getKey(K key) {
            try {
                return get(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<E> putKey(K key, List<E> list) {
            try {
                return put(key, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<E> removeKey(K key) {
            try {
                return remove(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // Listloader map wrapper
    public static class ListMapWrapper<K, E> implements IMap<K, E> {
        private final Map<K, List<E>> listMap;

        public ListMapWrapper(Map<K, List<E>> listMap) {
            if (listMap == null)
                throw new NullPointerException("No list map");
            this.listMap = listMap;
        }

        public Map<K, List<E>> getListMap() {
            synchronized (listMap) {
                return listMap;
            }
        }

        @Override
        public void applyTo(Map<K, List<E>> map) {
            try {
                if (map != null) {
                    synchronized (listMap) {
                        synchronized (map) {
                            map.clear();
                            map.putAll(listMap);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean contains(K key) {
            try {
                synchronized (listMap) {
                    return listMap.containsKey(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void clear() {
            try {
                synchronized (listMap) {
                    listMap.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public List<E> getKey(K key) {
            try {
                synchronized (listMap) {
                    return listMap.get(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<E> putKey(K key, List<E> list) {
            try {
                synchronized (listMap) {
                    return listMap.put(key, list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<E> removeKey(K key) {
            try {
                synchronized (listMap) {
                    return listMap.remove(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    // Listloader loading listener
    public interface OnLoadingListener<K, V, E> {
        void onListLoading(boolean success, int flags, K key, V value);
        void onListChanged(K key, V value, List<E> list);
        void onListLoaded(boolean success, K key, V value, List<E> list);
    }
}