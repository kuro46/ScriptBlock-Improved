package com.github.kuro46.scriptblockimproved.common;

public final class Utils {
    private Utils() {

    }

    public static String removeSlashIfNeeded(final String source) {
        if (source.isEmpty() || source.charAt(0) != '/') return source;
        // 'source' is not empty and starts with '/'
        return source.substring(1);
    }
}
