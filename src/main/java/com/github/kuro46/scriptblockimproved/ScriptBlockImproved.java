package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.command.SBICommandExecutor;
import com.github.kuro46.scriptblockimproved.handler.BroadcastHandler;
import com.github.kuro46.scriptblockimproved.handler.BypassCommandHandler;
import com.github.kuro46.scriptblockimproved.handler.CancelEventHandler;
import com.github.kuro46.scriptblockimproved.handler.CommandHandler;
import com.github.kuro46.scriptblockimproved.handler.ConsoleHandler;
import com.github.kuro46.scriptblockimproved.handler.SayHandler;
import com.github.kuro46.scriptblockimproved.listener.PlayerInteractListener;
import com.github.kuro46.scriptblockimproved.listener.PlayerMoveListener;
import com.github.kuro46.scriptblockimproved.placeholder.Placeholder;
import com.github.kuro46.scriptblockimproved.placeholder.PlaceholderGroup;
import com.github.kuro46.scriptblockimproved.storage.JSONStorage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
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
    private final PlaceholderGroup placeholderGroup = new PlaceholderGroup();
    @Getter
    private final Plugin plugin;

    private ScriptBlockImproved(@NonNull Bootstrap bootstrap) throws InitException {
        this.logger = bootstrap.getLogger();
        this.plugin = bootstrap;
        try {
            this.scriptList = ScriptList.load(JSONStorage.load(bootstrap.getDataFolder().toPath().resolve("scripts.json")));
        } catch (IOException e) {
            throw new InitException("Unable to load ScriptList from Storage", e);
        }
        Bukkit.getPluginCommand("sbi").setExecutor(new SBICommandExecutor());
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), bootstrap);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), bootstrap);

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

        placeholderGroup.add(Placeholder.builder()
            .name("player")
            .factory(data -> data.getPlayer().getName())
            .build());
        placeholderGroup.add(Placeholder.builder()
            .name("world")
            .factory(data -> data.getPosition().getWorld())
            .build());
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
}
