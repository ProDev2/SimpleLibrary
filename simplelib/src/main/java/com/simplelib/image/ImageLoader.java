package com.simplelib.image;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class ImageLoader {
    private static final int DEFAULT_LOADER_CAPACITY = 10;
    private static final int DEFAULT_IMAGE_CAPACITY = 100;

    //Statics
    private static ImageLoader loader;

    public static void init() {
        if (loader == null)
            loader = new ImageLoader();
    }

    public static ImageLoader get() {
        init();
        return loader;
    }

    public static void requestImage(ImageRequest request) {
        init();
        loader.request(request);
    }

    public static void requestImages(ArrayList<ImageRequest> requests) {
        init();
        loader.request(requests);
    }

    //Loader
    private ArrayList<Loader> loaderList;
    private int loaderCapacity;

    private ArrayList<ImageRequest> imageList;
    private int imageCapacity;

    private Executor executor;

    public ImageLoader() {
        this.loaderList = new ArrayList<>();
        this.loaderCapacity = DEFAULT_LOADER_CAPACITY;

        this.imageList = new ArrayList<>();
        this.imageCapacity = DEFAULT_IMAGE_CAPACITY;

        calculateLoaderLimit();
    }

    public void calculateLoaderLimit() {
        try {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            int maxLoaderCount = cpuCount * 2;

            setLoaderCapacity(maxLoaderCount);
        } catch (Exception e) {
        }
    }

    public void setLoaderCapacity(int loaderCapacity) {
        this.loaderCapacity = loaderCapacity;
    }

    public void setImageCapacity(int imageCapacity) {
        this.imageCapacity = imageCapacity;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void request(ImageRequest request) {
        ArrayList<ImageRequest> requests = new ArrayList<>();
        requests.add(request);
        request(requests);
    }

    public void request(ArrayList<ImageRequest> requests) {
        try {
            if (requests != null) {
                ArrayList<ImageRequest> requestList = new ArrayList<>();
                for (ImageRequest request : requests) {
                    if (request == null)
                        continue;
                    try {
                        if (fillRequest(request) && request.hasImage())
                            request.onFinish(request.image);
                        else if (!mergeRequest(request) || !request.hasImage())
                            requestList.add(request);
                    } catch (Exception e) {
                    }
                }

                if (requestList.size() > 0) {
                    Loader loader = new Loader(requestList);
                    if (loaderList != null && !loaderList.contains(loader))
                        loaderList.add(loader);
                    loader.start();
                }
            }
        } catch (Exception e) {
        }
    }

    public boolean fillRequest(ImageRequest request) {
        try {
            if (request != null) {
                for (ImageRequest image : imageList) {
                    if (image.isEqualRequest(request) && image.hasImage()) {
                        image.applyTo(request);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean mergeRequest(ImageRequest request) {
        try {
            if (request != null) {
                for (Loader loader : loaderList)
                    if (loader.addMergeRequest(request))
                        return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void removeRequest(String id) {
        removeRequest(findRequestById(id));
    }

    public void removeRequest(ImageRequest request) {
        try {
            if (request != null && imageList.contains(request)) {
                imageList.remove(request);
                if (request.hasImage())
                    request.image.recycle();
            }
        } catch (Exception e) {
        }
    }

    public ImageRequest findRequestById(String id) {
        for (ImageRequest request : imageList) {
            if (request.hasId() && request.getId().equals(id))
                return request;
        }
        return null;
    }

    public boolean hasId(String id) {
        for (ImageRequest request : imageList) {
            if (request.hasId() && request.getId().equals(id))
                return true;
        }
        return false;
    }

    private void pushRequest(ImageRequest request) {
        try {
            if (imageCapacity <= 0) {
                if (imageList.size() > 0)
                    imageList.clear();
                return;
            }

            if (request.storeRequest && !imageList.contains(request))
                imageList.add(0, request);

            if (imageCapacity >= 0 && imageList.size() > imageCapacity) {
                for (int pos = imageList.size() - 1; pos >= imageCapacity; pos--) {
                    if (pos >= 0 && pos < imageList.size()) {
                        try {
                            ImageRequest foundRequest = imageList.get(pos);
                            if (foundRequest != null && foundRequest.hasImage())
                                foundRequest.image.recycle();
                        } catch (Exception e) {
                        }

                        try {
                            imageList.remove(pos);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void startNext() {
        if (loaderList == null) return;
        if (loaderList.size() <= 0) return;

        try {
            boolean found = false;

            Iterator<Loader> loaderIterator = loaderList.iterator();
            while (!found && loaderIterator.hasNext()) {
                Loader loader = loaderIterator.next();
                if (loader != null && loader.start())
                    found = true;
            }
        } catch (Exception e) {
        }
    }

    public void stopAll() {
        try {
            ArrayList<Loader> stopList = new ArrayList<>();
            stopList.addAll(loaderList);
            for (Loader loader : stopList) {
                try {
                    loader.stop();
                } catch (Exception e) {
                }
            }
            stopList.clear();
            loaderList.clear();
        } catch (Exception e) {
        }
    }

    public void clearAll() {
        try {
            ArrayList<ImageRequest> clearList = new ArrayList<>();
            clearList.addAll(imageList);
            for (ImageRequest request : clearList) {
                try {
                    if (request.hasImage())
                        request.image.recycle();
                } catch (Exception e) {
                }
            }
            clearList.clear();
            imageList.clear();
        } catch (Exception e) {
        }
    }

    public void dispatch() {
        stopAll();
        clearAll();
    }

    public static abstract class ImageRequest {
        public static final int NO_RESULT = 0;
        public static final int RESULT_OK = 1;
        public static final int RESULT_ERROR = 2;

        private String id;

        private int resultCode;
        private Bitmap image;

        private boolean storeRequest;

        public ImageRequest() {
            this(null);
        }

        public ImageRequest(String id) {
            this.id = id;

            this.resultCode = NO_RESULT;
            this.image = null;

            this.storeRequest = true;
        }

        public void applyTo(ImageRequest request) {
            if (request != null) {
                request.id = this.id;

                request.resultCode = this.resultCode;
                request.image = this.image;

                request.storeRequest = this.storeRequest;
            }
        }

        public boolean isEqualRequest(ImageRequest request) {
            if (request != null && request.hasId() && hasId())
                return request.id.equals(id);
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ImageRequest)
                return isEqualRequest((ImageRequest) obj);
            return false;
        }

        public String getId() {
            return id;
        }

        public boolean hasId() {
            return id != null;
        }

        public boolean isId(String id) {
            return this.id.equals(id);
        }

        public int getResultCode() {
            return resultCode;
        }

        public boolean hasResultCode(int code) {
            return resultCode == code;
        }

        public Bitmap getImage() {
            return image;
        }

        public boolean hasImage() {
            try {
                return image != null && !image.isRecycled();
            } catch (Exception e) {
            }
            return false;
        }

        public void setStoreRequest(boolean storeRequest) {
            this.storeRequest = storeRequest;
        }

        public abstract Bitmap onLoad();

        public abstract void onFinish(Bitmap image);
    }

    private class Loader extends AsyncTask<Void, Void, Bitmap> {
        private boolean running;

        private ArrayList<ImageRequest> requests;
        private ArrayList<ImageRequest> mergeRequests;

        public Loader(ArrayList<ImageRequest> requests) {
            if (loaderList != null)
                loaderList.add(this);

            if (requests != null)
                this.requests = requests;
            else
                this.requests = new ArrayList<>();
            this.mergeRequests = new ArrayList<>();
        }

        public boolean isRunning() {
            if (isCancelled())
                running = false;

            return running;
        }

        public boolean canStart() {
            try {
                if (isCancelled()) return false;
                if (isRunning()) return false;
            } catch (Exception e) {
            }

            try {
                if (loaderCapacity >= 0 && loaderList != null && loaderList.contains(this)) {
                    int runningCount = 0;

                    Iterator<Loader> loaderIterator = loaderList.iterator();
                    while (loaderIterator.hasNext()) {
                        Loader loader = loaderIterator.next();
                        if (loader != null && loader.isRunning())
                            runningCount++;
                        if (runningCount >= loaderCapacity)
                            return false;
                    }
                }
            } catch (Exception e) {
            }
            return true;
        }

        public boolean start() {
            if (!canStart()) return false;

            try {
                if (!isCancelled() && loaderList.contains(this)) {
                    try {
                        if (executor != null)
                            executeOnExecutor(executor);
                        else
                            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } catch (Exception e) {
                        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    running = true;
                    return true;
                }
            } catch (Exception e) {
            }
            return false;
        }

        public void stop() {
            try {
                running = false;

                if (loaderList != null && loaderList.contains(this))
                    loaderList.remove(this);

                if (!isCancelled())
                    cancel(true);
            } catch (Exception e) {
            }
            dispatch();
        }

        public boolean hasRequest(String id) {
            try {
                for (ImageRequest imageRequest : requests)
                    if (imageRequest.isId(id))
                        return true;
            } catch (Exception e) {
            }
            return false;
        }

        public ImageRequest getRequest(String id) {
            try {
                for (ImageRequest imageRequest : requests)
                    if (imageRequest.isId(id))
                        return imageRequest;
            } catch (Exception e) {
            }
            return null;
        }

        public boolean addMergeRequest(ImageRequest mergeRequest) {
            try {
                if (mergeRequest != null && hasRequest(mergeRequest.id) && !mergeRequests.contains(mergeRequest))
                    mergeRequests.add(mergeRequest);
            } catch (Exception e) {
            }
            return false;
        }

        @Override
        protected Bitmap doInBackground(Void[] args) {
            if (requests == null)
                return null;

            running = true;

            try {
                for (ImageRequest request : requests)
                    handleRequest(request);
            } catch (Exception e) {
            }

            return null;
        }

        private void handleRequest(ImageRequest request) {
            try {
                Bitmap image = request.onLoad();
                if (image != null) {
                    request.image = image;
                    request.resultCode = ImageRequest.RESULT_OK;

                    if (request.storeRequest)
                        pushRequest(request);
                } else {
                    request.image = null;
                    request.resultCode = ImageRequest.NO_RESULT;
                }
            } catch (Exception e) {
                request.image = null;
                request.resultCode = ImageRequest.RESULT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            running = false;

            if (loaderList != null && loaderList.contains(this))
                loaderList.remove(this);

            for (ImageRequest request : requests) {
                try {
                    request.onFinish(request.image);
                } catch (Exception e) {
                }
            }

            for (ImageRequest mergeRequest : mergeRequests) {
                try {
                    ImageRequest request = getRequest(mergeRequest.id);
                    if (request != null)
                        request.applyTo(mergeRequest);
                    mergeRequest.onFinish(mergeRequest.image);
                } catch (Exception e) {
                }
            }
            dispatch();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dispatch();
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            super.onCancelled(bitmap);
            dispatch();
        }

        public void dispatch() {
            try {
                running = false;

                requests.clear();
                mergeRequests.clear();
            } catch (Exception e) {
            }

            startNext();
        }
    }
}