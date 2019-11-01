package com.github.kuro46.scriptblockimproved;

import com.github.kuro46.scriptblockimproved.common.Debouncer;
import com.github.kuro46.scriptblockimproved.common.ListUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class PermissionDetector {

    private static final Lock INITIALIZER_LOCK = new ReentrantLock();
    private static volatile PermissionDetector instance;
    private final Lock ioLock = new ReentrantLock();
    @NonNull
    private final Path filePath;
    @NonNull
    private final Debouncer debouncer;
    @NonNull
    private volatile ImmutableSetMultimap<Command, Permission> mappings =
        ImmutableSetMultimap.of();

    private PermissionDetector(@NonNull final Path dataFolder) throws IOException {
        this.filePath = dataFolder.resolve("permission-mappings.yml");
        this.debouncer = new Debouncer(() -> {
            try {
                save();
            } catch (final IOException e) {
                // TODO: make better
                System.out.println(e.getMessage());
            }
        }, 1, TimeUnit.SECONDS);
        if (Files.exists(filePath)) {
            load();
        } else {
            loadRegistered();
            save();
        }
    }

    public static void init(@NonNull final Path dataFolder) throws IOException {
        INITIALIZER_LOCK.lock();
        try {
            if (instance != null) throw new IllegalStateException("Already initialized");
            instance = new PermissionDetector(dataFolder);
        } finally {
            INITIALIZER_LOCK.unlock();
        }
    }

    public static PermissionDetector getInstance() {
        return instance;
    }

    private void load() throws IOException {
        final YamlConfiguration configuration;
        ioLock.lock();
        try (final BufferedReader reader = Files.newBufferedReader(filePath)) {
            configuration = YamlConfiguration.loadConfiguration(reader);
        } finally {
            ioLock.unlock();
        }
        final SetMultimap<Command, Permission> mappings = HashMultimap.create();
        for (final String command : configuration.getKeys(false)) {
            final List<Permission> permissions = configuration.getStringList(command).stream()
                .map(Permission::new)
                .collect(Collectors.toList());
            mappings.putAll(new Command(command), permissions);
        }
        update(mappings);
    }

    private void loadRegistered() {
        final SetMultimap<Command, Permission> mappings = HashMultimap.create();
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            final Map<String, Map<String, Object>> commands =
                plugin.getDescription().getCommands();
            if (commands == null) continue;
            commands.forEach((name, values) -> {
                final String permission = (String) values.get("permission");
                if (permission == null) return;
                mappings.put(new Command(name), new Permission(permission));
            });
        }
        update(mappings);
    }

    private void save() throws IOException {
        final YamlConfiguration configuration = new YamlConfiguration();
        mappings.asMap().forEach((command, permissions) -> {
            final List<String> stringPerms = permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
            configuration.set(command.getName(), stringPerms);
        });
        ioLock.lock();
        try (final BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(configuration.saveToString());
        } finally {
            ioLock.unlock();
        }
    }

    public SetMultimap<Command, Permission> mutable() {
        return HashMultimap.create(mappings);
    }

    public void associate(@NonNull final String command, @NonNull final String permission) {
        final SetMultimap<Command, Permission> mutable = mutable();
        mutable.put(new Command(command), new Permission(permission));
        update(mutable);
        debouncer.runLater();
    }

    private void update(final SetMultimap<Command, Permission> newMappings) {
        mappings = ImmutableSetMultimap.copyOf(newMappings);
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

        public Permission(@NonNull final String name) {
            this.name = name.toLowerCase();
        }
    }
}
