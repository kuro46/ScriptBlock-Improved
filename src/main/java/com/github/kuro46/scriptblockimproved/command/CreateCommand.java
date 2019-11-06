package com.github.kuro46.scriptblockimproved.command;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionCreate;
import com.github.kuro46.scriptblockimproved.command.clickaction.ActionQueue;
import com.github.kuro46.scriptblockimproved.common.MessageKind;
import com.github.kuro46.scriptblockimproved.common.command.Args;
import com.github.kuro46.scriptblockimproved.common.command.CandidateBuilder;
import com.github.kuro46.scriptblockimproved.common.command.CandidateFactories;
import com.github.kuro46.scriptblockimproved.common.command.Command;
import com.github.kuro46.scriptblockimproved.common.command.CompletionData;
import com.github.kuro46.scriptblockimproved.common.command.ExecutionData;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerRegistry;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.github.kuro46.scriptblockimproved.common.MessageUtils.sendMessage;

public final class CreateCommand extends Command {

    @NonNull
    private final ActionQueue actionQueue;
    @NonNull
    private final TriggerRegistry triggerRegistry;

    public CreateCommand() {
        super(
            "create",
            Args.builder()
                .required("trigger")
                .required("options")
                .build()
        );
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        this.actionQueue = sbi.getActionQueue();
        this.triggerRegistry = sbi.getTriggerRegistry();
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
        final Player player = (Player) sender;
        sendMessage(sender, "Click any block to create script to the block");
        actionQueue.add(player, new ActionCreate(data.getArgs()));
    }

    @Override
    public List<String> complete(final CompletionData data) {
        return new CandidateBuilder()
            .when("trigger", CandidateFactories.filter(value -> {
                return triggerRegistry.getView().keySet().stream()
                    .map(TriggerName::toString)
                    .collect(Collectors.toList());
            }))
            .build(data.getArgName(), data.getCurrentValue());
    }
}
