package com.simplelib.loader;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleLoader<E extends Loader> {
    private ArrayList<E> list;

    private Runnable preListener;
    private Runnable postListener;

    public SimpleLoader() {
        this.list = new ArrayList<>();
    }

    public SimpleLoader(ArrayList<E> list) {
        this.list = list;
    }

    public SimpleLoader addItem(E item) {
        this.list.add(item);

        return this;
    }

    public SimpleLoader addItems(E... item) {
        this.list.addAll(Arrays.asList(item));

        return this;
    }

    public SimpleLoader addItems(ArrayList<E> list) {
        this.list.addAll(list);

        return this;
    }

    public SimpleLoader setPreListener(Runnable listener) {
        this.preListener = listener;

        return this;
    }

    public SimpleLoader setPostListener(Runnable listener) {
        this.postListener = listener;

        return this;
    }

    public SimpleLoader load() {
        new Task().execute();

        return this;
    }

    private class Task extends AsyncTask<String, Loader, String> {
        @Override
        protected void onPreExecute() {
            try {
                if (preListener != null)
                    preListener.run();
            } catch (Exception e) {
            }

            for (E item : list) {
                try {
                    item.preLoad();
                } catch (Exception e) {
                }
            }
        }

        @Override
        protected String doInBackground(String[] values) {
            for (E item : list) {
                try {
                    item.load();
                } catch (Exception e) {
                }

                publishProgress(item);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Loader[] values) {
            try {
                Loader loader = values[0];
                loader.publish();
            } catch (Exception e) {
            }
        }

        @Override
        protected void onPostExecute(String result) {
            for (E item : list) {
                try {
                    item.postLoad();
                } catch (Exception e) {
                }
            }

            try {
                if (postListener != null)
                    postListener.run();
            } catch (Exception e) {
            }
        }
    }
}
