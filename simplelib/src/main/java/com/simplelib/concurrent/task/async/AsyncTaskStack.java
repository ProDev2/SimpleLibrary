/*
 * Copyright (c) 2020 ProDev+ (Pascal Gerner).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplelib.concurrent.task.async;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.simplelib.concurrent.util.ExecutorHelper;
import com.simplelib.concurrent.task.executor.ExecutorTaskStack;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public class AsyncTaskStack extends ExecutorTaskStack {
    @Nullable
    public Handler mHandler;

    public AsyncTaskStack() {
        this(null, null, null);
    }

    public AsyncTaskStack(@Nullable Executor executor,
                          @Nullable Handler handler) {
        this(executor, handler, null);
    }

    protected AsyncTaskStack(@Nullable Executor executor,
                             @Nullable Handler handler,
                             @Nullable Object lock) {
        super(executor, lock);

        mHandler = handler;
    }

    @Override
    public void close() {
        synchronized (mLock) {
            try {
                super.close();
            } finally {
                mHandler = null;
            }
        }
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    protected void onPostExecute(@NonNull Runnable runnable) throws Exception {
        synchronized (mLock) {
            throwIfClosed();
            Handler handler = mHandler;
            if (handler == null) {
                throw new NullPointerException("No handler attached");
            }
            handler.post(runnable);
        }
    }

    /* -------- Initialization -------- */
    @NonNull
    public static AsyncTaskStack with(@Nullable Executor executor) {
        return with(executor, true);
    }

    @NonNull
    public static AsyncTaskStack with(@Nullable Executor executor, boolean isShared) {
        Looper looper = Looper.myLooper();
        if (looper == null) looper = Looper.getMainLooper();

        AsyncTaskStack stack = new AsyncTaskStack();
        stack.mExecutor = executor;
        stack.mShutdown = !isShared;
        stack.mHandler = new Handler(looper);
        return stack;
    }

    @NonNull
    public static AsyncTaskStack create() {
        return with(ExecutorHelper.create(), false);
    }

    @NonNull
    public static AsyncTaskStack create(int maxPoolSize) {
        return with(ExecutorHelper.create(
                maxPoolSize
        ), false);
    }

    @NonNull
    public static AsyncTaskStack create(int maxPoolSize,
                                        long keepAliveTime) {
        return with(ExecutorHelper.create(
                maxPoolSize,
                keepAliveTime
        ), false);
    }

    @NonNull
    public static AsyncTaskStack create(int corePoolSize,
                                        int maxPoolSize,
                                        long keepAliveTime) {
        return with(ExecutorHelper.create(
                corePoolSize,
                maxPoolSize,
                keepAliveTime
        ), false);
    }
}
