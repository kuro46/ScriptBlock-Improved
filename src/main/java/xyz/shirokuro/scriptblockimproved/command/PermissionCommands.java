package xyz.shirokuro.scriptblockimproved.command;

import com.google.common.base.Splitter;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PermissionCommands {

    @Executor(command = "sbi permission map <permission> <command>", description = "TODO")
    public void executeMap(final ExecutionData data) {
        final String permission = data.get("permission");
        final String associateCommand = data.get("command");
        permissionDetector().map(associateCommand, permission);
        MessageUtils.sendMessage(data.getSender(), "Mapped");
    }

    @Completer(command = "sbi permission map <permission> <command>")
    public List<String> completeMap(final CompletionData data) {
        if (data.getName().equals("permission")) {
            return Bukkit.getPluginManager().getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Executor(command = "sbi permission unmap <command>", description = "TODO")
    public void executeUnmap(final ExecutionData data) {
        final String command = data.get("command");
        permissionDetector().unmap(command);
        MessageUtils.sendMessage(data.getSender(), "Unmapped");
    }

    @Completer(command = "sbi permission unmap <command>")
    public List<String> completeUnmap(final CompletionData data) {
        if (data.getName().equals("command")) {
            Branch current = permissionDetector().getRootBranch();
            for (String s : Splitter.on(' ').split(data.getCurrentValue())) {
                Branch temp = current.get(s);
                if (temp == null) {
                    break;
                } else {
                    current = temp;
                }
            }
            return current.getBranches().values().stream()
                .map(Branch::getName)
                .collect(Collectors.toList());
        } else {
            throw new RuntimeException("unreachable");
        }
    }

    @Executor(command = "sbi permission tree [filter]", description = "TODO")
    public void executeTree(final ExecutionData data) {
        final Iterable<String> filterPath = Optional.ofNullable(data.get("filter"))
            .map(s -> Splitter.on(' ').split(s))
            .orElse(Collections.emptyList());
        final CommandSender sender = data.getSender();
        int indentLevel = 0;
        Branch current = permissionDetector().getRootBranch();
        for (String s : filterPath) {
            current = current.get(s);
            if (current == null) {
                MessageUtils.sendMessage(sender, "No mappings found for filter: '" + filterPath + "'");
                return;
            }
            sendTree(sender, current, indentLevel, false);
            indentLevel++;
        }
        final int finalIndentLevel = indentLevel;
        current.getBranches().values().stream().sorted(Comparator.comparing(Branch::getName)).forEach(b -> {
            sendTree(sender, b, finalIndentLevel, true);
        });
    }

    private void sendTree(final CommandSender sender, final Branch branch, final int indentLevel, final boolean rec) {
        final String indent = Strings.repeat(" ", indentLevel * 2);
        final String perm = branch.getPermission() == null ? "" : branch.getPermission();
        MessageUtils.sendMessage(sender, indent + branch.getName() + ": " + perm);
        if (rec) {
            branch.getBranches().values().stream().sorted(Comparator.comparing(Branch::getName)).forEach(b -> {
                sendTree(sender, b, indentLevel + 1, true);
            });
        }
    }

    @Completer(command = "sbi permission tree [filter]")
    public List<String> completeTree(final CompletionData data) {
        if (data.getName().equals("filter")) {
            Branch current = permissionDetector().getRootBranch();
            for (String s : Splitter.on(' ').split(data.getCurrentValue())) {
                Branch temp = current.get(s);
                if (temp == null) {
                    break;
                } else {
                    current = temp;
                }
            }
            return current.getBranches().values().stream().map(Branch::getName).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private PermissionDetector permissionDetector() {
        return ScriptBlockImproved.getInstance().getPermissionDetector();
    }
}
