package com.github.kuro46.scriptblockimproved.command.handlers.migration;

import com.github.kuro46.scriptblockimproved.common.command.ArgName;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.Script;
import com.github.kuro46.scriptblockimproved.script.ScriptMap;
import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.Option;
import com.github.kuro46.scriptblockimproved.script.option.OptionList;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

final class SBScriptLoader {

    private static final Splitter COORD_SPLITTER = Splitter.on(',');
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("Author:(.*?)/.*");
    private static final Pattern OPTION_PATTERN = Pattern.compile("@(.*?)(?::(.*?))? (.*)");

    @NonNull
    private final TriggerName trigger;

    public SBScriptLoader(@NonNull final TriggerName trigger) {
        this.trigger = trigger;
    }

    public static ScriptMap load(
            @NonNull final TriggerName trigger,
            @NonNull final Path path) throws MigrationException {
        final SBScriptLoader loader = new SBScriptLoader(trigger);
        return loader.load(path);
    }

    private ScriptMap load(@NonNull final Path path) throws MigrationException {
        final ScriptMap.Builder scriptsBuilder = ScriptMap.builder();

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

                final ImmutableList.Builder<Option> builder = ImmutableList.builder();
                for (final String rawOption : data.subList(1, data.size())) {
                    final SBOption sbOption = parseOption(rawOption);
                    final Option option = convertToSBIOption(sbOption);
                    builder.add(option);
                }
                final OptionList options = new OptionList(builder.build());
                final Script script = new Script(-1, trigger, author, parsedCoord, options);
                scriptsBuilder.add(script);
            }
        }

        return scriptsBuilder.build();
    }

    private Author trimAuthor(@NonNull final String author) throws MigrationException {
        final Matcher matcher = AUTHOR_PATTERN.matcher(author);
        if (!matcher.find()) {
            throw new MigrationException(String.format("Invalid author format: '%s'", author));
        }
        return Author.player(matcher.group(1));
    }

    private BlockPosition parseCoord(@NonNull final String world, @NonNull final String coord) {
        final List<String> parts = COORD_SPLITTER.splitToList(coord);
        final int x = Integer.parseInt(parts.get(0));
        final int y = Integer.parseInt(parts.get(1));
        final int z = Integer.parseInt(parts.get(2));
        return new BlockPosition(world, x, y, z);
    }

    private SBOption parseOption(@NonNull final String option) throws MigrationException {
        final Matcher matcher = OPTION_PATTERN.matcher(option);
        if (!matcher.find()) {
            throw new MigrationException(String.format("Invalid option: '%s'", option));
        }
        final String name = matcher.group(1);
        final String additionalArgument = matcher.group(2);
        final String argument = matcher.group(3);

        return new SBOption(name, additionalArgument, argument);
    }

    private Option convertToSBIOption(@NonNull final SBOption sbOption) throws MigrationException {
        final OptionName name;
        final ImmutableMap.Builder<ArgName, String> builder = ImmutableMap.builder();
        switch (sbOption.getName().toLowerCase()) {
            case "command":
                name = OptionName.of("command");
                builder.put(ArgName.of("command"), sbOption.getArgument());
                break;
            case "bypassperm":
                name = OptionName.of("bypasscommand");
                builder.put(ArgName.of("permission"), sbOption.getAdditionalArgument().get());
                builder.put(ArgName.of("command"), sbOption.getArgument());
                break;
            case "player":
                name = OptionName.of("say");
                builder.put(ArgName.of("message"), sbOption.getArgument());
                break;
            case "bypass":
                name = OptionName.of("bypasscommand");
                // TODO: Check existence of permission
                builder.put(ArgName.of("permission"), "auto");
                builder.put(ArgName.of("command"), sbOption.getArgument());
                break;
            default:
                final String message = "Unsupported option. Supported options are "
                    + "'@command', '@bypassperm', '@bypass' and '@player'";
                throw new MigrationException(message);
        }
        final ParsedArgs args = new ParsedArgs(builder.build());
        return new Option(name, args);
    }

    private static final class SBOption {

        /**
         * @option:additionalArgument argument
         *  \here/
         */
        @NonNull
        private final String name;
        /**
         * nullable
         *
         * @option:additionalArgument argument
         *         \----- here -----/
         */
        @NonNull
        private final String additionalArgument;
        /**
         * @option:additionalArgument argument
         *                            \-here-/
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
