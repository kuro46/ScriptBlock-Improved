package xyz.shirokuro.scriptblockimproved;

import com.google.common.primitives.Ints;
import lombok.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public static Optional<BlockPosition> parseArgs(@NonNull final CommandSender sender, @NonNull final Map<String, String> args) {
        final String world = args.get("world");
        final List<String> parseFailedList = new ArrayList<>(3);
        final Integer x = Ints.tryParse(args.get("x"));
        if (x == null) parseFailedList.add("x");
        final Integer y = Ints.tryParse(args.get("y"));
        if (y == null) parseFailedList.add("y");
        final Integer z = Ints.tryParse(args.get("z"));
        if (z == null) parseFailedList.add("z");
        if (!parseFailedList.isEmpty()) {
            MessageUtils.sendMessage(sender, MessageKind.ERROR, String.join(", ", parseFailedList) + " is invalid number.");
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
