package com.github.kuro46.scriptblockimproved;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class BlockPosition {

    @Getter
    private final String world;
    @Getter
    private final int x, y, z;
}
