package com.github.kuro46.scriptblockimproved.command.clickaction;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class ActionQueue {

    private final Map<Player, Action> actions = new WeakHashMap<>();

    public void add(@NonNull final Player player, @NonNull final Action action) {
        actions.put(player, action);
    }

    public Optional<Action> poll(@NonNull final Player player) {
        return Optional.ofNullable(actions.remove(player));
    }
}
