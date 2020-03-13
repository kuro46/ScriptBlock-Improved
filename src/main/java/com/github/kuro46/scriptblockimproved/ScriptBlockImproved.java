package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.handler.TellHandler;
import com.github.kuro46.scriptblockimproved.storage.NoOpStorage;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public final class ScriptBlockImproved {

    @Getter
    private static ScriptBlockImproved instance;
    @Getter
    private final Logger logger;
    @Getter
    private final ScriptList scriptList;
    @Getter
    private final ScriptHandler scriptHandler = new ScriptHandler();

    private ScriptBlockImproved(@NonNull Bootstrap bootstrap) throws InitException {
        this.logger = bootstrap.getLogger();
        try {
            this.scriptList = ScriptList.load(new NoOpStorage());
        } catch (IOException e) {
            throw new InitException("Unable to load ScriptList from Storage", e);
        }

        scriptHandler.registerHandler("tell", new TellHandler());

        final Script script = new Script(Author.system("test"), OffsetDateTime.now(ZoneId.systemDefault()), "move", ImmutableList.of(new Script.Option("tell", ImmutableList.of("test message"))));
        scriptList.add(new BlockPosition("world", 0, 4, 0), script);
        Bukkit.getPluginManager().registerEvents(new MoveListener(), bootstrap);
    }

    static void init(@NonNull Bootstrap bootstrap) throws InitException {
        if (instance != null) {
            throw new InitException("Already initialized!");
        }
        instance = new ScriptBlockImproved(bootstrap);
    }

    @SuppressWarnings("serial")
    static class InitException extends Exception {
        public InitException(@NonNull final String message) {
            super(message);
        }

        public InitException(@NonNull final String message, @NonNull final Throwable cause) {
            super(message, cause);
        }
    }

    private final class MoveListener implements Listener {

        private final Map<Player, BlockPosition> lastTriggeredMap = new WeakHashMap<>();

        @EventHandler
        public void onMove(@NonNull final PlayerMoveEvent event) {
            final Player player = event.getPlayer();
            final Location location = player.getLocation();
            final BlockPosition position = new BlockPosition(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            final BlockPosition lastTriggered = lastTriggeredMap.get(player);
            if (lastTriggered == null || !lastTriggered.equals(position)) {
                scriptHandler.handle(player, position, TriggerInfo.builder().name("move").event(event).build());
                lastTriggeredMap.put(player, position);
            }
        }
    }
}
