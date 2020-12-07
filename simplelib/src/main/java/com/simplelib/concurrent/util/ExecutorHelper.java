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

package com.simplelib.concurrent.util;

import androidx.annotation.NonNull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public final class ExecutorHelper {
    /* -------- Defaults -------------- */
    public static int DEFAULT_CORE_POOL_SIZE = 0;
    public static int DEFAULT_MAX_POOL_SIZE = 6;
    public static long DEFAULT_KEEP_ALIVE_TIME = 20L * 1000L;

    /* -------- Initialization -------- */
    @NonNull
    public static ExecutorService create() {
        return create(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME);
    }

    @NonNull
    public static ExecutorService create(int maxPoolSize) {
        return create(DEFAULT_CORE_POOL_SIZE, maxPoolSize, DEFAULT_KEEP_ALIVE_TIME);
    }

    @NonNull
    public static ExecutorService create(int maxPoolSize,
                                         long keepAliveTime) {
        return create(DEFAULT_CORE_POOL_SIZE, maxPoolSize, keepAliveTime);
    }

    @NonNull
    public static ExecutorService create(int corePoolSize,
                                         int maxPoolSize,
                                         long keepAliveTime) {
        corePoolSize = Math.max(corePoolSize, 0);
        maxPoolSize = Math.max(Math.max(maxPoolSize, corePoolSize), 1);
        keepAliveTime = Math.max(keepAliveTime, 0L);

        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>()
        );
    }

    private ExecutorHelper() {
        throw new UnsupportedOperationException();
    }
}
