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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Tools {
    @SuppressLint("MissingPermission")
    public static boolean connected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        return connected;
    }

    public static boolean isAvailable(String url) {
        try {
            return InetAddress.getByName(url).isReachable(5000);
        } catch (Exception e) {
            return false;
        }
    }

    public static void swap(List<?> list, int fromPos, int toPos) {
        if (fromPos < toPos) {
            for (int pos = fromPos; pos < toPos; pos++) {
                Collections.swap(list, pos, pos + 1);
            }
        } else {
            for (int pos = fromPos; pos > toPos; pos--) {
                Collections.swap(list, pos, pos - 1);
            }
        }
    }

    public static float dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static int dpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / 160f));
    }

    public static String getDate() {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
            return df.format(Calendar.getInstance().getTime());
        } else {
            Calendar calendar = Calendar.getInstance();

            String date = "";
            date += calendar.get(Calendar.DAY_OF_MONTH) + " ";
            date += calendar.get(Calendar.MONTH) + " ";
            date += calendar.get(Calendar.YEAR);

            return date;
        }
    }

    public static Integer[] getAllIndexes(String text, String keyword) {
        List<Integer> indexes = new ArrayList<>();
        int index = text.indexOf(keyword);
        while (index >= 0) {
            indexes.add(index);
            index = text.indexOf(keyword, index + keyword.length());
        }
        return indexes.toArray(new Integer[indexes.size()]);
    }

    public static Point getDisplaySize(Context context) {
        Point size = new Point();
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            display.getSize(size);
        } catch (Exception e) {
        }
        return size;
    }
}