package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.common.Debouncer;
import com.github.kuro46.scriptblockimproved.common.ListUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class PermissionDetector {

    private static PermissionDetector instance;
    private final Path filePath;
    private final Debouncer saveDebouncer = new Debouncer(Duration.ofSeconds(1));;
    private final SetMultimap<Command, Permission> mappings = HashMultimap.create();

    private PermissionDetector(@NonNull final Plugin plugin, @NonNull final Path dataFolder) throws IOException {
        this.filePath = dataFolder.resolve("permission-mappings.yml");
        try {
            // Use runTask for reproduce POST_WORLD
            Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                // Load defaults
                mapCommands();
                // Load from file and override defaults
                overrideByFile();
                return null;
            }).get();
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof IOException) {
                throw new IOException(e);
            }
            e.printStackTrace();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void init(@NonNull final Plugin plugin, @NonNull final Path dataFolder) throws IOException {
        if (instance != null) throw new IllegalStateException("Already initialized");
        instance = new PermissionDetector(plugin, dataFolder);
    }

    public static PermissionDetector getInstance() {
        return instance;
    }

    private void mapCommands() {
        // Abort if running server is not based on CraftBukkit.
        final Class<?> serverClass = Bukkit.getServer().getClass();
        if (!serverClass.getName().matches("org\\.bukkit\\.craftbukkit\\..+?CraftServer")) {
            return;
        }
        final Method getCommandMap;
        try {
            getCommandMap = serverClass.getMethod("getCommandMap");
        } catch (final NoSuchMethodException e) {
            // unreachable
            throw new RuntimeException(e);
        }
        final SimpleCommandMap commandMap;
        try {
            commandMap = (SimpleCommandMap) getCommandMap.invoke(Bukkit.getServer());
        } catch (final IllegalAccessException | InvocationTargetException e) {
            // unreachable
            throw new RuntimeException(e);
        }
        commandMap.getCommands().forEach(command -> {
            if (command.getPermission() == null) {
                return;
            }
            mappings.put(
                new Command(command.getName()), new Permission(command.getPermission(), false)
            );
        });
    }

    private void overrideByFile() throws IOException {
        final YamlConfiguration configuration;
        try (final BufferedReader reader = Files.newBufferedReader(filePath)) {
            configuration = YamlConfiguration.loadConfiguration(reader);
        }
        for (final String command : configuration.getKeys(false)) {
            final List<Permission> permissions = configuration.getStringList(command).stream()
                .map(perm -> new Permission(perm, true))
                .collect(Collectors.toList());
            final Set<Permission> loaded = mappings.get(new Command(command));
            loaded.clear();
            loaded.addAll(permissions);
        }
    }

    private void save(@NonNull final SetMultimap<Command, Permission> mappings) throws IOException {
        final YamlConfiguration configuration = new YamlConfiguration();
        mappings.asMap().forEach((command, permissions) -> {
            final List<String> stringPerms = permissions.stream()
                .filter(Permission::doSave)
                .map(Permission::getName)
                .collect(Collectors.toList());
            configuration.set(command.getName(), stringPerms);
        });
        try (final BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(configuration.saveToString());
        }
    }

    private SetMultimap<Command, Permission> shallowCopy() {
        return HashMultimap.create(mappings);
    }

    public void associate(@NonNull final String command, @NonNull final String permission) {
        mappings.put(new Command(command), new Permission(permission, true));
        final SetMultimap<Command, Permission> copied = shallowCopy();
        saveDebouncer.runLater(() -> {
            try {
                save(copied);
            } catch (final IOException e) {
                // TODO
                throw new RuntimeException(e);
            }
        });
    }

    public Optional<List<String>> getPermissionsByCommand(@NonNull final String strCommand) {
        Set<Permission> permissions;
        Command command = new Command(strCommand);
        while (true) {
            if (mappings.containsKey(command)) {
                permissions = mappings.get(command);
                break;
            } else if (command.getParts().isEmpty()) {
                return Optional.empty();
            } else {
                command = new Command(ListUtils.removeLastElement(command.getParts()));
            }
        }
        final List<String> mapped = permissions.stream()
            .map(Permission::getName)
            .collect(Collectors.toList());
        return Optional.of(mapped);
    }

    @EqualsAndHashCode
    @ToString
    private static final class Command {

        @NonNull
        @Getter
        private final ImmutableList<String> parts;
        private String name;

        public Command(@NonNull final String name) {
            this(Arrays.asList(removeSlashIfNeeded(name).split(" ")));
        }

        public Command(@NonNull final List<String> parts) {
            this.parts = parts.stream()
                .map(String::toLowerCase)
                .collect(ImmutableList.toImmutableList());
        }

        private static String removeSlashIfNeeded(final String name) {
            if (name.isEmpty()) return name;
            final char first = name.charAt(0);
            if (first == '/') return name.substring(1, name.length());
            return name;
        }

        public String getName() {
            if (name == null) name = parts.stream().collect(Collectors.joining(" "));
            return name;
        }
    }

    @EqualsAndHashCode
    @ToString
    private static final class Permission {

        @NonNull
        @Getter
        private final String name;
        private final boolean save;

        public Permission(@NonNull final String name, final boolean save) {
            this.name = name.toLowerCase();
            this.save = save;
        }

        public boolean doSave() {
            return save;
        }
    }
}
