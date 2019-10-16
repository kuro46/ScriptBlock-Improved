package com.github.kuro46.scriptblockimproved.script;

import com.github.kuro46.scriptblockimproved.ScriptBlockImproved;
import com.github.kuro46.scriptblockimproved.script.option.ExecutionData;
import com.github.kuro46.scriptblockimproved.script.option.Option;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandler;
import com.github.kuro46.scriptblockimproved.script.option.OptionHandlers;
import com.github.kuro46.scriptblockimproved.script.option.OptionName;
import com.github.kuro46.scriptblockimproved.script.option.Options;
import com.github.kuro46.scriptblockimproved.script.option.PreExecuteResult;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.PlaceholderGroup;
import com.github.kuro46.scriptblockimproved.script.option.placeholder.SourceData;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerName;
import com.github.kuro46.scriptblockimproved.script.trigger.TriggerRegistry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class ScriptExecutor {

    private static final Lock INIT_LOCK = new ReentrantLock();

    private volatile static ScriptExecutor instance;

    private final TriggerRegistry triggerRegistry;
    private final PlaceholderGroup placeholderGroup;
    private final OptionHandlers handlers;
    private final Scripts scripts;

    private ScriptExecutor() {
        final ScriptBlockImproved sbi = ScriptBlockImproved.getInstance();
        this.triggerRegistry = sbi.getTriggerRegistry();
        this.placeholderGroup = sbi.getPlaceholderGroup();
        this.scripts = sbi.getScripts();
        this.handlers = sbi.getOptionHandlers();
    }

    public static void init() {
        INIT_LOCK.lock();
        try {
            if (instance != null) {
                throw new IllegalStateException("ScriptExecutor already initialized");
            }
            instance = new ScriptExecutor();
        } finally {
            INIT_LOCK.unlock();
        }
    }

    public static ScriptExecutor getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ScriptExecutor hasn't initialized yet");
        }
        return instance;
    }

    public void execute(
            @NonNull final TriggerName triggerName,
            @NonNull final Player player,
            @NonNull final BlockPosition position) {
        if (!triggerRegistry.isRegistered(triggerName)) {
            final String message = String.format("Trigger: '%s' is not registered", triggerName);
            throw new IllegalStateException(message);
        }
        if (!scripts.contains(position)) return;
        scripts.get(position).stream()
            .filter(script -> script.getTriggerName().equals(triggerName))
            .forEach(script -> execute(script, player));
    }

    public void execute(
            @NonNull final Script script,
            @NonNull final Player player) {
        final SourceData sourceData = SourceData.builder()
            .position(script.getPosition())
            .player(player)
            .build();
        final Options replaced = script.getOptions().replaced(placeholderGroup, sourceData);
        // Stop execution if needed
        for (final Option option : replaced.getView()) {
            final PreExecuteResult result = preExecuteOption(option, player, script);
            if (result == PreExecuteResult.CANCEL) return;
        }
        // Execute all scripts
        replaced.forEach(option -> executeOption(option, player, script));
    }

    private PreExecuteResult preExecuteOption(
            @NonNull final Option option,
            @NonNull final Player player,
            @NonNull final Script script) {
        final OptionHandler handler = handlers.getOrFail(option.getName());
        final ExecutionData data = ExecutionData.builder()
            .player(player)
            .script(script)
            .option(option)
            .build();
        return handler.preExecute(data);
    }

    private void executeOption(
            @NonNull final Option option,
            @NonNull final Player player,
            @NonNull final Script script) {
        final OptionName name = option.getName();
        final OptionHandler handler = handlers.getOrFail(name);
        final ExecutionData data = ExecutionData.builder()
            .player(player)
            .script(script)
            .option(option)
            .build();
        handler.execute(data);
    }
}
