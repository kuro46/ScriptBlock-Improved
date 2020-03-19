package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.commandutility.ParsedArgs;
import com.github.kuro46.scriptblockimproved.BlockPosition;
import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.Trigger;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CreateCommand extends Command {

    public CreateCommand() {
        super(
            "create",
            Args.builder()
                .required("trigger")
                .required("args")
                .build()
        );
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        if (!(sender instanceof Player)) {
            sendMessage(sender,
                MessageKind.ERROR,
                "Cannot perform this command from the console");
            return;
        }
        final ParsedArgs args = data.getArgs();
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to create script to the block");
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            final BlockPosition position = BlockPosition.ofLocation(location);
            player.performCommand(String.format("sbi createat %s %s %s %s %s %s",
                position.getWorld(),
                position.getX(),
                position.getY(),
                position.getZ(),
                args.getOrFail("trigger"),
                args.getOrFail("args")));
        });
    }

    @Override
    public List<String> complete(CompletionData data) {
        return new CandidateBuilder()
            .when("trigger", s -> {
                return ScriptBlockImproved.getInstance().getTriggerRegistry().getTriggers().stream()
                    .map(Trigger::getName)
                    .collect(Collectors.toList());
            })
            .build(data.getArgName(), data.getCurrentValue());
    }
}
