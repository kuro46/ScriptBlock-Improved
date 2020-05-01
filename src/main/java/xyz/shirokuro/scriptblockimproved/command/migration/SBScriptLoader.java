package xyz.shirokuro.scriptblockimproved.command.migration;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import xyz.shirokuro.scriptblockimproved.Author;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.Script;
import xyz.shirokuro.scriptblockimproved.ScriptList;
import xyz.shirokuro.scriptblockimproved.storage.NoOpStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SBScriptLoader {

    private static final Splitter COORD_SPLITTER = Splitter.on(',');
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("Author:(.*?)/.*");
    private static final Pattern OPTION_PATTERN = Pattern.compile("@(.*?)(?::(.*?))? (.*)");

    @NonNull
    private final String trigger;

    public SBScriptLoader(@NonNull final String trigger) {
        this.trigger = trigger;
    }

    public static ScriptList load(
        @NonNull final String trigger,
        @NonNull final Path path) throws MigrationException {
        final SBScriptLoader loader = new SBScriptLoader(trigger);
        return loader.load(path);
    }

    private ScriptList load(@NonNull final Path path) throws MigrationException {
        final ScriptList scriptsBuilder;
        try {
            scriptsBuilder = ScriptList.load(new NoOpStorage());
        } catch (IOException e) {
            throw new RuntimeException("Unreachable", e);
        }

        final Configuration worlds;
        try (final BufferedReader reader = Files.newBufferedReader(path)) {
            worlds = YamlConfiguration.loadConfiguration(reader);
        } catch (final IOException e) {
            throw new MigrationException("Failed to load configuration file");
        }
        for (final String world : worlds.getKeys(false)) {
            final ConfigurationSection coords = worlds.getConfigurationSection(world);
            for (final String coord : coords.getKeys(false)) {
                final List<String> data = coords.getStringList(coord);

                final BlockPosition parsedCoord = parseCoord(world, coord);
                final Author author = trimAuthor(data.get(0));

                final ImmutableList.Builder<Script.Option> builder = ImmutableList.builder();
                for (final String rawOption : data.subList(1, data.size())) {
                    final SBOption sbOption = parseOption(rawOption);
                    final Script.Option option = convertToSBIOption(sbOption);
                    builder.add(option);
                }
                final Script script = new Script(author, OffsetDateTime.MIN, trigger, builder.build());
                scriptsBuilder.add(parsedCoord, script);
            }
        }

        return scriptsBuilder;
    }

    private Author trimAuthor(@NonNull final String author) throws MigrationException {
        final Matcher matcher = AUTHOR_PATTERN.matcher(author);
        if (!matcher.find()) {
            throw new MigrationException(String.format("Invalid author format: '%s'", author));
        }
        return Author.player(matcher.group(1), null);
    }

    private BlockPosition parseCoord(@NonNull final String world, @NonNull final String coord) {
        final List<String> parts = COORD_SPLITTER.splitToList(coord);
        final int x = Integer.parseInt(parts.get(0));
        final int y = Integer.parseInt(parts.get(1));
        final int z = Integer.parseInt(parts.get(2));
        return new BlockPosition(world, x, y, z);
    }

    public static SBOption parseOption(@NonNull final String option) throws MigrationException {
        final Matcher matcher = OPTION_PATTERN.matcher(option);
        if (!matcher.find()) {
            throw new MigrationException(String.format("Invalid option: '%s'", option));
        }
        final String name = matcher.group(1);
        final String additionalArgument = matcher.group(2);
        final String argument = matcher.group(3);

        return new SBOption(name, additionalArgument, argument);
    }

    public static Script.Option convertToSBIOption(@NonNull final SBOption sbOption) throws MigrationException {
        final String name;
        final ImmutableList.Builder<String> argsBuilder = ImmutableList.builder();
        switch (sbOption.getName().toLowerCase()) {
            case "command":
                name = "command";
                argsBuilder.add(sbOption.getArgument());
                break;
            case "bypassperm":
                name = "bypassCommand";
                argsBuilder.add(sbOption.getAdditionalArgument().get());
                argsBuilder.add(sbOption.getArgument());
                break;
            case "player":
                name = "say";
                argsBuilder.add(sbOption.getArgument());
                break;
            case "bypass":
                name = "bypassCommand";
                // TODO: Check existence of permission
                argsBuilder.add("auto");
                argsBuilder.add(sbOption.getArgument());
                break;
            default:
                final String message = "Unsupported option. Supported options are "
                    + "'@command', '@bypassperm', '@bypass' and '@player'";
                throw new MigrationException(message);
        }
        return new Script.Option(name, argsBuilder.build());
    }

    private static final class SBOption {

        /**
         * @option:additionalArgument argument
         * \here/
         */
        @NonNull
        private final String name;
        /**
         * nullable
         *
         * @option:additionalArgument argument
         * \----- here -----/
         */
        @NonNull
        private final String additionalArgument;
        /**
         * @option:additionalArgument argument
         * \-here-/
         */
        @NonNull
        private final String argument;

        public SBOption(
            @NonNull final String name,
            final String additionalArgument,
            @NonNull final String argument) {
            this.name = name;
            this.additionalArgument = additionalArgument;
            this.argument = argument;
        }

        public String getName() {
            return name;
        }

        public Optional<String> getAdditionalArgument() {
            return Optional.ofNullable(additionalArgument);
        }

        public String getArgument() {
            return argument;
        }
    }
}
