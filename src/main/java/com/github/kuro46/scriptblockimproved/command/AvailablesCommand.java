package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import java.util.Set;
import org.bukkit.command.CommandSender;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class AvailablesCommand extends Command {

    public AvailablesCommand() {
        super("availables", Args.empty());
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        options(sender);
    }

    private void options(final CommandSender sender) {
        final Set<String> names = ScriptBlockImproved.getInstance().getScriptHandler().getHandlers().keySet();
        if (names.isEmpty()) {
            sendMessage(sender, "No available options exist");
        } else {
            sendMessage(sender, "Available options:");
            names.forEach(optionName -> sendMessage(sender, "  " + optionName));
        }
    }
}
