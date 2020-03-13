package com.github.kuro46.scriptblockimproved.handler;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.TriggerInfo;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ConsoleHandler implements OptionHandler {
    @Override
    public void handleOption(TriggerInfo triggerInfo, Player player, ImmutableList<String> args) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ScriptBlockImproved.removeSlashIfNeeded(String.join(" ", args)));
    }

    @Override
    public ValidationResult validateArgs(List<String> args) {
        return ValidationResult.VALID;
    }
}
