package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.google.gson.JsonObject;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * A BlockPosition represents a position of the block.<br>
 */
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

    public static BlockPosition fromArgs(
            @NonNull final ParsedArgs args) throws InvalidNumberException {
        final int x = parseStringToInt("x", args);
        final int y = parseStringToInt("y", args);
        final int z = parseStringToInt("z", args);
        return new BlockPosition(args.getOrFail("world"), x, y, z);
    }

    private static int parseStringToInt(
            @NonNull final String argumentName,
            @NonNull final ParsedArgs args) throws InvalidNumberException {
        final String value = args.getOrFail(argumentName);
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new InvalidNumberException(argumentName, value);
        }
    }

    public static BlockPosition fromLocation(@NonNull final Location location) {
        return new BlockPosition(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    public static BlockPosition fromBlock(@NonNull final Block block) {
        return new BlockPosition(
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ());
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
