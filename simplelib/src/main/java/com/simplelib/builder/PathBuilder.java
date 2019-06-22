package com.simplelib.builder;

public class PathBuilder {
    public static final String PATH_SEPARATOR = "/";

    public static final String build(String... parts) {
        if (isEmpty(parts))
            return null;

        String path = null;
        for (String part : parts) {
            part = format(part);
            if (isEmpty(part))
                continue;

            if (!isEmpty(path))
                path += PATH_SEPARATOR + part;
            else
                path = part;
        }

        if (!isEmpty(path))
            return path;
        return null;
    }

    public static final String format(String path) {
        if (isEmpty(path))
            return null;

        path = path.trim();
        while (!isEmpty(path) &&
                path.startsWith(PATH_SEPARATOR)) {
            path = path.substring(PATH_SEPARATOR.length()).trim();
        }
        while (!isEmpty(path) &&
                path.endsWith(PATH_SEPARATOR)) {
            path = path.substring(0, path.length() - PATH_SEPARATOR.length()).trim();
        }

        if (!isEmpty(path))
            return path;
        return null;
    }

    public static final String nextPath(String path) {
        path = format(path);
        if (isEmpty(path))
            return null;

        int length = path.length();

        int index = path.indexOf(PATH_SEPARATOR);
        if (index >= 0 && index <= length) {
            int startIndex = index + PATH_SEPARATOR.length();
            if (startIndex <= length)
                return format(path.substring(startIndex));
        }
        return null;
    }

    public static final String get(String path) {
        path = format(path);
        if (isEmpty(path))
            return null;

        int length = path.length();

        int endIndex = path.indexOf(PATH_SEPARATOR);
        if (endIndex >= 0 && endIndex <= length)
            return format(path.substring(0, endIndex));
        return path;
    }

    public static final String next(String path) {
        path = format(path);
        if (isEmpty(path))
            return null;

        String nextPath = nextPath(path);
        if (!isEmpty(nextPath))
            return get(nextPath);
        return null;
    }

    public static final String last(String path) {
        path = format(path);
        if (isEmpty(path))
            return null;

        int length = path.length();

        int index = path.lastIndexOf(PATH_SEPARATOR);
        if (index >= 0 && index <= length) {
            int startIndex = index + PATH_SEPARATOR.length();
            if (startIndex <= length)
                return format(path.substring(startIndex));
        }
        return path;
    }

    public static final boolean isSinglePart(String str) {
        str = format(str);
        if (isEmpty(str))
            return false;

        int index = str.indexOf(PATH_SEPARATOR);
        return index < 0;
    }

    public static final boolean isEqual(String path1, String path2) {
        path1 = format(path1);
        path2 = format(path2);
        if (isEmpty(path1) || isEmpty(path2))
            return false;
        return path1.equals(path2);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private static boolean isEmpty(String[] strs) {
        return strs == null || strs.length == 0;
    }
}