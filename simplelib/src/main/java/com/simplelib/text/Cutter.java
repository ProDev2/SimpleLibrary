package com.simplelib.text;

import java.util.*;

public class Cutter implements Iterable<Cutter.Part>
{
    //Static methods
    public static Cutter use(String text, Part... parts) {
        return new Cutter(text, parts);
    }

    public static Cutter use(String text, ArrayList<Part> parts) {
        return new Cutter(text, parts);
    }

    //Cutter
    private String text;
    private ArrayList<Part> parts;

    public Cutter(String text, Part... parts) {
        setText(text);
        setParts(parts);
    }

    public Cutter(String text, ArrayList<Part> parts) {
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

    public Cutter setParts(ArrayList<Part> parts) {
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

    public ArrayList<Part> getList() {
        cut();
        ArrayList<Part> list = new ArrayList<>();
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

        private ArrayList<String> data;
        private ArrayList<Part> subParts;

        private ArrayList<String> excludedData;

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

        public ArrayList<String> getData() {
            return data;
        }

        public ArrayList<Part> getSubParts() {
            return subParts;
        }

        public ArrayList<String> getExcludedData() {
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