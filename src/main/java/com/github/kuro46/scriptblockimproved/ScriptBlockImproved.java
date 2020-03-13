package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.command.SBICommandExecutor;
import com.github.kuro46.scriptblockimproved.handler.BroadcastHandler;
import com.github.kuro46.scriptblockimproved.handler.BypassCommandHandler;
import com.github.kuro46.scriptblockimproved.handler.CancelEventHandler;
import com.github.kuro46.scriptblockimproved.handler.CommandHandler;
import com.github.kuro46.scriptblockimproved.handler.ConsoleHandler;
import com.github.kuro46.scriptblockimproved.handler.SayHandler;
import com.github.kuro46.scriptblockimproved.listener.PlayerInteractListener;
import com.github.kuro46.scriptblockimproved.storage.NoOpStorage;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.bukkit.plugin.Plugin;

public final class ScriptBlockImproved {

    @Getter
    private static ScriptBlockImproved instance;
    @Getter
    private final ActionQueue actionQueue = new ActionQueue();
    @Getter
    private final Logger logger;
    @Getter
    private final ScriptList scriptList;
    @Getter
    private final ScriptHandler scriptHandler = new ScriptHandler();
    @Getter
    private final Plugin plugin;

    private ScriptBlockImproved(@NonNull Bootstrap bootstrap) throws InitException {
        this.logger = bootstrap.getLogger();
        this.plugin = bootstrap;
        try {
            this.scriptList = ScriptList.load(new NoOpStorage());
        } catch (IOException e) {
            throw new InitException("Unable to load ScriptList from Storage", e);
        }
        Bukkit.getPluginCommand("sbi").setExecutor(new SBICommandExecutor());
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), bootstrap);

        final Path dataFolder = plugin.getDataFolder().toPath();
        if (!Files.exists(dataFolder.resolve("permission-mappings.yml"))) {
            plugin.saveResource("permission-mapping.yml", false);
        }
        try {
            PermissionDetector.init(plugin, dataFolder);
        } catch (IOException e) {
            throw new InitException("Unable to init PermissionDetector", e);
        }

        scriptHandler.registerHandler("command", new CommandHandler());
        scriptHandler.registerHandler("console", new ConsoleHandler());
        scriptHandler.registerHandler("broadcast", new BroadcastHandler());
        scriptHandler.registerHandler("say", new SayHandler());
        scriptHandler.registerHandler("cancelEvent", new CancelEventHandler());
        scriptHandler.registerHandler("bypassCommand", new BypassCommandHandler());

        final Script script = new Script(Author.system("test"), OffsetDateTime.now(ZoneId.systemDefault()), "move", ImmutableList.of(new Script.Option("cancelEvent", ImmutableList.of())));
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
            final boolean shouldSuppress = lastTriggered == null || !lastTriggered.equals(position);
            scriptHandler.handle(player, position, TriggerInfo.builder().name("move").shouldSuppress(shouldSuppress).event(event).build());
            if (!shouldSuppress) {
                lastTriggeredMap.put(player, position);
            }
        }
    }
}
