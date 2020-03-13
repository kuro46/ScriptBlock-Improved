package com.github.kuro46.scriptblockimproved;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Location;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class BlockPosition {

    @Getter
    @NonNull
    private final String world;
    @Getter
    private final int x, y, z;

    public static BlockPosition ofLocation(@NonNull final Location location) {
        return new BlockPosition(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
