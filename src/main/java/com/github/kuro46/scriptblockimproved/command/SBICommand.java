package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.scriptblockimproved.command.handlers.AddAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.AddHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.AvailablesHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.CreateAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.CreateHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.HelpHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ListHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.PermMapHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.RemoveAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.RemoveHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.SBIHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.SaveHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ViewAtHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.ViewHandler;
import com.github.kuro46.scriptblockimproved.command.handlers.migration.MigrateHandler;
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
            .handler(new SBIHandler())
            .build();

        Command.builder()
            .section("help")
            .handler(new HelpHandler())
            .childOf(root);
        Command.builder()
            .section("migrate")
            .handler(new MigrateHandler())
            .childOf(root);
        Command.builder()
            .section("list")
            .handler(new ListHandler())
            .childOf(root);
        Command.builder()
            .section("availables")
            .handler(new AvailablesHandler())
            .childOf(root);
        Command.builder()
            .section("save")
            .handler(new SaveHandler())
            .childOf(root);
        Command.builder()
            .section("map-perm")
            .handler(new PermMapHandler())
            .childOf(root);
        Command.builder()
            .section("create")
            .handler(new CreateHandler())
            .childOf(root);
        Command.builder()
            .section("createat")
            .handler(new CreateAtHandler())
            .childOf(root);
        Command.builder()
            .section("add")
            .handler(new AddHandler())
            .childOf(root);
        Command.builder()
            .section("addat")
            .handler(new AddAtHandler())
            .childOf(root);
        Command.builder()
            .section("remove")
            .handler(new RemoveHandler())
            .childOf(root);
        Command.builder()
            .section("removeat")
            .handler(new RemoveAtHandler())
            .childOf(root);
        Command.builder()
            .section("view")
            .handler(new ViewHandler())
            .childOf(root);
        Command.builder()
            .section("viewat")
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
