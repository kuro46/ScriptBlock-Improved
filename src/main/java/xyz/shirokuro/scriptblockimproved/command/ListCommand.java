package xyz.shirokuro.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.CandidateFactories;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class ListCommand extends Command {

    public ListCommand() {
        super("list", Args.builder().optional("world").build());
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        final String world = data.getArgs().getOrNull("world");
        final List<BlockPosition> positions = ScriptBlockImproved.getInstance().getScriptList().asUnmodifiableMap().keySet().stream()
            .filter(position -> world == null || world.equalsIgnoreCase(position.getWorld()))
            .sorted()
            .collect(Collectors.toList());
        if (positions.isEmpty()) {
            MessageUtils.sendMessage(sender, MessageKind.ERROR, "Empty");
            return;
        }
        int count = 0;
        for (final BlockPosition position : positions) {
            sendMessage(sender,
                "[%s] %s/%s/%s/%s",
                ++count,
                position.getWorld(),
                position.getX(),
                position.getY(),
                position.getZ());
        }
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.filter(value -> {
                return ScriptBlockImproved.getInstance().getScriptList().asUnmodifiableMap().keySet().stream()
                    .map(BlockPosition::getWorld)
                    .distinct()
                    .collect(Collectors.toList());
            }))
            .build(data.getArgName(), data.getCurrentValue());
    }
}
