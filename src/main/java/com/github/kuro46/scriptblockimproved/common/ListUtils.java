package com.github.kuro46.scriptblockimproved.common;

import java.util.List;
import java.util.Optional;

public final class ListUtils {

    private ListUtils() {
    }

    public static boolean isPresent(final List<?> list, final int index) {
        return index > -1 && index < list.size();
    }

    public static <T> Optional<T> get(final List<T> list, final int index) {
        return isPresent(list, index)
            ? Optional.of(list.get(index))
            : Optional.empty();
    }

    public static <T> List<T> removeLastElement(final List<T> list) {
        return list.subList(0, list.size() - 1);
    }
}
