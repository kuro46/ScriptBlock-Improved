package xyz.shirokuro.scriptblockimproved.command;

import com.github.kuro46.commandutility.Args;
import com.github.kuro46.commandutility.CandidateBuilder;
import com.github.kuro46.commandutility.CandidateFactories;
import com.github.kuro46.commandutility.Command;
import com.github.kuro46.commandutility.CompletionData;
import com.github.kuro46.commandutility.ExecutionData;
import com.github.kuro46.commandutility.ParsedArgs;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.ScriptList;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import java.util.List;
import org.bukkit.command.CommandSender;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;
import static xyz.shirokuro.scriptblockimproved.common.MessageUtils.sendMessage;

public final class RemoveAtCommand extends Command {

    public RemoveAtCommand() {
        super(
            "removeat",
            Args.builder()
                .required("world", "x", "y", "z")
                .build()
        );
    }

    @Override
    public void execute(final ExecutionData data) {
        final CommandSender sender = data.getDispatcher();
        final ParsedArgs args = data.getArgs();

        final BlockPosition position = BlockPosition.parseArgs(sender, args).orElse(null);
        if (position == null) {
            return;
        }

        final ScriptList scriptList = ScriptBlockImproved.getInstance().getScriptList();
        if (!scriptList.get(position).isEmpty()) {
            scriptList.remove(position);
            MessageUtils.sendMessage(sender, MessageKind.SUCCESS, "Script(s) has been removed");
        } else {
            MessageUtils.sendMessage(sender, MessageKind.ERROR, "Script not exists");
        }
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("world", CandidateFactories.worlds())
            .build(data.getArgName(), data.getCurrentValue());
    }
}
