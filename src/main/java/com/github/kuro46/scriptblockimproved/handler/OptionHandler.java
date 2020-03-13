package com.github.kuro46.scriptblockimproved.handler;

import com.github.kuro46.scriptblockimproved.TriggerInfo;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

public interface OptionHandler {

    void handleOption(TriggerInfo triggerInfo, Player player, ImmutableList<String> args);
}
