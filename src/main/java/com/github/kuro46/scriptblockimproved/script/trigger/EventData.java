package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.bukkit.entity.Player;

public final class EventData {

    private final BlockCoordinate coordinate;
    private final Player player;

    public EventData(final BlockCoordinate coordinate, final Player player) {
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");
        Objects.requireNonNull(player, "'player' cannot be null");

        this.coordinate = coordinate;
        this.player = player;
    }

    public BlockCoordinate getCoordinate() {
        return coordinate;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof EventData)) {
            return false;
        }
        final EventData castedOther = (EventData) other;

        return this.coordinate.equals(castedOther.coordinate)
            && this.player.equals(castedOther.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate, player);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("coordinate", coordinate)
            .add("player", player)
            .toString();
    }
}
