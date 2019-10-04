package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.google.common.primitives.Ints;
import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@EqualsAndHashCode
@ToString
public final class BlockPosition implements Comparable<BlockPosition> {

    @Getter
    @NonNull
    private final String world;
    @Getter
    private final int x;
    @Getter
    private final int y;
    @Getter
    private final int z;

    public BlockPosition(@NonNull final String world, final int x, final int y, final int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Optional<BlockPosition> fromArgs(@NonNull final ParsedArgs args) {
        final World world = Bukkit.getWorld(args.getOrFail("world"));
        if (world == null) return Optional.empty();
        final Integer x = Ints.tryParse(args.getOrFail("x"));
        if (x == null) return Optional.empty();
        final Integer y = Ints.tryParse(args.getOrFail("y"));
        if (y == null) return Optional.empty();
        final Integer z = Ints.tryParse(args.getOrFail("z"));
        if (z == null) return Optional.empty();
        return Optional.of(new BlockPosition(world.getName(), x, y, z));
    }

    public static BlockPosition fromLocation(@NonNull final Location location) {
        return new BlockPosition(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    public static BlockPosition fromJson(@NonNull final JsonObject json) {
        final String world = json.get("world").getAsString();
        final int x = json.get("x").getAsInt();
        final int y = json.get("y").getAsInt();
        final int z = json.get("z").getAsInt();
        return new BlockPosition(world, x, y, z);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("world", world);
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);
        return json;
    }

    public Location toLocation() {
        final World world = Bukkit.getWorld(this.world);
        Objects.requireNonNull(world, "World '" + this.world + "' not found!");
        return new Location(world, x, y, z);
    }

    @Override
    public int compareTo(final BlockPosition other) {
        final int worldCompareTo = this.world.compareTo(other.world);
        if (worldCompareTo != 0) return worldCompareTo;
        final int xComplareTo = Integer.compare(this.x, other.x);
        if (xComplareTo != 0) return xComplareTo;
        final int zCompareTo = Integer.compare(this.z, other.z);
        if (zCompareTo != 0) return zCompareTo;
        return Integer.compare(this.y, other.y);
    }
}
