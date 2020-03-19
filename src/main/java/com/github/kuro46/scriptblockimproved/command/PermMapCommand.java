package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.CandidateFactories;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.commandutility.ParsedArgs;
import com.github.kuro46.scriptblockimproved.PermissionDetector;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class PermMapCommand extends Command {

    public PermMapCommand() {
        super(
            "map-perm",
            Args.builder()
                .required("permission", "command")
                .build()
        );
    }

    @Override
    public void execute(final ExecutionData data) {
        final ParsedArgs args = data.getArgs();
        final CommandSender sender = data.getDispatcher();
        final String permission = args.getOrFail("permission");
        final String command = args.getOrFail("command");
        PermissionDetector.getInstance().associate(command, permission);
        sendMessage(sender, "Mapped");
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("permission", CandidateFactories.filter(value -> {
                return Bukkit.getPluginManager().getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList());
            }))
            .build(data.getArgName(), data.getCurrentValue());
    }
}
