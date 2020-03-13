package com.github.kuro46.scriptblockimproved.handler;

import com.github.kuro46.scriptblockimproved.TriggerInfo;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.entity.Player;

public interface OptionHandler {

    @SuppressWarnings("unused")
    default void onSuppressed(TriggerInfo triggerInfo, Player player, ImmutableList<String> args) {
        // no-op
    }

    void handleOption(TriggerInfo triggerInfo, Player player, ImmutableList<String> args);

    ValidationResult validateArgs(List<String> args);
}
