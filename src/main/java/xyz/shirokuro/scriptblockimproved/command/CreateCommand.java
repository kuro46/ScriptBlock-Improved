package xyz.shirokuro.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.commandutility.ParsedArgs;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.OptionListParser;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.Trigger;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CreateCommand extends Command {

    public CreateCommand() {
        super(
            "create",
            Args.builder()
                .required("trigger")
                .required("options")
                .build()
        );
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender,
                MessageKind.ERROR,
                "Cannot perform this command from the console");
            return;
        }
        final ParsedArgs args = data.getArgs();
        final Player player = (Player) sender;
        MessageUtils.sendMessage(sender, "Click any block to create script to the block");
        try {
            OptionListParser.parse(data.getArgs().getOrFail("options"));
        } catch (OptionListParser.ParseException e) {
            MessageUtils.sendMessage(sender, ChatColor.RED + "Incorrect script!: " + e.getMessage());
            return;
        }
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            final BlockPosition position = BlockPosition.ofLocation(location);
            player.performCommand(String.format("sbi createat %s %s %s %s %s %s",
                position.getWorld(),
                position.getX(),
                position.getY(),
                position.getZ(),
                args.getOrFail("trigger"),
                args.getOrFail("options")));
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
