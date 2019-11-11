package com.github.kuro46.scriptblockimproved.script.trigger;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.BlockPosition;
import com.github.kuro46.scriptblockimproved.script.Script;
import com.github.kuro46.scriptblockimproved.script.option.ExecutionData;
import com.github.kuro46.scriptblockimproved.script.option.Option;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandler;
import com.github.kuro46.scriptblockimproved.script.option.OptionList;
import com.github.kuro46.scriptblockimproved.script.option.PreExecuteResult;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.SourceData;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

public abstract class Trigger<E extends Event> implements EventExecutor {

    private final Listener listener = new Listener() {
    };

    @NonNull
    @Getter
    private final Class<E> eventClass;
    @NonNull
    @Getter
    private final TriggerName name;

    public Trigger(
        @NonNull final Plugin plugin,
        @NonNull final Class<E> eventClass,
        @NonNull final String name
    ) {
        this(plugin, eventClass, TriggerName.of(name));
    }

    public Trigger(
        @NonNull final Plugin plugin,
        @NonNull final Class<E> eventClass,
        @NonNull final TriggerName name
    ) {
        this.eventClass = eventClass;
        this.name = name;
        ScriptBlockImproved.getInstance().getTriggerRegistry().register(this);
        hookBukkit(plugin);
    }

    private void hookBukkit(@NonNull final Plugin plugin) {
        Bukkit.getPluginManager().registerEvent(
            eventClass,
            listener,
            EventPriority.NORMAL,
            this,
            plugin
        );
    }

    // For TriggerRegistory#unregister
    void unhookBukkit() {
        HandlerList.unregisterAll(listener);
    }

    /**
     * Validates condition of the event.<br>
     * This method should be called first. (after event is fired)
     *
     * @param event An event to validate
     * @return {@code true} if specified event is valid, otherwise {@code false}
     */
    public abstract boolean validateCondition(E event);

    /**
     * Retrieves a {@code BlockPosition} from specified event.
     * This method should be called second. (after {@code validateCondition} is called)
     *
     * @param event An event to retrieve
     * @return A BlockPosition that must be non-null
     */
    public abstract BlockPosition retrievePosition(E event);

    /**
     * Retrieves a {@code Player} from specified event.
     * This method should be called third. (after {@code retrievePosition} is called)
     */
    public abstract Player retrievePlayer(E event);

    /**
     * Returns a bool that represents whether to suppress script execution.<br>
     * This method should be called third. (after {@code retrievePlayer} is called)
     *
     * @param event An event to check
     * @return {@code true} if should suppress script execution, otherwise {@code false}
     */
    public abstract boolean shouldSuppress(E event, BlockPosition position, Player player);

    @Override
    public final void execute(@NonNull final Listener listener, @NonNull final Event rawEvent) {
        @SuppressWarnings("unchecked")
        final E event = (E) rawEvent;
        if (!validateCondition(event)) {
            return;
        }
        // Retrieve necessary data
        final BlockPosition position = retrievePosition(event);
        Objects.requireNonNull(position, "'retrievePosition' must NOT be return 'null'");
        final Player player = retrievePlayer(event);
        Objects.requireNonNull(player, "'retrievePlayer' must NOT be return 'null'");
        // Find scripts in the retrieved position
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        final ImmutableList<Script> scripts = sbi.getScripts().get(position).stream()
            .filter(script -> script.getTriggerName().equals(name))
            .collect(ImmutableList.toImmutableList());
        // Call OptionHandler#onTriggered
        fireOnTriggered(event, scripts);
        // Suppress or dont
        if (shouldSuppress(event, position, player)) {
            return;
        }
        final SourceData sourceData = SourceData.builder()
            .position(position)
            .player(player)
            .build();
        for (final Script script : scripts) {
            executeScript(sourceData, player, script);
        }
    }

    private final void fireOnTriggered(
        @NonNull final Event event,
        @NonNull final List<Script> scripts
    ) {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        for (final Script script : scripts) {
            for (final Option option : script.getOptions().asList()) {
                final OptionHandler handler = sbi.getOptionHandlers()
                    .getOrFail(option.getName());
                handler.onTriggered(event);
            }
        }
    }

    private final void executeScript(
        @NonNull final SourceData sourceData,
        @NonNull final Player player,
        @NonNull final Script script
    ) {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        final OptionList replacedOptions = script.getOptions()
            .replacePlaceholders(sbi.getPlaceholderGroup(), sourceData);
        for (final Option option : replacedOptions.asList()) {
            final ExecutionData executionData = ExecutionData.builder()
                .player(player)
                .script(script)
                .option(option)
                .build();
            final OptionHandler handler = sbi.getOptionHandlers()
                .getOrFail(option.getName());
            final PreExecuteResult result = handler.preExecute(executionData);
            if (result == PreExecuteResult.CANCEL) {
                return;
            }
        }
        for (final Option option : replacedOptions.asList()) {
            final ExecutionData executionData = ExecutionData.builder()
                .player(player)
                .script(script)
                .option(option)
                .build();
            final OptionHandler handler = sbi.getOptionHandlers()
                .getOrFail(option.getName());
            handler.execute(executionData);
        }
    }
}
