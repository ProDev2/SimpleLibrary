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

import android.view.MotionEvent;
import android.view.View;

public class Utils {
    public static View.OnTouchListener createDragListener(final Runnable dragListener, final float minDragXInDp, final float minDragYInDp) {
        return new View.OnTouchListener() {
            private float touchX, moveX;
            private float touchY, moveY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchX = event.getX();
                        touchY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        moveX = event.getX() - touchX;
                        moveY = event.getY() - touchY;
                        break;

                    case MotionEvent.ACTION_UP:
                        float minXConv = MathTools.dpToPx(minDragXInDp);
                        float minYConv = MathTools.dpToPx(minDragYInDp);

                        boolean isX = MathTools.isPositive(minXConv) ? moveX >= minXConv : moveX <= minXConv;
                        boolean isY = MathTools.isPositive(minYConv) ? moveY >= minYConv : moveY <= minYConv;

                        if (minXConv == 0) isX = true;
                        if (minYConv == 0) isY = true;

                        if (isX && isY) dragListener.run();

                        touchX = 0;
                        touchY = 0;
                        moveX = 0;
                        moveY = 0;
                        break;
                }
                return true;
            }
        };
    }
}
