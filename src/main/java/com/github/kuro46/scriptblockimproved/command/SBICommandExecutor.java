package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.StringConverters;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionAdd;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionCreate;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionDelete;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionView;
import com.github.kuro46.scriptblockimproved.command.clickaction.Actions;
import com.github.kuro46.scriptblockimproved.command.handler.Command;
import com.github.kuro46.scriptblockimproved.command.handler.Commands;
import com.github.kuro46.scriptblockimproved.command.handler.CommandsListener;
import com.github.kuro46.scriptblockimproved.command.handler.ExecutionArguments;
import com.github.kuro46.scriptblockimproved.script.BlockCoordinate;
import com.github.kuro46.scriptblockimproved.script.Script;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import com.github.kuro46.scriptblockimproved.script.author.Author;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.trigger.Trigger;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.github.kuro46.scriptblockimproved.script.trigger.Triggers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public final class SBICommandExecutor {

    private final Actions actions;
    private final Scripts scripts;
    private final OptionHandlers handlers;
    private final Triggers triggers;

    public SBICommandExecutor(
        final Actions actions,
        final Scripts scripts,
        final OptionHandlers handlers,
        final Triggers triggers) {
        Objects.requireNonNull(actions, "'actions' cannot be null");
        Objects.requireNonNull(scripts, "'scripts' cannot be null");
        Objects.requireNonNull(handlers, "'handlers' cannot be null");
        Objects.requireNonNull(triggers, "'triggers' cannot be null");

        this.actions = actions;
        this.scripts = scripts;
        this.handlers = handlers;
        this.triggers = triggers;

        final StringConverters converters = new StringConverters();
        converters.registerDefaults();
        final Commands commands = new Commands(converters);
        commands.addListener(new CommandsListener() {

            @Override
            public void onInvalidSyntax(final CommandSender sender) {
                sender.sendMessage("Incorrect usage. Type '/sbi help' for help");
            }

            @Override
            public void onUnknownCommand(final CommandSender sender) {
                sender.sendMessage("Unknown command. Type '/sbi help' for help");
            }
        });
        Command.builder()
            .sections("sbi help")
            .executor((sender, args) -> help(sender))
            .register(commands);
        Command.builder()
            .sections("sbi create")
            .syntax("<trigger> <script>")
            .executor(this::create)
            .register(commands);
        Command.builder()
            .sections("sbi add")
            .syntax("<trigger> <script>")
            .executor(this::add)
            .register(commands);
        Command.builder()
            .sections("sbi delete")
            .executor(this::delete)
            .register(commands);
        Command.builder()
            .sections("sbi view")
            .executor(this::view)
            .register(commands);
        Command.builder()
            .sections("sbi createat")
            .syntax("<world> <x> <y> <z> <trigger> <script>")
            .executor(this::createAt)
            .register(commands);
        Command.builder()
            .sections("sbi addat")
            .syntax("<world> <x> <y> <z> <trigger> <script>")
            .executor(this::addAt)
            .register(commands);
        Command.builder()
            .sections("sbi deleteat")
            .syntax("<world> <x> <y> <z>")
            .executor(this::deleteAt)
            .register(commands);
        Command.builder()
            .sections("sbi viewat")
            .syntax("<world> <x> <y> <z>")
            .executor(this::viewAt)
            .register(commands);
        Command.builder()
            .sections("sbi list")
            .executor((sender, args) -> list(sender))
            .register(commands);
        Command.builder()
            .sections("sbi availables")
            .executor((sender, args) -> {
                triggers(sender);
                options(sender);
            })
            .register(commands);

        Bukkit.getPluginCommand("sbi").setExecutor(commands);
    }

    private void help(final CommandSender sender) {
        sender.sendMessage("/sbi help - Displays this message");
        sender.sendMessage("/sbi list - Displays list of scripts");
        sender.sendMessage("/sbi create <trigger> <script> - Creates script in clicked block");
        sender.sendMessage("/sbi createat <world> <x> <y> <z> <trigger> <script> - Creates script into specified location");
        sender.sendMessage("/sbi add <trigger> <script> - Adds script in clicked block");
        sender.sendMessage("/sbi addat <world> <x> <y> <z> <trigger> <script> - Adds script into specified location");
        sender.sendMessage("/sbi delete - Deletes script in clicked block");
        sender.sendMessage("/sbi deleteat <world> <x> <y> <z> - Creates script in clicked block");
        sender.sendMessage("/sbi view - Displays information of scripts in the clicked block");
        sender.sendMessage("/sbi viewat <world> <x> <y> <z> - Displays information of scripts in the clicked block");
        sender.sendMessage("/sbi availables - Lists available options/triggers");
    }

    private void create(final CommandSender sender, final ExecutionArguments args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cannot perform this command from the console");
            return;
        }
        final Player player = (Player) sender;
        player.sendMessage("Click any block to create script to the block");
        actions.add(player, new ActionCreate(args));
    }

    private void add(final CommandSender sender, final ExecutionArguments args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cannot perform this command from the console");
            return;
        }
        final Player player = (Player) sender;
        player.sendMessage("Click any block to add script to the block");
        actions.add(player, new ActionAdd(args));
    }

    private void delete(final CommandSender sender, final ExecutionArguments args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cannot perform this command from the console");
            return;
        }
        final Player player = (Player) sender;
        player.sendMessage("Click any block to delete scripts from the block");
        actions.add(player, new ActionDelete());
    }

    private void view(final CommandSender sender, final ExecutionArguments args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cannot perform this command from the console");
            return;
        }
        final Player player = (Player) sender;
        player.sendMessage("Click any block to view information about scripts in the block");
        actions.add(player, new ActionView());
    }

    private void deleteAt(
            final CommandSender sender,
            final ExecutionArguments args) {
        final World world = args.get(World.class, sender, 0).orElse(null);
        if (world == null) return;
        final Integer x = args.get(Integer.class, sender, 1).orElse(null);
        if (x == null) return;
        final Integer y = args.get(Integer.class, sender, 2).orElse(null);
        if (y == null) return;
        final Integer z = args.get(Integer.class, sender, 3).orElse(null);
        if (z == null) return;

        final BlockCoordinate coordinate = new BlockCoordinate(world.getName(), x, y, z);

        if (scripts.contains(coordinate)) {
            scripts.removeAll(coordinate);
            sender.sendMessage("Script(s) has been deleted");
        } else {
            sender.sendMessage("Script not exists");
        }
    }

    private void addAt(
            final CommandSender sender,
            final ExecutionArguments args) {
        final World world = args.get(World.class, sender, 0).orElse(null);
        if (world == null) return;
        final Integer x = args.get(Integer.class, sender, 1).orElse(null);
        if (x == null) return;
        final Integer y = args.get(Integer.class, sender, 2).orElse(null);
        if (y == null) return;
        final Integer z = args.get(Integer.class, sender, 3).orElse(null);
        if (z == null) return;
        final String trigger = args.get(4);

        final String rawOptions = args.subArgs(5).stream()
            .collect(Collectors.joining(" "));

        final Options options = Options.parse(handlers, rawOptions)
            .orElseThrow(IllegalArgumentException::new);
        final BlockCoordinate coordinate = new BlockCoordinate(world.getName(), x, y, z);
        final Author author = createAuthor(sender);

        if (!scripts.contains(coordinate)) {
            sender.sendMessage("Script not exists at that place. Instead use '/sbi create[at]'");
            return;
        }

        scripts.add(new Script(
                    System.currentTimeMillis(),
                    TriggerName.of(trigger),
                    author,
                    coordinate,
                    options));
        sender.sendMessage("The script has been added");
    }

    private void createAt(
            final CommandSender sender,
            final ExecutionArguments args) {
        final World world = args.get(World.class, sender, 0).orElse(null);
        if (world == null) return;
        final Integer x = args.get(Integer.class, sender, 1).orElse(null);
        if (x == null) return;
        final Integer y = args.get(Integer.class, sender, 2).orElse(null);
        if (y == null) return;
        final Integer z = args.get(Integer.class, sender, 3).orElse(null);
        if (z == null) return;
        final String trigger = args.get(4);

        final String rawOptions = args.subArgs(5).stream()
            .collect(Collectors.joining(" "));

        final Options options = Options.parse(handlers, rawOptions)
            .orElseThrow(IllegalArgumentException::new);
        final BlockCoordinate coordinate = new BlockCoordinate(world.getName(), x, y, z);
        final Author author = createAuthor(sender);

        if (scripts.contains(coordinate)) {
            sender.sendMessage("Script already exists at that place. Instead use '/sbi add[at]'");
            return;
        }

        scripts.add(new Script(
                    System.currentTimeMillis(),
                    TriggerName.of(trigger),
                    author,
                    coordinate,
                    options));
        sender.sendMessage("The script has been created");
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

    private void viewAt(final CommandSender sender, final ExecutionArguments args) {
        final World world = args.get(World.class, sender, 0).orElse(null);
        if (world == null) return;
        final Integer x = args.get(Integer.class, sender, 1).orElse(null);
        if (x == null) return;
        final Integer y = args.get(Integer.class, sender, 2).orElse(null);
        if (y == null) return;
        final Integer z = args.get(Integer.class, sender, 3).orElse(null);
        if (z == null) return;

        final BlockCoordinate coordinate = new BlockCoordinate(world.getName(), x, y, z);

        if (!scripts.contains(coordinate)) {
            sender.sendMessage("Script not exists");
        } else {
            scripts.get(coordinate).forEach(script -> {
                sender.sendMessage("-----");
                showScript(sender, script);
            });
            sender.sendMessage("-----");
        }
    }

    private void showScript(final CommandSender sender, final Script script) {
        sender.sendMessage(String.format("author: %s", script.getAuthor().getName()));
        sender.sendMessage(String.format("trigger: %s", script.getTrigger().getName()));
        sender.sendMessage("options:");
        script.getOptions().forEach(option -> {
            sender.sendMessage(String.format("  %s: ", option.getName().getName()));
            option.getArguments().getView().forEach((key, value) -> {
                sender.sendMessage(String.format("    %s: %s", key, value));
            });
        });
    }

    private void list(final CommandSender sender) {
        final List<BlockCoordinate> coordinates = new ArrayList<>(scripts.getCoordinates());
        Collections.sort(coordinates);
        int count = 0;
        for (final BlockCoordinate coordinate : coordinates) {
            sender.sendMessage(String.format(
                        "[%s] %s/%s/%s/%s",
                        ++count,
                        coordinate.getWorld(),
                        coordinate.getX(),
                        coordinate.getY(),
                        coordinate.getZ()));
        }
    }

    private void options(final CommandSender sender) {
        final ImmutableSet<OptionName> names = handlers.names();
        if (names.isEmpty()) {
            sender.sendMessage("No available options exist");
        } else {
            sender.sendMessage("Available options:");
            names.forEach(optionName -> sender.sendMessage("  " + optionName.getName()));
        }
    }

    private void triggers(final CommandSender sender) {
        final ImmutableList<Trigger> triggers = this.triggers.getTriggers();
        if (triggers.isEmpty()) {
            sender.sendMessage("No available triggers exist");
        } else {
            sender.sendMessage("Available triggers:");
            triggers.forEach(trigger -> sender.sendMessage("  " + trigger.getName()));
        }
    }
}
