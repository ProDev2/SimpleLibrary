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
import com.simplelib.concurrent.task.executor.ExecutorTaskSpawner;
import com.simplelib.concurrent.util.ExecutorHelper;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public class AsyncTaskSpawner extends ExecutorTaskSpawner {
    @Nullable
    public Handler mHandler;

    public AsyncTaskSpawner() {
        this(null, null, null);
    }

    public AsyncTaskSpawner(@Nullable Executor executor,
                            @Nullable Handler handler) {
        this(executor, handler, null);
    }

    protected AsyncTaskSpawner(@Nullable Executor executor,
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
    public static AsyncTaskSpawner with(@Nullable Executor executor) {
        return with(executor, true);
    }

    @NonNull
    public static AsyncTaskSpawner with(@Nullable Executor executor, boolean isShared) {
        Looper looper = Looper.myLooper();
        if (looper == null) looper = Looper.getMainLooper();

        AsyncTaskSpawner spawner = new AsyncTaskSpawner();
        spawner.mExecutor = executor;
        spawner.mShutdown = !isShared;
        spawner.mHandler = new Handler(looper);
        return spawner;
    }

    @NonNull
    public static AsyncTaskSpawner create() {
        return with(ExecutorHelper.create(), false);
    }

    @NonNull
    public static AsyncTaskSpawner create(int corePoolSize) {
        return with(ExecutorHelper.create(
                corePoolSize
        ), false);
    }

    @NonNull
    public static AsyncTaskSpawner create(int corePoolSize, int queueCapacity) {
        return with(ExecutorHelper.create(
                corePoolSize,
                queueCapacity
        ), false);
    }

    @NonNull
    public static AsyncTaskSpawner create(int corePoolSize, int queueCapacity, long keepAliveTime) {
        return with(ExecutorHelper.create(
                corePoolSize,
                queueCapacity,
                keepAliveTime
        ), false);
    }
}
