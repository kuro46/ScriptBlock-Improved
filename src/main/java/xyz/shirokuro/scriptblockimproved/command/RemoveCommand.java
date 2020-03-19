package xyz.shirokuro.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.ExecutionData;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove", Args.empty());
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();

        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, MessageKind.ERROR, "Cannot perform this command from the console");
            return;
        }
        final Player player = (Player) sender;
        MessageUtils.sendMessage(sender, "Click any block to remove scripts from the block");
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            final BlockPosition position = BlockPosition.ofLocation(location);
            player.performCommand(String.format("sbi removeat %s %s %s %s",
                position.getWorld(),
                position.getX(),
                position.getY(),
                position.getZ()));
        });
    }
}
