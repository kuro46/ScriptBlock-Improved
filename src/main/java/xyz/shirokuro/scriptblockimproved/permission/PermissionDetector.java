package xyz.shirokuro.scriptblockimproved.permission;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import xyz.shirokuro.scriptblockimproved.NotRegularFileException;
import xyz.shirokuro.scriptblockimproved.ResourceNotFoundException;
import xyz.shirokuro.scriptblockimproved.common.Debouncer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

public final class PermissionDetector {

    private static final Splitter SPACE_SPLITTER = Splitter.on(' ');

    private final Path filePath;
    private final Debouncer saveDebouncer = new Debouncer(Duration.ofSeconds(1));
    private final Branch rootBranch = new Branch("root");

    /**
     * Initializes PermissionDetector.
     *
     * @param plugin Plugin instance for get resource/execute Bukkit task
     * @param filePath Path to save resource
     * @throws NotRegularFileException If specified path is not a regular file
     * @throws ResourceNotFoundException If cannot find resource by resource name detected from specified path
     * @throws IOException If other I/O Exception has occurred
     */
    public PermissionDetector(@NonNull final Plugin plugin, @NonNull final Path filePath) throws IOException {
        this.filePath = filePath;
        if (!Files.isRegularFile(filePath)) {
            throw new NotRegularFileException(filePath);
        }
        if (Files.notExists(filePath)) {
            final String resourceName = filePath.getFileName().toString();
            final InputStream resource = plugin.getResource(resourceName);
            if (resource == null) {
                throw new ResourceNotFoundException(resourceName);
            } else {
                Files.copy(resource, filePath);
            }
        }
        // Use runTask for reproduce POST_WORLD
        Bukkit.getScheduler().runTask(plugin, () -> {
            // Load defaults
            mapCommands();
            // Load from file and override defaults
            try {
                overrideByFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to read permission mapped file", e);
            }
        });
    }

    public Branch getRootBranch() {
        return rootBranch;
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
            updatePath(Collections.singletonList(command.getName()), command.getPermission(), true);
        });
    }

    private void overrideByFile() throws IOException {
        final YamlConfiguration configuration;
        try (final BufferedReader reader = Files.newBufferedReader(filePath)) {
            configuration = YamlConfiguration.loadConfiguration(reader);
        }
        overrideByFile(rootBranch, configuration);
    }

    private void overrideByFile(final Branch branch, final ConfigurationSection section) {
        for (String command : section.getKeys(false)) {
            final Branch child = branch.branch(command);
            final ConfigurationSection commandInfo = section.getConfigurationSection(command);
            final String permission = commandInfo.getString("permission");
            child.setPermission(permission);
            child.setProvided(false);
            final ConfigurationSection childrenSec = commandInfo.getConfigurationSection("children");
            overrideByFile(child, childrenSec);
        }
    }

    private void updatePath(final Iterable<String> path, final String permission, final boolean provided) {
        if (Iterables.isEmpty(path)) {
            throw new IllegalArgumentException("Path is empty!");
        }
        Branch current = rootBranch;
        for (String s : path) {
            current = current.branch(s);
        }
        current.setPermission(permission);
        current.setProvided(provided);
    }

    private void save() throws IOException {
        final YamlConfiguration configuration = new YamlConfiguration();
        for (Branch branch : rootBranch.getBranches().values()) {
            save(configuration, branch);
        }
        try (final BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write(configuration.saveToString());
        }
    }

    private void save(final Configuration configuration, final Branch branch) {
        final Configuration section = new MemoryConfiguration();
        if (!branch.isProvided()) {
            section.set("permission", branch.getPermission());
        }
        final List<Configuration> children = new ArrayList<>();
        for (Branch child : branch.getBranches().values()) {
            final Configuration childConf = new MemoryConfiguration();
            save(childConf, child);
            children.add(childConf);
        }
        if (!children.isEmpty()) {
            section.set("children", children);
        }
        configuration.set(branch.getName(), section);
    }

    public void associate(@NonNull final String command, @NonNull final String permission) {
        updatePath(SPACE_SPLITTER.split(trimSlash(command)), permission, false);
        saveDebouncer.runLater(() -> {
            try {
                save();
            } catch (final IOException e) {
                // TODO
                throw new RuntimeException(e);
            }
        });
    }

    public Set<String> getPermissionsByCommand(@NonNull final String strCommand) {
        final Set<String> permission = new HashSet<>();
        Branch current = rootBranch;
        for (String s : SPACE_SPLITTER.split(trimSlash(strCommand))) {
            current = current.get(s);
            if (current == null) {
                break;
            }
            permission.add(current.getPermission());
        }
        return permission;
    }

    private static String trimSlash(final String name) {
        if (name.isEmpty()) return name;
        final char first = name.charAt(0);
        if (first == '/') return name.substring(1);
        return name;
    }
}
