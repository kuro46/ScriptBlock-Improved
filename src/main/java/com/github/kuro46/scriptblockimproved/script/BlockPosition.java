package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;
import com.google.gson.JsonObject;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class BlockPosition implements Comparable<BlockPosition> {

    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public BlockPosition(final String world, final int x, final int y, final int z) {
        Objects.requireNonNull(world, "'world' cannot be null");

        // Should I call 'toLowerCase' here?
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Optional<BlockPosition> fromArgs(final ParsedArgs args) {
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

    public static BlockPosition fromLocation(final Location location) {
        Objects.requireNonNull(location, "'location' cannot be null");

        return new BlockPosition(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    public static BlockPosition fromJson(final JsonObject json) {
        Objects.requireNonNull(json, "'json' cannot be null");

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

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Location toLocation() {
        final World world = Bukkit.getWorld(this.world);
        Objects.requireNonNull(world, "World '" + this.world + "' not found!");
        return new Location(world, x, y, z);
    }

    @Override
    public int compareTo(final BlockPosition other) {
        Objects.requireNonNull(other, "'other' cannot be null");

        final int worldCompareTo = this.world.compareTo(other.world);
        if (worldCompareTo != 0) return worldCompareTo;
        final int xComplareTo = Integer.compare(this.x, other.x);
        if (xComplareTo != 0) return xComplareTo;
        final int zCompareTo = Integer.compare(this.z, other.z);
        if (zCompareTo != 0) return zCompareTo;
        return Integer.compare(this.y, other.y);
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof BlockPosition)) return false;
        BlockPosition castedOther = (BlockPosition) other;
        return this.x == castedOther.x
                && this.y == castedOther.y
                && this.z == castedOther.z
                && this.world.equals(castedOther.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, world);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("world", world)
            .add("x", x)
            .add("y", y)
            .add("z", z)
            .toString();
    }
}
