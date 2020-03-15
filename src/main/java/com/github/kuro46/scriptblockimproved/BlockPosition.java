package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.commandutility.ParsedArgs;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public final class BlockPosition implements Comparable<BlockPosition> {

    @Getter
    @NonNull
    private final String world;
    @Getter
    private final int x, y, z;

    public static BlockPosition ofLocation(@NonNull final Location location) {
        return new BlockPosition(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Optional<BlockPosition> parseArgs(@NonNull final CommandSender sender, @NonNull final ParsedArgs args) {
        final String world = args.getOrFail("world");
        final List<String> parseFailedList = new ArrayList<>(3);
        final Integer x = Ints.tryParse(args.getOrFail("x"));
        if (x == null) parseFailedList.add("x");
        final Integer y = Ints.tryParse(args.getOrFail("y"));
        if (y == null) parseFailedList.add("y");
        final Integer z = Ints.tryParse(args.getOrFail("z"));
        if (z == null) parseFailedList.add("z");
        if (!parseFailedList.isEmpty()) {
            sendMessage(sender, MessageKind.ERROR, String.join(", ", parseFailedList) + " is invalid number.");
            return Optional.empty();
        }
        //noinspection ConstantConditions
        return Optional.of(new BlockPosition(world, x, y, z));
    }

    @Override
    public int compareTo(final BlockPosition other) {
        final int worldCompareTo = this.world.compareTo(other.world);
        if (worldCompareTo != 0) return worldCompareTo;
        final int xCompareTo = Integer.compare(this.x, other.x);
        if (xCompareTo != 0) return xCompareTo;
        final int zCompareTo = Integer.compare(this.z, other.z);
        if (zCompareTo != 0) return zCompareTo;
        return Integer.compare(this.y, other.y);
    }
}
