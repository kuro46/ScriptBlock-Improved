package xyz.shirokuro.scriptblockimproved.command;

import org.bukkit.command.CommandSender;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class SaveCommand {

    @Executor(command = "sbi save", description = "TODO")
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getSender();
        MessageUtils.sendMessage(sender,
            "Saving scripts into '/ScriptBlock-Improved/scripts.json'");
        CompletableFuture.runAsync(() -> {
            try {
                ScriptBlockImproved.getInstance().getScriptList().getStorage().save();
                MessageUtils.sendMessage(sender, MessageKind.SUCCESS, "Successfully saved");
            } catch (final IOException e) {
                MessageUtils.sendMessage(sender, MessageKind.ERROR, "Save failed!");
            }
        });
    }
}
