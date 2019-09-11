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
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public final class ScriptExecutor {

    private final Placeholders placeholders;
    private final OptionHandlers handlers;
    private final Scripts scripts;
    private final Triggers triggers;
    private final Plugin plugin;

    public ScriptExecutor(
            final Placeholders placeholders,
            final Plugin plugin,
            final Scripts scripts,
            final OptionHandlers handlers,
            final Triggers triggers) {
        this.placeholders = Objects.requireNonNull(placeholders, "'placeholders' cannot be null");
        this.plugin = Objects.requireNonNull(plugin, "'plugin' cannot be null");
        this.scripts = Objects.requireNonNull(scripts, "'scripts' cannot be null");
        this.handlers = Objects.requireNonNull(handlers, "'handlers' cannot be null");
        this.triggers = Objects.requireNonNull(triggers, "'triggers' cannot be null");

        triggers.addListener(this::execute);
    }

    private void execute(
            final Trigger triggerBy,
            final Event event,
            final Player player,
            final BlockCoordinate coordinate) {
        Objects.requireNonNull(triggerBy, "'triggerBy' cannot be null");
        Objects.requireNonNull(event, "'event' cannot be null");
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");

        if (!scripts.contains(coordinate)) {
            return;
        }
        scripts.get(coordinate).stream()
            .filter(script -> script.getTrigger().equals(triggerBy.getName()))
            .forEach(script -> executeScript(event, player, coordinate, script));
    }

    private void executeScript(
            final Event event,
            final Player player,
            final BlockCoordinate coordinate,
            final Script script) {
        Objects.requireNonNull(event, "'event' cannot be null");
        Objects.requireNonNull(player, "'player' cannot be null");
        Objects.requireNonNull(coordinate, "'coordinate' cannot be null");
        Objects.requireNonNull(script, "'script' cannot be null");

        final Options replaced = script.getOptions().replaced(placeholders, player, coordinate);

        final boolean needCancel = replaced.stream()
            .anyMatch(option -> {
                final OptionHandler handler = handlers.getOrFail(option.getName());
                final CheckResult result = handler.check(
                        event,
                        player,
                        script,
                        option.getName(),
                        option.getArguments());
                return result == CheckResult.CANCEL;
            });
        if (needCancel) return;

        replaced.forEach(option -> {
            final OptionName name = option.getName();
            final OptionHandler handler = handlers.getOrFail(name);
            handler.execute(event, player, script, name, option.getArguments());
        });
    }
}
