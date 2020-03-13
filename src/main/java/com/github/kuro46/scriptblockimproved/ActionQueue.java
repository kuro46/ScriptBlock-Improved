package com.github.kuro46.scriptblockimproved;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ActionQueue {
    private final Cache<UUID, Consumer<Location>> queue = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10))
        .build();

    public void queue(@NonNull final Player player, @NonNull Consumer<Location> consumer) {
        queue.put(player.getUniqueId(), consumer);
    }

    public void executeIfQueued(@NonNull final Player player, @NonNull final Location location) {
        Optional.ofNullable(queue.getIfPresent(player.getUniqueId())).ifPresent(consumer -> consumer.accept(location));
        queue.invalidate(player.getUniqueId());
    }
}
