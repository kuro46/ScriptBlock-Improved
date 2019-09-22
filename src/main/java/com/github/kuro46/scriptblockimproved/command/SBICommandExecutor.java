package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.scriptblockimproved.command.clickaction.ActionAdd;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionCreate;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionDelete;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionView;
import com.github.kuro46.scriptblockimproved.command.clickaction.Actions;
import com.github.kuro46.scriptblockimproved.common.ListUtils;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.common.command.CommandSection;
import com.github.kuro46.scriptblockimproved.common.command.ParsedArgs;
import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import com.github.kuro46.scriptblockimproved.script.Script;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.serialize.ScriptSerializer;
import com.github.kuro46.scriptblockimproved.script.trigger.Trigger;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.github.kuro46.scriptblockimproved.script.trigger.Triggers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public final class SBICommandExecutor {

    private static final String PREFIX = ChatColor.GRAY
        + "["
        + ChatColor.DARK_AQUA
        + "SB"
        + ChatColor.AQUA
        + "I"
        + ChatColor.GRAY
        + "] "
        + ChatColor.RESET;

    private final Actions actions;
    private final Scripts scripts;
    private final OptionHandlers handlers;
    private final Triggers triggers;
    private final Path dataFolder;

    public SBICommandExecutor(
        final Actions actions,
        final Scripts scripts,
        final OptionHandlers handlers,
        final Triggers triggers,
        final Path dataFolder) {
        this.actions = Objects.requireNonNull(actions, "'actions' cannot be null");
        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
        this.handlers = Objects.requireNonNull(handlers, "'handlers' cannot be null");
        this.triggers = Objects.requireNonNull(triggers, "'triggers' cannot be null");
        this.dataFolder = Objects.requireNonNull(dataFolder, "'dataFolder' cannot be null");

        final CommandManager manager = new CommandManager();
        manager.addErrorHandler(new CommandManager.ErrorHandler() {

            @Override
            public void onUnknownCommand(final CommandSender sender, final List<String> sections) {
                sendMessage(sender, "Unknown command. Probably a bug.");
            }

            @Override
            public void onParseFailed(final CommandSender sender, final Command command) {
                sendMessage(sender,
                            "Usage: /%s %s",
                            command.getName(),
                            command.getArgs());
            }
        });
        Command.builder()
            .name("sbi")
            .description("Prints description")
            .args(Args.builder()
                .optional("")
                .build())
            .executor((sender, args) -> {
                final String free = args.getOrNull("");
                if (free == null) {
                    //TODO: improve
                    final String version = Bukkit.getPluginManager()
                        .getPlugin("ScriptBlock-Improved").getDescription().getVersion();
                    sendMessage(sender, "ScriptBlock-Improved v" + version);
                    help(manager, sender);
                } else {
                    sendMessage(sender, "Unknown command. Available commands:");
                    final String subCommands = manager.asMap().keySet().stream()
                        .map(name -> ListUtils.get(name.asSections(), 1))
                        .filter(section -> section.isPresent())
                        .map(Optional::get)
                        .map(CommandSection::getName)
                        .collect(Collectors.joining(", "));
                    sendMessage(sender, subCommands);
                    sendMessage(sender, "'/sbi help' for more details");
                }
            })
            .register(manager);
        Command.builder()
            .name("sbi help")
            .description("Displays this message")
            .executor((sender, args) -> help(manager, sender))
            .register(manager);
        Command.builder()
            .name("sbi create")
            .description("Creates script in clicked block")
            .args(Args.builder()
                .required("trigger")
                .required("script")
                .build())
            .executor(this::create)
            .register(manager);
        Command.builder()
            .name("sbi add")
            .description("Adds script in clicked block")
            .args(Args.builder()
                .required("trigger")
                .required("script")
                .build())
            .executor(this::add)
            .register(manager);
        Command.builder()
            .name("sbi delete")
            .description("Deletes script in clicked block")
            .executor(this::delete)
            .register(manager);
        Command.builder()
            .name("sbi view")
            .description("Displays information of scripts in the clicked block")
            .executor(this::view)
            .register(manager);
        Command.builder()
            .name("sbi createat")
            .description("Creates script into specified location")
            .args(Args.builder()
                .required("world")
                .required("x")
                .required("y")
                .required("z")
                .required("trigger")
                .required("script")
                .build())
            .executor(this::createAt)
            .register(manager);
        Command.builder()
            .name("sbi addat")
            .description("Adds script into specified location")
            .args(Args.builder()
                .required("world")
                .required("x")
                .required("y")
                .required("z")
                .required("trigger")
                .required("script")
                .build())
            .executor(this::addAt)
            .register(manager);
        Command.builder()
            .name("sbi deleteat")
            .description("Deletes script into specified location")
            .args(Args.builder()
                .required("world")
                .required("x")
                .required("y")
                .required("z")
                .build())
            .executor(this::deleteAt)
            .register(manager);
        Command.builder()
            .name("sbi viewat")
            .description("Displays information of scripts in the clicked block")
            .args(Args.builder()
                .required("world")
                .required("x")
                .required("y")
                .required("z")
                .build())
            .executor(this::viewAt)
            .register(manager);
        Command.builder()
            .name("sbi list")
            .description("Displays list of scripts")
            .executor((sender, args) -> list(sender))
            .register(manager);
        Command.builder()
            .name("sbi availables")
            .description("List available options/triggers")
            .executor((sender, args) -> {
                triggers(sender);
                options(sender);
            })
            .register(manager);
        Command.builder()
            .name("sbi save")
            .description("Save scripts into specified file (or scripts.json)")
            .args(Args.builder()
                .optional("fileName")
                .build())
            .executor(this::save)
            .register(manager);
    }

    private void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(PREFIX + message);
    }

    private void sendMessage(
            final CommandSender sender,
            final String message,
            final Object... args) {
        sendMessage(sender, String.format(message, args));
    }

    private void help(final CommandManager manager, final CommandSender sender) {
        sendMessage(sender, "Usage:");
        manager.asMap().values().forEach(command -> {
            String args = String.format("%s", command.getArgs());
            args = (args.isEmpty() ? "" : " ") + args;

            final String message = String.format(
                    "/%s%s - %s",
                    command.getName(),
                    args,
                    command.getDescription());
            sendMessage(sender, message);
        });
    }

    private void create(final CommandSender sender, final ParsedArgs args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "%sCannot perform this command from the console", ChatColor.RED);
            return;
        }
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to create script to the block");
        actions.add(player, new ActionCreate(args));
    }

    private void add(final CommandSender sender, final ParsedArgs args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "%sCannot perform this command from the console", ChatColor.RED);
            return;
        }
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to add script to the block");
        actions.add(player, new ActionAdd(args));
    }

    private void delete(final CommandSender sender, final ParsedArgs args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "%sCannot perform this command from the console", ChatColor.RED);
            return;
        }
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to delete scripts from the block");
        actions.add(player, new ActionDelete());
    }

    private void view(final CommandSender sender, final ParsedArgs args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "%sCannot perform this command from the console", ChatColor.RED);
            return;
        }
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to view information about scripts in the block");
        actions.add(player, new ActionView());
    }

    private void deleteAt(
            final CommandSender sender,
            final ParsedArgs args) {
        final BlockCoordinate coordinate = createCoordinate(args).orElse(null);
        if (coordinate == null) {
            return;
        }

        if (scripts.contains(coordinate)) {
            scripts.removeAll(coordinate);
            sendMessage(sender, "%sScript(s) has been deleted", ChatColor.GREEN);
        } else {
            sendMessage(sender, "%sScript not exists", ChatColor.RED);
        }
    }

    private void addAt(
            final CommandSender sender,
            final ParsedArgs args) {
        final String trigger = args.getOrFail("trigger");
        final String rawOptions = args.getOrFail("script");
        final Options options = Options.parse(handlers, rawOptions)
            .orElseThrow(IllegalArgumentException::new);
        final BlockCoordinate coordinate = createCoordinate(args).orElse(null);
        if (coordinate == null) {
            return;
        }
        final Author author = createAuthor(sender);

        if (!scripts.contains(coordinate)) {
            sendMessage(sender,
                    "%sScript not exists at that place. Instead use '/sbi create[at]'",
                    ChatColor.RED);
            return;
        }

        scripts.add(new Script(
                    System.currentTimeMillis(),
                    TriggerName.of(trigger),
                    author,
                    coordinate,
                    options));
        sendMessage(
                sender,
                "%sThe script has been added",
                ChatColor.RED);
    }

    private void createAt(
            final CommandSender sender,
            final ParsedArgs args) {
        final String trigger = args.getOrFail("trigger");
        final String rawOptions = args.getOrFail("script");
        final Options options = Options.parse(handlers, rawOptions)
            .orElseThrow(IllegalArgumentException::new);
        final BlockCoordinate coordinate = createCoordinate(args).orElse(null);
        if (coordinate == null) {
            return;
        }
        final Author author = createAuthor(sender);

        if (scripts.contains(coordinate)) {
            sendMessage(sender,
                    "%sScript already exists at that place. Instead use '/sbi add[at]'",
                    ChatColor.RED);
            return;
        }

        scripts.add(new Script(
                    System.currentTimeMillis(),
                    TriggerName.of(trigger),
                    author,
                    coordinate,
                    options));
        sendMessage(sender, "%sThe script has been created", ChatColor.GREEN);
    }

    private Author createAuthor(final CommandSender sender) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            return Author.player(player.getName(), player.getUniqueId());
        } else if (sender instanceof ConsoleCommandSender) {
            return Author.console();
        } else {
            throw new IllegalArgumentException(
                    String.format("Unknown CommandSender: %s", sender.getClass()));
        }
    }

    private void viewAt(final CommandSender sender, final ParsedArgs args) {
        final BlockCoordinate coordinate = createCoordinate(args).orElse(null);
        if (coordinate == null) {
            return;
        }

        if (!scripts.contains(coordinate)) {
            sendMessage(sender, "%sScript not exists", ChatColor.RED);
        } else {
            scripts.get(coordinate).forEach(script -> {
                sendMessage(sender, "-----");
                showScript(sender, script);
            });
            sendMessage(sender, "-----");
        }
    }

    private Optional<BlockCoordinate> createCoordinate(final ParsedArgs args) {
        final World world = Bukkit.getWorld(args.getOrFail("world"));
        if (world == null) return Optional.empty();
        final Integer x = Ints.tryParse(args.getOrFail("x"));
        if (x == null) return Optional.empty();
        final Integer y = Ints.tryParse(args.getOrFail("y"));
        if (y == null) return Optional.empty();
        final Integer z = Ints.tryParse(args.getOrFail("z"));
        if (z == null) return Optional.empty();

        return Optional.of(new BlockCoordinate(world.getName(), x, y, z));
    }

    private void showScript(final CommandSender sender, final Script script) {
        sendMessage(sender,
                "author: %s%s",
                ChatColor.RESET,
                script.getAuthor().getName());
        sendMessage(sender,
                "trigger: %s%s",
                ChatColor.RESET,
                script.getTrigger().getName());
        sendMessage(sender, "options:");
        script.getOptions().forEach(option -> {
            sendMessage(sender, "  %s: ", option.getName().getName());
            option.getArguments().getView().forEach((key, value) -> {
                sendMessage(sender, "    %s: %s", key, value);
            });
        });
    }

    private void list(final CommandSender sender) {
        final List<BlockCoordinate> coordinates = new ArrayList<>(scripts.getCoordinates());
        Collections.sort(coordinates);
        int count = 0;
        for (final BlockCoordinate coordinate : coordinates) {
            sendMessage(sender,
                        "[%s] %s/%s/%s/%s",
                        ++count,
                        coordinate.getWorld(),
                        coordinate.getX(),
                        coordinate.getY(),
                        coordinate.getZ());
        }
    }

    private void options(final CommandSender sender) {
        final ImmutableSet<OptionName> names = handlers.names();
        if (names.isEmpty()) {
            sendMessage(sender, "No available options exist");
        } else {
            sendMessage(sender, "Available options:");
            names.forEach(optionName -> sendMessage(sender, "  " + optionName.getName()));
        }
    }

    private void triggers(final CommandSender sender) {
        final ImmutableList<Trigger> triggers = this.triggers.getTriggers();
        if (triggers.isEmpty()) {
            sendMessage(sender, "No available triggers exist");
        } else {
            sendMessage(sender, "Available triggers:");
            triggers.forEach(trigger -> sendMessage(sender, "  " + trigger.getName()));
        }
    }

    private void save(final CommandSender sender, final ParsedArgs args) {
        final String fileName = args.get("fileName").orElse("scripts.json");
        final boolean canOverwrite = fileName.equals("scripts.json");

        sendMessage(sender,
                "Saving scripts into '/ScriptBlock-Improved/%s'",
                fileName);
        new Thread(() -> {
            try {
                ScriptSerializer.serialize(dataFolder.resolve(fileName), scripts, canOverwrite);
                sendMessage(sender, "%sSuccessfully saved", ChatColor.GREEN);
            } catch (final IOException e) {
                sendMessage(sender, "%sSave failed!", ChatColor.RED);
            }
        }, "sbi-command-sbi_save").start();
    }
}
