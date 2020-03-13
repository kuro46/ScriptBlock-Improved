package com.github.kuro46.scriptblockimproved.handler;

import com.github.kuro46.scriptblockimproved.TriggerInfo;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class BroadcastHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerInfo triggerInfo, Player player, ImmutableList<String> args) {
        Bukkit.broadcastMessage(String.join(" ", args));
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return ValidationResult.VALID;
    }
}
