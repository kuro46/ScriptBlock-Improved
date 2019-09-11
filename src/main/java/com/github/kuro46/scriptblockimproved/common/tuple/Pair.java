package com.github.kuro46.scriptblockimproved.common.tuple;

import java.util.Objects;

public final class Pair<L, R> {

    private final L left;
    private final R right;

    private Pair(final L left, final R right) {
        this.left = Objects.requireNonNull(left, "'left' cannot be null");
        this.right = Objects.requireNonNull(right, "'right' cannot be null");
    }

    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new Pair<>(left, right);
    }

    public L left() {
        return left;
    }

    public R right() {
        return right;
    }
}
