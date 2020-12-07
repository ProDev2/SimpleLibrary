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

import com.simplelib.math.Vector2;

public class PointTools {
    public static double getDistance(Vector2 p1, Vector2 p2) {
        Vector2 d = getDistanceXY(p1, p2);

        return (double) Math.sqrt(Math.pow(d.x, 2) + Math.pow(d.y, 2));
    }

    public static int getDistanceAsInt(Vector2 p1, Vector2 p2) {
        Vector2 d = getDistanceXY(p1, p2);

        return (int) Math.sqrt(Math.pow(d.x, 2) + Math.pow(d.y, 2));
    }

    public static Vector2 getDistanceXY(Vector2 p1, Vector2 p2) {
        return new Vector2(getDistance(p1.x, p2.x), getDistance(p1.y, p2.y));
    }

    public static double getDistance(double p1, double p2) {
        return Math.max(p1, p2) - Math.min(p1, p2);
    }
}
