package xyz.shirokuro.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.ExecutionData;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import java.io.IOException;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class SaveCommand extends Command {

    public SaveCommand() {
        super(
            "save",
            Args.empty()
        );
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();

        MessageUtils.sendMessage(sender,
            "Saving scripts into '/ScriptBlock-Improved/scripts.json'");
        new Thread(() -> {
            try {
                ScriptBlockImproved.getInstance().getScriptList().getStorage().save();
                MessageUtils.sendMessage(sender, MessageKind.SUCCESS, "Successfully saved");
            } catch (final IOException e) {
                MessageUtils.sendMessage(sender, MessageKind.ERROR, "Save failed!");
            }
        }, "sbi-command-sbi-save").start();
    }
}
