package xyz.shirokuro.scriptblockimproved.command;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import xyz.shirokuro.commandutility.CompletionData;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Completer;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import xyz.shirokuro.scriptblockimproved.permission.Branch;
import xyz.shirokuro.scriptblockimproved.permission.PermissionDetector;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class PermissionCommands {

    @Executor(command = "sbi permission associate <permission> <command>", description = "TODO")
    public void executeAssociate(final ExecutionData data) {
        final String permission = data.get("permission");
        final String associateCommand = data.get("command");
        permissionDetector().associate(associateCommand, permission);
        MessageUtils.sendMessage(data.getSender(), "Mapped");
    }

    @Completer(command = "sbi permission associate <permission> <command>")
    public List<String> completeAssociate(final CompletionData data) {
        if (data.getName().equals("permission")) {
            return Bukkit.getPluginManager().getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Executor(command = "sbi permission get <command>", description = "TODO")
    public void executeGet(final ExecutionData data) {
        final CommandSender sender = data.getSender();
        MessageUtils.sendMessage(sender, "Permissions for '" + data.get("command") + "'");
        permissionDetector().getPermissionsByCommand(data.get("command"))
            .stream()
            .sorted()
            .forEach(perm -> MessageUtils.sendMessage(sender, " - " + perm));
    }

    @Executor(command = "sbi permission tree", description = "TODO")
    public void executeTree(final ExecutionData data) {
        for (Branch child : permissionDetector().getRootBranch().getBranches().values()) {
            sendTree(data.getSender(), child, 0);
        }
    }

    private void sendTree(final CommandSender sender, final Branch branch, final int indentLevel) {
        final String indent = Strings.repeat(" ", indentLevel * 2);
        MessageUtils.sendMessage(sender, indent + branch.getName() + ":");
        MessageUtils.sendMessage(sender, indent + " - " + branch.getPermission());
        for (Branch child : branch.getBranches().values()) {
            sendTree(sender, child, indentLevel + 1);
        }
    }

    private PermissionDetector permissionDetector() {
        return ScriptBlockImproved.getInstance().getPermissionDetector();
    }
}
