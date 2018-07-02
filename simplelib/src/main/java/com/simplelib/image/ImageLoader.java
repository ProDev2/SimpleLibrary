package com.simplelib.image;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.ArrayList;

public class ImageLoader {
    private static final int DEFAULT_CAPACITY = 100;

    //Statics
    private static ImageLoader loader;
    //Loader
    private ArrayList<Loader> loaderList;
    private ArrayList<ImageRequest> imageList;
    private int capacity;

    public ImageLoader() {
        this.loaderList = new ArrayList<>();

        this.imageList = new ArrayList<>();
        this.capacity = DEFAULT_CAPACITY;

        if (loader == null)
            loader = this;
    }

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

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void request(ImageRequest request) {
        ArrayList<ImageRequest> requests = new ArrayList<>();
        requests.add(request);
        request(requests);
    }

    public void request(ArrayList<ImageRequest> requests) {
        if (requests != null) {
            ArrayList<ImageRequest> requestList = new ArrayList<>();
            for (ImageRequest request : requests) {
                if (request == null)
                    continue;
                try {
                    boolean filled = fillRequest(request);
                    if (filled)
                        request.onFinish(request.image);
                    else
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
    }

    public boolean fillRequest(ImageRequest request) {
        try {
            if (request != null) {
                for (ImageRequest image : imageList) {
                    if (image.isEqualRequest(request)) {
                        image.applyTo(request);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
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

    public void pushRequest(ImageRequest request) {
        if (request.storeRequest && !imageList.contains(request))
            imageList.add(0, request);

        if (capacity >= 0 && imageList.size() > capacity) {
            for (int pos = imageList.size() - 1; pos >= capacity; pos--) {
                try {
                    if (pos >= 0 && pos < imageList.size()) {
                        ImageRequest foundRequest = imageList.get(pos);
                        if (foundRequest != null && foundRequest.hasImage())
                            foundRequest.image.recycle();

                        imageList.remove(pos);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public void stopAll() {
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
    }

    public void clearAll() {
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
    }

    public void dispatch() {
        stopAll();
        clearAll();
        loader = null;
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

        public String getId() {
            return id;
        }

        public boolean hasId() {
            return id != null;
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
            return image != null;
        }

        public void setStoreRequest(boolean storeRequest) {
            this.storeRequest = storeRequest;
        }

        public abstract Bitmap onLoad();

        public abstract void onFinish(Bitmap image);
    }

    private class Loader extends AsyncTask<Void, Void, Bitmap> {
        private ArrayList<ImageRequest> requests;

        public Loader(ArrayList<ImageRequest> requests) {
            if (loaderList != null)
                loaderList.add(this);

            if (requests != null)
                this.requests = requests;
            else
                this.requests = new ArrayList<>();
        }

        public void start() {
            try {
                if (!isCancelled())
                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e) {
            }
        }

        public void stop() {
            try {
                if (loaderList != null && loaderList.contains(this))
                    loaderList.remove(this);

                if (!isCancelled())
                    cancel(true);
            } catch (Exception e) {
            }
        }

        @Override
        protected Bitmap doInBackground(Void[] args) {
            if (requests == null)
                return null;

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
            if (loaderList != null && loaderList.contains(this))
                loaderList.remove(this);

            for (ImageRequest request : requests) {
                try {
                    request.onFinish(request.image);
                } catch (Exception e) {
                }
            }
        }
    }
}