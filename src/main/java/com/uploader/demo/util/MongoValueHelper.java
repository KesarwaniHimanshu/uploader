package com.uploader.demo.util;

import java.util.*;

public final class MongoValueHelper {

    private MongoValueHelper() {
        // utility class
    }

    public static String normalize(String header) {
        return header
                .toLowerCase()
                .replace("?", "")
                .replace("-", "")
                .trim()
                .replaceAll("[^a-z0-9]+", "_");
    }

    public static String getString(Map r, String key) {
        Object val = r.get(key);
        if (val == null || val.toString().isBlank()) return null;
        return val.toString().trim();
    }

    public static Integer getInteger(Map r, String key) {
        Object val = r.get(key);
        if (val == null || val.toString().isBlank()) return null;
        return Integer.parseInt(val.toString().trim());
    }

    public static Boolean getBoolean(Map r, String key) {
        Object val = r.get(key);
        if (val == null || val.toString().isBlank()) return null;
        return Boolean.parseBoolean(val.toString().trim());
    }

    public static List<String> split(Object value) {
        if (value == null || value.toString().isBlank()) return List.of();
        return Arrays.stream(value.toString().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
