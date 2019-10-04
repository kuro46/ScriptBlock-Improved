package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.script.option.OptionHandler;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.option.PreExecuteResult;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.Placeholders;
import com.github.kuro46.scriptblockimproved.script.trigger.Trigger;
import com.github.kuro46.scriptblockimproved.script.trigger.Triggers;
import java.util.Objects;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class ScriptExecutor {

    private final Placeholders placeholders;
    private final OptionHandlers handlers;
    private final Scripts scripts;

    private ScriptExecutor(
            final Placeholders placeholders,
            final Scripts scripts,
            final OptionHandlers handlers,
            final Triggers triggers) {
        this.placeholders = Objects.requireNonNull(placeholders, "'placeholders' cannot be null");
        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
        this.handlers = Objects.requireNonNull(handlers, "'handlers' cannot be null");
        Objects.requireNonNull(triggers, "'triggers' cannot be null");

        triggers.addListener((trigger, event, player, position) -> {
            execute(trigger, player, position);
        });
    }

    public static ScriptExecutor init(
            @NonNull final Placeholders placeholders,
            @NonNull final Scripts scripts,
            @NonNull final OptionHandlers handlers,
            @NonNull final Triggers triggers) {
        return new ScriptExecutor(placeholders, scripts, handlers, triggers);
    }

    private void execute(
            final Trigger triggerBy,
            final Player player,
            final BlockPosition position) {
        Objects.requireNonNull(triggerBy, "'triggerBy' cannot be null");
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(position, "'position' cannot be null");

        if (!scripts.contains(position)) {
            return;
        }
        scripts.get(position).stream()
            .filter(script -> script.getTrigger().equals(triggerBy.getName()))
            .forEach(script -> executeScript(player, position, script));
    }

    private void executeScript(
            final Player player,
            final BlockPosition position,
            final Script script) {
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(position, "'position' cannot be null");
        Objects.requireNonNull(script, "'script' cannot be null");

        final Options replaced = script.getOptions().replaced(placeholders, player, position);

        final boolean needCancel = replaced.stream()
            .anyMatch(option -> {
                final OptionHandler handler = handlers.getOrFail(option.getName());
                final PreExecuteResult result = handler.preExecute(
                        player,
                        script,
                        option);
                return result == PreExecuteResult.CANCEL;
            });
        if (needCancel) return;

        replaced.forEach(option -> {
            final OptionName name = option.getName();
            final OptionHandler handler = handlers.getOrFail(name);
            handler.execute(player, script, option);
        });
    }
}
