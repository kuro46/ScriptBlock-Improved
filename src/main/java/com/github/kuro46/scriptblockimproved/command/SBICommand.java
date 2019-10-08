package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.scriptblockimproved.command.handlers.AddAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.AddHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.AvailablesHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.CreateAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.CreateHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.DeleteAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.DeleteHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.HelpHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ListHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.PermMapHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.SBIHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.SaveHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ViewAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ViewHandler;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CommandRoot;
import com.github.kuro46.scriptblockimproved.common.command.CommandSection;
import java.util.stream.Collectors;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class SBICommand {

    private SBICommand() {
    }

    public static void register() {
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
            .handler(new ListHandler())
            .childOf(root);
        Command.builder()
            .section("availables")
            .description("List available options/triggers")
            .handler(new AvailablesHandler())
            .childOf(root);
        Command.builder()
            .section("save")
            .description("Save scripts into specified file (or scripts.json)")
            .handler(new SaveHandler())
            .childOf(root);
        Command.builder()
            .section("map-perm")
            .description("Assosiate permission and command")
            .handler(new PermMapHandler())
            .childOf(root);
        Command.builder()
            .section("create")
            .description("Creates script in clicked block")
            .handler(new CreateHandler())
            .childOf(root);
        Command.builder()
            .section("createat")
            .description("Creates script into specified location")
            .handler(new CreateAtHandler())
            .childOf(root);
        Command.builder()
            .section("add")
            .description("Adds script in clicked block")
            .handler(new AddHandler())
            .childOf(root);
        Command.builder()
            .section("addat")
            .description("Adds script into specified location")
            .handler(new AddAtHandler())
            .childOf(root);
        Command.builder()
            .section("delete")
            .description("Deletes script in clicked block")
            .handler(new DeleteHandler())
            .childOf(root);
        Command.builder()
            .section("deleteat")
            .description("Deletes script into specified location")
            .handler(new DeleteAtHandler())
            .childOf(root);
        Command.builder()
            .section("view")
            .description("Displays information of scripts in the clicked block")
            .handler(new ViewHandler())
            .childOf(root);
        Command.builder()
            .section("viewat")
            .description("Displays information of scripts in the clicked block")
            .handler(new ViewAtHandler())
            .childOf(root);

        final CommandRoot commandRoot = CommandRoot.register(root);
        commandRoot.addListener((sender, path, command) -> {
            final String pathStr = path.stream()
                .map(CommandSection::getName)
                .collect(Collectors.joining(" "));
            sendMessage(sender,
                    MessageKind.ERROR,
                    "Usage: /%s %s",
                    pathStr,
                    command.getHandler().getArgs());
        });
    }
}
