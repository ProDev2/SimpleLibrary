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

package com.simplelib.tools;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.Closeable;
import java.io.IOException;

public final class SessionTools {
    private SessionTools() {
    }

    public static void closeWithoutFail(@Nullable Closeable closeable) {
        try {
            close(closeable);
        } catch (IOException ignored) {
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
    }

    public static void closeQuietly(@Nullable Closeable closeable) {
        try {
            close(closeable);
        } catch (IOException ignored) {
        }
    }

    public static void close(@Nullable Closeable closeable) throws IOException {
        if (closeable == null)
            return;

        closeable.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void closeWithoutFail(@Nullable AutoCloseable closeable) {
        try {
            close(closeable);
        } catch (IOException ignored) {
        } catch (Throwable tr) {
            tr.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void closeQuietly(@Nullable AutoCloseable closeable) {
        try {
            close(closeable);
        } catch (Exception ignored) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void close(@Nullable AutoCloseable closeable) throws Exception {
        if (closeable == null)
            return;

        closeable.close();
    }
}
