package com.github.kuro46.scriptblockimproved.common.command;

import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;

public final class CandidateFactories {

    private CandidateFactories() {
    }

    public static CandidateFactory created(@NonNull final List<String> candidates) {
        return currentValue -> candidates;
    }

    public static CandidateFactory filter(@NonNull final CandidateFactory factory) {
        return currentValue -> {
            return factory.create(currentValue).stream()
                .filter(candidate -> candidate.startsWith(currentValue))
                .collect(Collectors.toList());
        };
    }

    public static CandidateFactory worlds() {
        return filter(currentValue -> {
            return Bukkit.getWorlds().stream()
                .map(World::getName)
                .collect(Collectors.toList());
        });
    }
}
