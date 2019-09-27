package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.bukkit.entity.Player;

public final class EventData {

    private final BlockPosition position;
    private final Player player;

    public EventData(final BlockPosition position, final Player player) {
        Objects.requireNonNull(position, "'position' cannot be null");
        Objects.requireNonNull(player, "'player' cannot be null");

        this.position = position;
        this.player = player;
    }

    public BlockPosition getPosition() {
        return position;
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

        return this.position.equals(castedOther.position)
            && this.player.equals(castedOther.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, player);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("position", position)
            .add("player", player)
            .toString();
    }
}
