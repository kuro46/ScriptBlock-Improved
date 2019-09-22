package com.github.kuro46.scriptblockimproved.common;

import java.util.List;
import java.util.Optional;

public final class ListUtils {

    private ListUtils() {
    }

    public static boolean isPresent(final List<?> list, final int index) {
        return index < list.size();
    }

    public static <T> Optional<T> get(final List<T> list, final int index) {
        return isPresent(list, index)
            ? Optional.of(list.get(index))
            : Optional.empty();
    }
}
