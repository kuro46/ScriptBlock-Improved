package com.github.kuro46.scriptblockimproved.command.clickaction;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;

public class Actions {

    private final Map<Player, Action> actions = new WeakHashMap<>();

    public void add(final Player player, final Action action) {
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(action, "'action' cannot be null");

        actions.put(player, action);
    }

    public Optional<Action> poll(final Player player) {
        Objects.requireNonNull(player, "'player' cannot be null");

        return Optional.ofNullable(actions.remove(player));
    }
}
