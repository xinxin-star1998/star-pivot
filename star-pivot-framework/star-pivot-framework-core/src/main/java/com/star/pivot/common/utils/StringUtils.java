package com.star.pivot.common.utils;

import java.util.Collection;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        if (isEmpty(str)) return true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static boolean startsWith(String str, String prefix) {
        return str != null && prefix != null && str.startsWith(prefix);
    }

    public static boolean endsWith(String str, String suffix) {
        return str != null && suffix != null && str.endsWith(suffix);
    }

    public static boolean contains(String str, String searchStr) {
        return str != null && searchStr != null && str.contains(searchStr);
    }

    public static String join(Object[] array, String separator) {
        if (array == null) return null;
        if (separator == null) separator = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(separator);
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static String join(Collection<?> collection, String separator) {
        return collection == null ? null : join(collection.toArray(), separator);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) return null;
        if (end < 0) end = str.length() + end;
        if (start < 0) start = str.length() + start;
        if (end > str.length()) end = str.length();
        if (start > end) return "";
        if (start < 0) start = 0;
        if (end < 0) end = 0;
        return str.substring(start, end);
    }

    public static String substring(String str, int start) {
        if (str == null) return null;
        if (start < 0) start = str.length() + start;
        if (start < 0) start = 0;
        if (start > str.length()) return "";
        return str.substring(start);
    }
}
