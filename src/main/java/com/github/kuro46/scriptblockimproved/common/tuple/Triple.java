package com.github.kuro46.scriptblockimproved.common.tuple;

import java.util.Objects;

public final class Triple<L, M, R> {

    private final L left;
    private final M middle;
    private final R right;

    private Triple(final L left, final M middle, final R right) {
        this.left = Objects.requireNonNull(left, "'left' cannot be null");
        this.middle = Objects.requireNonNull(middle, "'middle' cannot be null");
        this.right = Objects.requireNonNull(right, "'right' cannot be null");
    }

    public static <L, M, R> Triple<L, M, R> of(final L left, final M middle, final R right) {
        return new Triple<>(left, middle, right);
    }

    public L left() {
        return left;
    }

    public M middle() {
        return middle;
    }

    public R right() {
        return right;
    }
}
