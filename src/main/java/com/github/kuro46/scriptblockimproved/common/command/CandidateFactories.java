package com.github.kuro46.scriptblockimproved.common.command;

import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.World;

public final class CandidateFactories {

    private CandidateFactories() {
    }

    public static CandidateFactory worlds() {
        return currentValue -> {
            return Bukkit.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toList());
        };
    }
}
