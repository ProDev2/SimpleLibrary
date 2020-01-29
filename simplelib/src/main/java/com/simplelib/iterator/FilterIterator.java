package com.simplelib.iterator;

import java.util.Iterator;

public abstract class FilterIterator<E> implements Iterator<E> {
    protected int posNext = -1;
    protected int posLast = -1;

    protected boolean skipNull;

    public FilterIterator() {
        this.skipNull = false;
    }

    public FilterIterator(boolean skipNull) {
        this.skipNull = skipNull;
    }

    protected synchronized void setNextPos(int posNext) {
        this.posNext = posNext;
        this.posLast = -1;
    }

    protected synchronized void setLastPos(int posLast) {
        if (posLast >= posNext)
            return;

        this.posLast = posLast;
    }

    protected synchronized void setSkipNull(boolean skipNull) {
        this.skipNull = skipNull;
    }

    @Override
    public synchronized boolean hasNext() {
        int count = getCount();
        if (count <= 0)
            return false;

        boolean hasNext = false;

        E element;
        while (posNext < 0 ||
                posNext < count &&
                        (((element = get(posNext)) == null &&
                                skipNull) ||
                                !(hasNext = include(posNext, element))))
            posNext++;

        return hasNext;
    }

    @Override
    public synchronized E next() {
        if (!hasNext())
            throw new IllegalStateException("No more elements");

        return get(posLast = posNext++);
    }

    public synchronized E peek() {
        return peek(null);
    }

    public synchronized E peek(E defElement) {
        if (!hasNext())
            return defElement;

        return get(posNext);
    }

    @Override
    public synchronized void remove() {
        int count = getCount();
        if (posLast < 0 || posLast >= count)
            throw new IllegalStateException("No element to remove");
        if (posLast >= posNext)
            throw new IllegalStateException("Invalid remove position");

        boolean removed = remove(posLast);
        if (removed) {
            posNext = posLast;
            posLast = -1;
        } else {
            throw new IllegalStateException("No element was removed");
        }
    }

    public synchronized int skip(int count) {
        int skipCount;
        for (skipCount = 0;
             skipCount < count &&
                     hasNext();
             skipCount++)
            posNext++;
        return skipCount;
    }

    public synchronized void reset() {
        posNext = -1;
        posLast = -1;
    }

    protected abstract int getCount();
    protected abstract E get(int pos);

    protected boolean remove(int pos) {
        return false;
    }

    protected abstract boolean include(int pos, E element);

    public static abstract class ArrayIterator<E> extends FilterIterator<E> {
        protected E[] elements;

        public ArrayIterator(E[] elements) {
            super();

            this.elements = elements;
        }

        public ArrayIterator(E[] elements, boolean skipNull) {
            super(skipNull);

            this.elements = elements;
        }

        protected synchronized void setElements(E[] elements) {
            reset();

            this.elements = elements;
        }

        @Override
        protected int getCount() {
            return elements != null
                    ? elements.length
                    : 0;
        }

        @Override
        protected E get(int pos) {
            if (elements == null)
                throw new IllegalStateException("No elements");

            return elements[pos];
        }
    }

    public static abstract class ListIterator<E> extends FilterIterator<E> {
        protected java.util.List<E> elements;

        public ListIterator(java.util.List<E> elements) {
            super();

            this.elements = elements;
        }

        public ListIterator(java.util.List<E> elements, boolean skipNull) {
            super(skipNull);

            this.elements = elements;
        }

        protected synchronized void setElements(java.util.List<E> elements) {
            reset();

            this.elements = elements;
        }

        @Override
        protected int getCount() {
            return elements != null
                    ? elements.size()
                    : 0;
        }

        @Override
        protected E get(int pos) {
            if (elements == null)
                throw new IllegalStateException("No elements");

            return elements.get(pos);
        }
    }
}