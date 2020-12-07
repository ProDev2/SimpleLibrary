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

package com.simplelib.text;

import java.util.*;

public class Splitter implements Iterable<String>
{
    //Static methods
    public static Splitter use(String text, String... separations) {
        return new Splitter(text, separations);
    }

    public static Splitter use(String text, List<String> separations) {
        return new Splitter(text, separations);
    }

    public static Splitter use(String text, boolean keepSeparations, String... separations) {
        return new Splitter(text, keepSeparations, separations);
    }

    public static Splitter use(String text, boolean keepSeparations, List<String> separations) {
        return new Splitter(text, keepSeparations, separations);
    }

    //Splitter
    private String text;
    private List<String> separations;
    private boolean keepSeparations;

    private List<String> parts;

    public Splitter(String text, String... separations) {
        setText(text);
        setSeparations(separations);
    }

    public Splitter(String text, List<String> separations) {
        setText(text);
        setSeparations(separations);
    }

    public Splitter(String text, boolean keepSeparations, String... separations) {
        setText(text);
        setKeepSeparations(keepSeparations);
        setSeparations(separations);
    }

    public Splitter(String text, boolean keepSeparations, List<String> separations) {
        setText(text);
        setKeepSeparations(keepSeparations);
        setSeparations(separations);
    }

    public Splitter setText(String text) {
        if (text == null)
            throw new NullPointerException();

        this.text = text;
        return this;
    }

    public Splitter setSeparations(String... separations) {
        return setSeparations(new ArrayList<String>(Arrays.asList(separations)));
    }

    public Splitter setSeparations(List<String> separations) {
        if (this.separations == null)
            this.separations = new ArrayList<>();

        if (separations != null) {
            this.separations.clear();
            this.separations.addAll(separations);
        }
        return this;
    }

    public Splitter setKeepSeparations(boolean keepSeparations) {
        this.keepSeparations = keepSeparations;
        return this;
    }

    public Splitter split() {
        if (text == null)
            throw new NullPointerException();

        if (separations == null)
            separations = new ArrayList<>();

        if (parts == null)
            parts = new ArrayList<>();

        parts.clear();
        parts.add(text);
        for (String separation : separations) {
            splitAt(separation);
        }
        return this;
    }

    private void splitAt(String separation) {
        if (separation.length() <= 0) return;

        List<String> list = new ArrayList<>();
        for (String part : parts) {
            List<String> splitParts = splitAt(part, separation);

            if (splitParts.size() <= 0)
                splitParts.add(part);

            list.addAll(splitParts);
        }

        parts.clear();
        parts.addAll(list);
    }

    private List<String> splitAt(String partText, String separation) {
        List<String> list = new ArrayList<>();
        if (partText.contains(separation)) {
            int fPos = partText.indexOf(separation);
            if (fPos > 0)
                list.add(partText.substring(0, fPos));

            Integer[] positions = getAllIndexes(partText, separation);
            for (int count = 0; count < positions.length; count++) {
                if (keepSeparations)
                    list.add(separation);

                int start = positions[count] + separation.length();
                int end = positions.length > count + 1 ? positions[count + 1] : partText.length();

                if (start >= 0 && end <= partText.length()) {
                    String part = partText.substring(start, end);
                    if (part.length() > 0)
                        list.add(part);
                }
            }
        } else {
            list.add(partText);
        }
        return list;
    }

    private Integer[] getAllIndexes(String text, String keyword) {
        List<Integer> indexes = new ArrayList<>();
        int index = text.indexOf(keyword);
        while (index >= 0) {
            indexes.add(index);
            index = text.indexOf(keyword, index + keyword.length());
        }
        return indexes.toArray(new Integer[indexes.size()]);
    }

    public List<String> getList() {
        split();
        List<String> list = new ArrayList<>();
        for (String part : parts) {
            list.add(part);
        }
        return list;
    }

    public String[] getAsList() {
        split();
        String[] list = new String[parts.size()];
        for (int pos = 0; pos < parts.size(); pos++) {
            try {
                list[pos] = parts.get(pos);
            } catch (Exception e) {
            }
        }
        return list;
    }

    @Override
    public Iterator<String> iterator() {
        split();
        return parts.iterator();
    }
}