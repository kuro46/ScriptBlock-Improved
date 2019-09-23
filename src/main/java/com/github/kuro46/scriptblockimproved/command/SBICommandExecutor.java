package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.scriptblockimproved.command.clickaction.Actions;
import com.github.kuro46.scriptblockimproved.command.handlers.AddAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.AddHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.AvailablesHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.CreateAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.CreateHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.DeleteAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.DeleteHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.HelpHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ListHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.SBIHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.SaveHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ViewAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ViewHandler;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CommandManager;
import com.github.kuro46.scriptblockimproved.script.Scripts;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.trigger.Triggers;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class SBICommandExecutor {

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

        final Command root = Command.builder()
            .section("sbi")
            .description("Prints description")
            .handler(new SBIHandler())
            .build();

        Command.builder()
            .section("help")
            .description("Displays this message")
            .handler(new HelpHandler())
            .childOf(root);
        Command.builder()
            .section("list")
            .description("Displays list of scripts")
            .handler(new ListHandler(scripts))
            .childOf(root);
        Command.builder()
            .section("availables")
            .description("List available options/triggers")
            .handler(new AvailablesHandler(handlers, triggers))
            .childOf(root);
        Command.builder()
            .section("save")
            .description("Save scripts into specified file (or scripts.json)")
            .handler(new SaveHandler(dataFolder, scripts))
            .childOf(root);
        Command.builder()
            .section("create")
            .description("Creates script in clicked block")
            .handler(new CreateHandler(actions))
            .childOf(root);
        Command.builder()
            .section("createat")
            .description("Creates script into specified location")
            .handler(new CreateAtHandler(handlers, scripts))
            .childOf(root);
        Command.builder()
            .section("add")
            .description("Adds script in clicked block")
            .handler(new AddHandler(actions))
            .childOf(root);
        Command.builder()
            .section("addat")
            .description("Adds script into specified location")
            .handler(new AddAtHandler(handlers, scripts))
            .childOf(root);
        Command.builder()
            .section("delete")
            .description("Deletes script in clicked block")
            .handler(new DeleteHandler(actions))
            .childOf(root);
        Command.builder()
            .section("deleteat")
            .description("Deletes script into specified location")
            .handler(new DeleteAtHandler(scripts))
            .childOf(root);
        Command.builder()
            .section("view")
            .description("Displays information of scripts in the clicked block")
            .handler(new ViewHandler(actions))
            .childOf(root);
        Command.builder()
            .section("viewat")
            .description("Displays information of scripts in the clicked block")
            .handler(new ViewAtHandler(scripts))
            .childOf(root);

        final CommandManager manager = new CommandManager();
        manager.registerCommand(root);
        manager.addErrorHandler(new CommandManager.ErrorHandler() {

            @Override
            public void onUnknownCommand(final CommandSender sender, final List<String> sections) {
                sendMessage(sender, "Unknown command. Probably a bug.");
            }

            @Override
            public void onParseFailed(final CommandSender sender, final Command command) {
                sendMessage(sender,
                            "Usage: /%s %s",
                            command.getSection(),
                            command.getHandler().getArgs());
            }
        });
    }
}
