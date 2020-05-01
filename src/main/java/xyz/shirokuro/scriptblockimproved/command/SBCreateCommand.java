package xyz.shirokuro.scriptblockimproved.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import xyz.shirokuro.commandutility.ExecutionData;
import xyz.shirokuro.commandutility.annotation.Executor;
import xyz.shirokuro.scriptblockimproved.Author;
import xyz.shirokuro.scriptblockimproved.BlockPosition;
import xyz.shirokuro.scriptblockimproved.OptionListParser;
import xyz.shirokuro.scriptblockimproved.Script;
import xyz.shirokuro.scriptblockimproved.ScriptBlockImproved;
import xyz.shirokuro.scriptblockimproved.command.migration.MigrationException;
import xyz.shirokuro.scriptblockimproved.command.migration.SBScriptLoader;
import xyz.shirokuro.scriptblockimproved.common.MessageKind;
import xyz.shirokuro.scriptblockimproved.common.MessageUtils;

import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class SBCreateCommand {

    @Executor(command = "sbinteract create <script>", description = "TODO")
    public void executeInteract(final ExecutionData data) {
        handle(data, "sbinteract");
    }

    @Executor(command = "sbwalk create <script>", description = "TODO")
    public void executeWalk(final ExecutionData data) {
        handle(data, "sbwalk");
    }

    private void handle(final ExecutionData data, final String trigger) {
        final Player player = data.getSenderAsPlayer();
        if (player == null) {
            MessageUtils.sendMessage(data.getSender(), MessageKind.ERROR, "Cannot perform this command from the console");
            return;
        }

        final ImmutableList<Script.Option> options;
        try {
            ImmutableList.Builder<Script.Option> builder = ImmutableList.builder();
            for (final String option : OptionListParser.stringOptions(data.get("script"))) {
                builder.add(SBScriptLoader.convertToSBIOption(SBScriptLoader.parseOption(option)));
            }
            options = builder.build();
        } catch (final MigrationException e) {
            MessageUtils.sendMessage(player, MessageKind.ERROR, e.getMessage());
            return;
        }

        final Script script = Script.builder()
            .author(Author.player(player))
            .createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
            .triggerName(trigger)
            .options(options)
            .build();

        MessageUtils.sendMessage(player, "Click any block to create script to the block");
        ScriptBlockImproved.getInstance().getActionQueue().queue(player, location -> {
            final BlockPosition position = BlockPosition.ofLocation(location);
            ScriptBlockImproved.getInstance().getScriptList().removeAll(position);
            ScriptBlockImproved.getInstance().getScriptList().add(position, script);
            MessageUtils.sendMessage(player, MessageKind.SUCCESS, "The script has been created");
        });
    }

}
