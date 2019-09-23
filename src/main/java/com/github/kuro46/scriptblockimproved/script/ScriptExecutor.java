package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.script.option.CheckResult;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandler;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.Placeholders;
import com.github.kuro46.scriptblockimproved.script.trigger.Trigger;
import com.github.kuro46.scriptblockimproved.script.trigger.Triggers;
import java.util.Objects;
import org.bukkit.entity.Player;

public final class ScriptExecutor {

    private final Placeholders placeholders;
    private final OptionHandlers handlers;
    private final Scripts scripts;

    public ScriptExecutor(
            final Placeholders placeholders,
            final Scripts scripts,
            final OptionHandlers handlers,
            final Triggers triggers) {
        this.placeholders = Objects.requireNonNull(placeholders, "'placeholders' cannot be null");
        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
        this.handlers = Objects.requireNonNull(handlers, "'handlers' cannot be null");
        Objects.requireNonNull(triggers, "'triggers' cannot be null");

        triggers.addListener((trigger, event, player, coordinate) -> {
            execute(trigger, player, coordinate);
        });
    }

    private void execute(
            final Trigger triggerBy,
            final Player player,
            final BlockCoordinate coordinate) {
        Objects.requireNonNull(triggerBy, "'triggerBy' cannot be null");
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");

        if (!scripts.contains(coordinate)) {
            return;
        }
        scripts.get(coordinate).stream()
            .filter(script -> script.getTrigger().equals(triggerBy.getName()))
            .forEach(script -> executeScript(player, coordinate, script));
    }

    private void executeScript(
            final Player player,
            final BlockCoordinate coordinate,
            final Script script) {
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");
        Objects.requireNonNull(script, "'script' cannot be null");

        final Options replaced = script.getOptions().replaced(placeholders, player, coordinate);

        final boolean needCancel = replaced.stream()
            .anyMatch(option -> {
                final OptionHandler handler = handlers.getOrFail(option.getName());
                final CheckResult result = handler.check(
                        player,
                        script,
                        option);
                return result == CheckResult.CANCEL;
            });
        if (needCancel) return;

        replaced.forEach(option -> {
            final OptionName name = option.getName();
            final OptionHandler handler = handlers.getOrFail(name);
            handler.execute(player, script, option);
        });
    }
}
