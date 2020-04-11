package xyz.shirokuro.scriptblockimproved;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import xyz.shirokuro.commandutility.CommandGroup;
import xyz.shirokuro.scriptblockimproved.command.*;
import xyz.shirokuro.scriptblockimproved.command.migration.MigrateCommand;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import xyz.shirokuro.scriptblockimproved.handler.*;
import xyz.shirokuro.scriptblockimproved.listener.PlayerInteractListener;
import xyz.shirokuro.scriptblockimproved.listener.PlayerMoveListener;
import xyz.shirokuro.scriptblockimproved.placeholder.Placeholder;
import xyz.shirokuro.scriptblockimproved.placeholder.PlaceholderGroup;
import xyz.shirokuro.scriptblockimproved.storage.JSONStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

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
    private final TriggerRegistry triggerRegistry = new TriggerRegistry();
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
        registerCommands();
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(triggerRegistry), bootstrap);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(triggerRegistry), bootstrap);

        final Path dataFolder = plugin.getDataFolder().toPath();
        if (!Files.exists(dataFolder.resolve("permission-mappings.yml"))) {
            plugin.saveResource("permission-mappings.yml", false);
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

    private void registerCommands() {
        new CommandGroup(MessageUtils.PREFIX)
            .addAll(new HelpCommand())
            .addAll(new MigrateCommand())
            .addAll(new ListCommand())
            .addAll(new AvailablesCommand())
            .addAll(new SaveCommand())
            .addAll(new PermMapCommand())
            .addAll(new CreateCommand())
            .addAll(new CreateAtCommand())
            .addAll(new RemoveCommand())
            .addAll(new RemoveAtCommand())
            .addAll(new ViewCommand())
            .addAll(new ViewAtCommand());
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
