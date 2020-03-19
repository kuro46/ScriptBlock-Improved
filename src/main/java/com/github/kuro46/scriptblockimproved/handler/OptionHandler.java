package com.github.kuro46.scriptblockimproved.handler;

import com.github.kuro46.scriptblockimproved.TriggerData;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.bukkit.entity.Player;

public interface OptionHandler {

    @SuppressWarnings("unused")
    default void onSuppressed(TriggerData triggerData, Player player, ImmutableList<String> args) {
        // no-op
    }

    void handleOption(TriggerData triggerData, Player player, ImmutableList<String> args);

    ValidationResult validateArgs(List<String> args);
}
