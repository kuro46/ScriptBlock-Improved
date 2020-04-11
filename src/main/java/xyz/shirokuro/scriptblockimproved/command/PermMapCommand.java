package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import xyz.shirokuro.commandutility.CompletionData;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Completer;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.PermissionDetector;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class PermMapCommand {

    @Executor(command = "sbi map-perm <permission> <command>", description = "TODO")
    public void execute(final ExecutionData data) {
        final String permission = data.get("permission");
        final String associateCommand = data.get("command");
        PermissionDetector.getInstance().associate(associateCommand, permission);
        MessageUtils.sendMessage(data.getSender(), "Mapped");
    }

    @Completer(command = "sbi map-perm <permission> <command>")
    public List<String> complete(final CompletionData data) {
        if (data.getName().equals("permission")) {
            return Bukkit.getPluginManager().getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
