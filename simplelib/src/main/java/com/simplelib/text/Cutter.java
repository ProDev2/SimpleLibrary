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

public class Cutter implements Iterable<Cutter.Part>
{
    //Static methods
    public static Cutter use(String text, Part... parts) {
        return new Cutter(text, parts);
    }

    public static Cutter use(String text, List<Part> parts) {
        return new Cutter(text, parts);
    }

    //Cutter
    private String text;
    private List<Part> parts;

    public Cutter(String text, Part... parts) {
        setText(text);
        setParts(parts);
    }

    public Cutter(String text, List<Part> parts) {
        setText(text);
        setParts(parts);
    }

    public Cutter setText(String text) {
        if (text == null)
            throw new NullPointerException();

        this.text = text;
        return this;
    }

    public Cutter setParts(Part... parts) {
        return setParts(new ArrayList<Part>(Arrays.asList(parts)));
    }

    public Cutter setParts(List<Part> parts) {
        if (this.parts == null)
            this.parts = new ArrayList<>();

        if (parts != null) {
            this.parts.clear();
            this.parts.addAll(parts);
        }
        return this;
    }

    public Cutter cut() {
        if (text == null)
            throw new NullPointerException();

        if (parts == null)
            parts = new ArrayList<>();

        for (Part part : parts) {
            scanText(text, part);
        }

        return this;
    }

    private void scanText(String partText, Part part) {
        if (part == null) return;

        part.base = partText;

        Splitter splitter = new Splitter(partText, part.start, part.end);
        splitter.setKeepSeparations(true);

        String data = "";

        int stackCount = 0;
        for (String item : splitter) {
            boolean add = true;
            if (item.equals(part.start)) {
                if (stackCount == 0) {
                    add = false;

                    if (data.length() > 0) {
                        part.excludedData.add(data);
                        data = "";
                    }
                }

                stackCount++;
            } else if (item.equals(part.end) && stackCount > 0) {
                stackCount--;

                if (stackCount == 0) {
                    add = false;

                    if (data.length() > 0) {
                        part.data.add(data);
                        scanDataForMore(data, part);
                        data = "";
                    }
                }
            }

            if (add) data += item;
        }

        if (data.length() > 0) {
            if (stackCount <= 0)
                part.excludedData.add(data);
            else
                part.data.add(data);
        }
    }

    private void scanDataForMore(String data, Part scanPart) {
        for (Part part : parts) {
            if (data.contains(part.start) && data.contains(part.end)) {
                Part subPart = new Part(part.start, part.end);
                scanText(data, subPart);
                scanPart.subParts.add(subPart);
            }
        }
    }

    public List<Part> getList() {
        cut();
        List<Part> list = new ArrayList<>();
        for (Part part : parts) {
            list.add(part);
        }
        return list;
    }

    public Part[] getAsList() {
        cut();
        Part[] list = new Part[parts.size()];
        for (int pos = 0; pos < parts.size(); pos++) {
            try {
                list[pos] = parts.get(pos);
            } catch (Exception e) {
            }
        }
        return list;
    }

    @Override
    public Iterator iterator() {
        cut();
        return parts.iterator();
    }

    public static class Part implements Iterable {
        //Static methods
        public static Part create(String start, String end) {
            return new Part(start, end);
        }

        //Part
        private String start, end;

        private String base;

        private List<String> data;
        private List<Part> subParts;

        private List<String> excludedData;

        public Part(String start, String end) {
            this.start = start;
            this.end = end;

            this.data = new ArrayList<>();
            this.subParts = new ArrayList<>();

            this.excludedData = new ArrayList<>();
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }

        public String getBase() {
            return base;
        }

        public List<String> getData() {
            return data;
        }

        public List<Part> getSubParts() {
            return subParts;
        }

        public List<String> getExcludedData() {
            return excludedData;
        }

        public boolean hasData() {
            return data.size() > 0;
        }

        public boolean hasSubParts() {
            return subParts.size() > 0;
        }

        public boolean hasExcludedData() {
            return excludedData.size() > 0;
        }

        @Override
        public Iterator iterator() {
            return data.iterator();
        }
    }
}